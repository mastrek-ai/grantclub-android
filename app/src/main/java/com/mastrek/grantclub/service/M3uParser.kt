package com.mastrek.grantclub.service

import com.mastrek.grantclub.data.Channel
import java.util.UUID

object M3uParser {

    fun parse(content: String): List<Channel> {
        val channels = mutableListOf<Channel>()
        val lines    = content.lines()
        var i        = 0
        while (i < lines.size) {
            val line = lines[i].trim()
            if (line.startsWith("#EXTINF")) {
                val name    = extractAttr(line, "tvg-name")
                    ?: line.substringAfterLast(",").trim()
                val logoUrl = extractAttr(line, "tvg-logo")
                val group   = extractAttr(line, "group-title")
                val url     = lines.getOrNull(i + 1)?.trim() ?: ""
                if (url.isNotEmpty() && !url.startsWith("#")) {
                    channels.add(Channel(
                        id      = UUID.randomUUID().toString(),
                        name    = name,
                        url     = url,
                        logoUrl = logoUrl,
                        group   = group
                    ))
                    i += 2
                    continue
                }
            }
            i++
        }
        return channels
    }

    private fun extractAttr(line: String, attr: String): String? {
        val pattern = Regex("""$attr="([^"]*)"""")
        return pattern.find(line)?.groupValues?.get(1)?.takeIf { it.isNotEmpty() }
    }
}
