package com.example.urlshortener.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.sql.DriverManager

@Service
class IpRateLimiter {

    companion object {
        private val logger = LoggerFactory.getLogger(IpRateLimiter::class.java)
    }

    fun isAllowed(ipAddress: String, maxRequests: Int = 300): Boolean {
        // Connect to the database and retrieve the number of requests made by the IP address
        val connection = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5432/url_short",
            "url_short",
            "url_short"
        )
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery("SELECT count FROM url_statistics WHERE ip_address = '$ipAddress' AND timestamps >= (NOW() - INTERVAL '1 hour')")
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