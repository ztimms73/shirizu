package org.xtimms.shirizu.utils

import android.content.Context
import org.xtimms.shirizu.R
import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

enum class FileSize(private val multiplier: Int) {

    BYTES(1), KILOBYTES(1024), MEGABYTES(1024 * 1024);

    fun convert(amount: Long, target: FileSize): Long = amount * multiplier / target.multiplier

    fun freeFormat(context: Context, amount: Float): String {
        val bytes = amount * multiplier
        val units = context.getString(R.string.text_file_sizes_free).split('|')
        if (bytes <= 0) {
            return "0 ${units.first()}"
        }
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return buildString {
            append(
                DecimalFormat("#,##0.#").format(
                    bytes / 1024.0.pow(digitGroups.toDouble()),
                ),
            )
            val unit = units.getOrNull(digitGroups)
            if (unit != null) {
                append(' ')
                append(unit)
            }
        }
    }

    fun totalFormat(context: Context, amount: Float): String {
        val bytes = amount * multiplier
        val units = context.getString(R.string.text_file_sizes_total).split('|')
        if (bytes <= 0) {
            return "0 ${units.first()}"
        }
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return buildString {
            append(
                DecimalFormat("#,##0.#").format(
                    bytes / 1024.0.pow(digitGroups.toDouble()),
                ),
            )
            val unit = units.getOrNull(digitGroups)
            if (unit != null) {
                append(' ')
                append(unit)
            }
        }
    }

    fun showUnit(context: Context, amount: Float): String {
        val bytes = amount * multiplier
        val units = context.getString(R.string.text_file_sizes_used).split('|')
        if (bytes <= 0) {
            return units.first()
        }
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return buildString {
            val unit = units.getOrNull(digitGroups)
            if (unit != null) {
                append(' ')
                append(unit)
            }
        }
    }

    fun format(context: Context, amount: Float): String {
        val bytes = amount * multiplier
        val units = context.getString(R.string.text_file_sizes).split('|')
        if (bytes <= 0) {
            return "0 ${units.first()}"
        }
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return buildString {
            append(
                DecimalFormat("#,##0.#").format(
                    bytes / 1024.0.pow(digitGroups.toDouble()),
                ),
            )
            val unit = units.getOrNull(digitGroups)
            if (unit != null) {
                append(' ')
                append(unit)
            }
        }
    }

    fun formatWithoutUnits(amount: Float): String {
        val bytes = amount * multiplier
        if (bytes <= 0) {
            return "0"
        }
        val digitGroups = (log10(bytes.toDouble()) / log10(1024.0)).toInt()
        return buildString {
            append(
                DecimalFormat("#,##0.#").format(
                    bytes / 1024.0.pow(digitGroups.toDouble()),
                ),
            )
        }
    }
}