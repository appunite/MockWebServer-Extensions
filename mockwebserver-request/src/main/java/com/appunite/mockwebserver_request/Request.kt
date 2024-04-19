package com.appunite.mockwebserver_request

import okhttp3.Headers
import okhttp3.HttpUrl
import okio.Buffer

class Request(
    val headers: Headers,
    val method: String,
    val url: HttpUrl,
    val body: Buffer
) {
    override fun toString(): String =
        "Request(method=$method, url=$url, headers=${headers.joinToString(separator = ",") { (key, value) -> "$key: $value" }})"
}
