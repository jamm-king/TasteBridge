package com.jammking.tastebridge.util

import java.security.MessageDigest

object HashUtil {
    fun sha256Hex(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray())

        return bytes.joinToString("") { "%02x".format(it) }
    }
}