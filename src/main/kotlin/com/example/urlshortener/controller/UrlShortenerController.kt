package com.example.urlshortener.controller

import com.example.urlshortener.service.IpRateLimiter
import com.example.urlshortener.service.UrlShortenerService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import java.sql.Connection
import java.sql.DriverManager

enum class UrlStatus {
    Active,
    Disabled
}
@RestController
@CrossOrigin
class UrlShortenerController @Autowired constructor(
    val urlShortenerService: UrlShortenerService,
    val ipRateLimiter: IpRateLimiter,
) {

    private fun getConnection(): Connection {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/url_short", "url_short", "url_short")
    }

    @GetMapping("/urls")
    fun getAllUrls(): List<Map<String, Any>> {
        val rows = mutableListOf<Map<String, Any>>()
        try {
            Class.forName("org.postgresql.Driver")
            val connection = getConnection()
            val statement = connection.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM url_shortener")
            while (resultSet.next()) {
                val row = mutableMapOf<String, Any>()
                for (i in 1..resultSet.metaData.columnCount) {
                    val columnName = resultSet.metaData.getColumnName(i)
                    val columnValue = resultSet.getObject(i)
                    row[columnName] = columnValue
                }
                rows.add(row)
            }
            connection.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return rows
    }

    @PostMapping("/shorten")
    fun shortenUrl(@RequestParam("url") url: String, request: HttpServletRequest): String {
        // Generate the short URL by creating a hash of the original URL
        val ipAddress = request.remoteAddr
        println("shortenUrl $url $ipAddress")
        if (!ipRateLimiter.tryAcquire(ipAddress)) {
            return "Too many request"
        }
        val shortUrl = urlShortenerService.generateShortUrl(url)

        // Connect to the database and insert a new row with the original and shortened URLs
        val connection = getConnection()
        val statement = connection.createStatement()
        statement.executeUpdate("INSERT INTO url_shortener (original_url, short_url) VALUES ('$url', '$shortUrl')")
        connection.close()

        return shortUrl
        // Return the short URL to the user
    }

    @GetMapping("/{shortUrl}")
    fun getOriginalUrl(@PathVariable("shortUrl") shortUrl: String, request: HttpServletRequest): String {
        // Log the user's IP address
        val ipAddress = request.remoteAddr
        println("getOriginalUrl $shortUrl $ipAddress")
        if (!ipRateLimiter.tryAcquire(request.remoteAddr)) {
            return "Too many request"
        }
        println("User with IP address $ipAddress called the /original endpoint")

        // Connect to the database and insert a new row with the IP address, URL, and count
        val connection = getConnection()
        val statement = connection.createStatement()
        statement.executeUpdate("INSERT INTO url_statistics (ip_address, url, count) VALUES ('$ipAddress', '$shortUrl', 1) ON CONFLICT (ip_address, url) DO UPDATE SET count = url_statistics.count + 1")
        val resultSet = statement.executeQuery("SELECT original_url FROM url_shortener WHERE short_url = '$shortUrl'")
        if (!resultSet.next()) {
            connection.close()
            return "Not found"
        }
        val originalUrl = resultSet.getString("original_url")
        connection.close()

        // Return the original URL to the user
        return originalUrl
    }

    @GetMapping("/statistics")
    fun getUrlStatistics(request: HttpServletRequest): List<Pair<String, Int>> {
        println("getUrlStatistics  ${request.remoteAddr}")
        if (!ipRateLimiter.tryAcquire(request.remoteAddr)) {
            return listOf(Pair("Too many request", HttpStatus.TOO_MANY_REQUESTS.ordinal))
        }
        // Connect to the database and retrieve the number of times each URL has been called
        val connection = getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT url, SUM(count) AS count FROM url_statistics GROUP BY url")
        val statistics = mutableListOf<Pair<String, Int>>()
        while (resultSet.next()) {
            val url = resultSet.getString("url")
            val count = resultSet.getInt("count")
            statistics.add(Pair(url, count))
        }
        val resultSet2 = statement.executeQuery("SELECT ip_address, SUM(count) AS count FROM url_statistics GROUP BY ip_address")
        while (resultSet2.next()) {
            val url = resultSet2.getString("ip_address")
            val count = resultSet2.getInt("count")
            statistics.add(Pair(url, count))
        }
        connection.close()

        // Return the statistics to the user
        return statistics
    }

    @PostMapping("/mark/{urlId}")
    fun markUrl(@PathVariable("urlId") urlId: Int, @RequestParam("status") status: String, request: HttpServletRequest): String {
        println("markUrl  $urlId ${request.remoteAddr} $status")
        if (!ipRateLimiter.tryAcquire(request.remoteAddr)) {
            return "Too many request"
        }
        if (status != UrlStatus.Disabled.ordinal.toString() && status != UrlStatus.Active.ordinal.toString()) {
            return "Invalid status $status valid 0|1"
        }
        // Connect to the database and update the URL's status
        val connection = getConnection()
        val sql = "UPDATE url_shortener SET status = ? WHERE id = ?"
        val pstmt = connection.prepareStatement(sql)
        pstmt.setString(1, status)
        pstmt.setInt(2, urlId)
        val rowAffected = pstmt.executeUpdate()
        connection.close()
        return isSuccessUpdate(rowAffected)
    }

    @DeleteMapping("/delete")
    fun deleteUrl(@RequestParam("url") url: Int, request: HttpServletRequest): String {
        println("markUrl  $url ${request.remoteAddr}")
        if (!ipRateLimiter.tryAcquire(request.remoteAddr)) {
            return "Too many request"
        }
        // Connect to the database and delete the URL
        val connection = getConnection()
        val sql = "DELETE FROM url_shortener WHERE id = ?"
        val pstmt = connection.prepareStatement(sql)
        pstmt.setInt(1, url)
        val rowAffected = pstmt.executeUpdate()
        connection.close()
        return isSuccessUpdate(rowAffected)
    }

    private fun isSuccessUpdate(rowAffected: Int): String = if (rowAffected != 1) "failed" else "ok"
}
