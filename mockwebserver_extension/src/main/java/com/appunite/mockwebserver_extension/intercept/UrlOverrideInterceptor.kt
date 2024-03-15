package com.appunite.mockwebserver_extension.intercept

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response

class UrlOverrideInterceptor(private val baseUrl: HttpUrl) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newUrl = request.url.newBuilder()
            .host(baseUrl.host)
            .scheme(baseUrl.scheme)
            .port(baseUrl.port)
            .build()
        return chain.proceed(
            request.newBuilder().url(newUrl)
                .addHeader("X-Test-Original-Url", request.url.toString()).build()
        )
    }
}
