package com.example.urlshortener.service

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.math.BigInteger
import java.security.MessageDigest

@Service
@Qualifier("urlShortenerService")
class UrlShortenerService {
    fun generateShortUrl(url: String): String {
        // Create a MessageDigest instance using the SHA-1 algorithm
        val digest = MessageDigest.getInstance("SHA-1")

        // Compute the hash of the URL
        val hash = digest.digest(url.toByteArray())

        // Convert the hash to a hexadecimal string
        val hashString = BigInteger(1, hash).toString(16)

        // Return the short URL by concatenating a prefix with the hexadecimal string
        return hashString
    }
}