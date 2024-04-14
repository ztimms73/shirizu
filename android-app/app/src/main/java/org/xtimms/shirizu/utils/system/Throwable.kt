package org.xtimms.shirizu.utils.system

import android.content.ActivityNotFoundException
import android.content.res.Resources
import coil.network.HttpException
import okio.FileNotFoundException
import org.jsoup.HttpStatusException
import org.koitharu.kotatsu.parsers.exception.AuthRequiredException
import org.koitharu.kotatsu.parsers.exception.ContentUnavailableException
import org.koitharu.kotatsu.parsers.exception.NotFoundException
import org.koitharu.kotatsu.parsers.exception.ParseException
import org.xtimms.shirizu.R
import org.xtimms.shirizu.core.exceptions.CloudflareProtectedException
import org.xtimms.shirizu.core.exceptions.SyncApiException
import org.xtimms.shirizu.core.exceptions.TooManyRequestExceptions
import org.xtimms.shirizu.utils.lang.ifNullOrEmpty
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.getDisplayMessage(resources: Resources): String = when (this) {
    is AuthRequiredException -> resources.getString(R.string.auth_required)
    is CloudflareProtectedException -> resources.getString(R.string.captcha_required)
    is ActivityNotFoundException,
    is UnsupportedOperationException,
    -> resources.getString(R.string.operation_not_supported)

    is TooManyRequestExceptions -> resources.getString(R.string.too_many_requests_message)
    is FileNotFoundException -> resources.getString(R.string.file_not_found)
    is AccessDeniedException -> resources.getString(R.string.no_access_to_file)
    is SyncApiException,
    is ContentUnavailableException,
    -> message

    is ParseException -> shortMessage
    is UnknownHostException,
    is SocketTimeoutException,
    -> resources.getString(R.string.network_error)

    is NotFoundException -> resources.getString(R.string.not_found_404)

    is HttpException -> getHttpDisplayMessage(response.code, resources)
    is HttpStatusException -> getHttpDisplayMessage(statusCode, resources)

    else -> localizedMessage
}.ifNullOrEmpty {
    resources.getString(R.string.error_occured)
}

private fun getHttpDisplayMessage(statusCode: Int, resources: Resources): String? = when (statusCode) {
    404 -> resources.getString(R.string.not_found_404)
    in 500..599 -> resources.getString(R.string.server_error, statusCode)
    else -> null
}