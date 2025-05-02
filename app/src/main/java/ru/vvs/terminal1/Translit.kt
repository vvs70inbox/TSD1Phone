package ru.vvs.terminal1

import java.util.Locale

class Translit {


    companion object {
        fun cyr2lat(ch: Char, chBack: String): String {
            return when (ch) {
                'А' -> "A"
                'Б' -> "B"
                'В' -> "V"
                'Г' -> "G"
                'Д' -> "D"
                'Е' -> "E"
                'Ё' -> "YE"
                'Ж' -> "ZH"
                'З' -> "Z"
                'И' -> "I"
                'Й' -> "Y"
                'К' -> "K"
                'Л' -> "L"
                'М' -> "M"
                'Н' -> "N"
                'О' -> "O"
                'П' -> "P"
                'Р' -> "R"
                'С' -> "S"
                'Т' -> "T"
                'У' -> "U"
                'Ф' -> "F"
                'Х' -> "KH"
                'Ц' -> "TS"
                'Ч' -> "CH"
                'Ш' -> "SH"
                'Щ' -> "SHCH"
                'Ъ' -> ""
                'Ы' -> "Y"
                'Ь' -> ""
                'Э' -> "E"
                'Ю' -> "YU"
                'Я' -> "YA"
                in 'A'..'Z' -> ch.toString()
                in '0'..'9' -> ch.toString()
                else -> if (chBack!="_") "_" else ""
            }
        }

        fun Cyr2Lat(s: String): String {
            val sb = StringBuilder(s.length * 2)
            var chBack: String = ""
            for (ch in s.toCharArray()) {
                val upCh: Char = ch
                val lat = cyr2lat(upCh.uppercaseChar(), chBack)
                chBack = if (lat.isEmpty() && ch!='ь' && ch!='ъ')  "_" else lat
                sb.append(lat)
            }
            return if (s.last() == '`')
                sb.toString().dropLast(1).lowercase()
            else
                sb.toString().lowercase()
        }
    }

}