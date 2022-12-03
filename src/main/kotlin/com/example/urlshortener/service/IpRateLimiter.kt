package com.example.urlshortener.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.sql.Connection
import java.sql.DriverManager

@Service
class IpRateLimiter {
    companion object {
        private val logger = LoggerFactory.getLogger(IpRateLimiter::class.java)
    }

    private fun getConnection(): Connection {
        val host = "au77784bkjx6ipju.cbetxkdyhwsb.us-east-1.rds.amazonaws.com"
        val user = "zlhlkevzdggbg2j6"
        val password = "kxzbaikp2rk7q2cv"
        val db = "ws9bhzyzyhnc8jtb"
        return DriverManager.getConnection("jdbc:mysql://$host:3306/$db", user, password)
    }
    fun isAllowed(ipAddress: String, maxRequests: Int = 300): Boolean {
        // Connect to the database and retrieve the number of requests made by the IP address
        val connection = getConnection()
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT count FROM url_statistics WHERE ip_address = '$ipAddress' AND timestamps >= (NOW() - INTERVAL 1 HOUR)")
        var count = 0
        if (resultSet.next()) {
            count = resultSet.getInt("count")
        }
        connection.close()

        // Check if the IP address is allowed to make a request
        return count < maxRequests
    }

    fun tryAcquire(ip: String): Boolean {
        val isAllowed = isAllowed(ip)
        if (!isAllowed) {
            logger.warn("Request from IP address {} has been blocked due to rate limiting", ip)
        }
        return isAllowed
    }
}