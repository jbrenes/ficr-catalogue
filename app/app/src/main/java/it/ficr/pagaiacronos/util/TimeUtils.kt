package it.ficr.pagaiacronos.util

object TimeUtils {

    private val NON_TIME = setOf("NP", "NA", "SQ", "---", "")
    private val MINUTE_RE = Regex("""^(\d+)'(\d+)\.(\d+)$""")
    private val SECOND_RE = Regex("""^(\d+)\.(\d+)$""")

    /** Converts FICR time strings like "1'53.75" or "53.75" to milliseconds. */
    fun parseTimeMs(raw: String?): Long? {
        val s = raw?.trim() ?: return null
        if (s in NON_TIME) return null

        MINUTE_RE.matchEntire(s)?.let { m ->
            val mins = m.groupValues[1].toLong()
            val secs = m.groupValues[2].toLong()
            val cents = m.groupValues[3].padEnd(2, '0').take(2).toLong()
            return (mins * 60 + secs) * 1000L + cents * 10L
        }

        SECOND_RE.matchEntire(s)?.let { m ->
            val secs = m.groupValues[1].toLong()
            val cents = m.groupValues[2].padEnd(2, '0').take(2).toLong()
            return secs * 1000L + cents * 10L
        }

        return null
    }

    /** Formats milliseconds to "M'SS.cc" or "SS.cc". */
    fun formatTimeMs(ms: Long?): String {
        ms ?: return "--"
        val totalSecs = ms / 1000L
        val cents = (ms % 1000L) / 10L
        val mins = totalSecs / 60L
        val secs = totalSecs % 60L
        return if (mins > 0) "%d'%02d.%02d".format(mins, secs, cents)
        else "%d.%02d".format(secs, cents)
    }

    /** Formats gap milliseconds as "+M'SS.cc" or "" for the leader. */
    fun formatGapMs(ms: Long?): String {
        if (ms == null || ms <= 0L) return ""
        return "+${formatTimeMs(ms)}"
    }

    /** Parses FICR date "DD/MM/YYYY" to ISO "YYYY-MM-DD". */
    fun fickDateToIso(date: String?): String? {
        val d = date?.trim() ?: return null
        val parts = d.split("/")
        if (parts.size != 3) return null
        return "${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}"
    }

    /** Returns epoch days for a YYYY-MM-DD date string (for chart x-axis). */
    fun isoDateToEpochDay(iso: String): Long? {
        return try {
            val parts = iso.split("-")
            if (parts.size != 3) return null
            val y = parts[0].toLong()
            val m = parts[1].toLong()
            val d = parts[2].toLong()
            // Simple approximation good enough for relative chart positioning
            y * 365L + m * 30L + d
        } catch (_: Exception) { null }
    }
}
