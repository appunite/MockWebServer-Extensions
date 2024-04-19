package com.appunite.mockwebserverextensions.util

import com.appunite.mockwebserver_request.Request
import okhttp3.mockwebserver.MockResponse
import org.intellij.lang.annotations.Language

typealias ResponseGenerator = (Request) -> MockResponse

fun jsonResponse(@Language("JSON") json: String): MockResponse = MockResponse()
    .addHeader("Content-Type", "application/json")
    .setBody(json.trimIndent())
