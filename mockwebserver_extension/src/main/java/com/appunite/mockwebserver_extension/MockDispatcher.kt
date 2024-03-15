package com.appunite.mockwebserver_extension

import com.appunite.mockwebserver_extension.util.MultipleFailuresError
import com.appunite.mockwebserver_extension.util.ResponseGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

private const val TAG = "MockDispatcher"
private val logger = KotlinLogging.logger {}

class MockDispatcher : Dispatcher() {

    data class Mock(val response: ResponseGenerator)

    private val mocks: MutableList<Mock> = mutableListOf()
    val errors: MutableList<Throwable> = mutableListOf()

    fun register(response: ResponseGenerator) {
        mocks.add(Mock(response))
    }

    fun clear() {
        mocks.clear()
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        try {
            val mockRequest = try {
                Request(
                    url = (
                            request.getHeader("X-Test-Original-Url")
                                ?: throw Exception("No X-Test-Original-Url header, problem with mocker")
                            ).toHttpUrl(),
                    headers = request.headers.newBuilder().removeAll("X-Test-Original-Url").build(),
                    method = request.method ?: throw Exception("Nullable method in the request"),
                    body = request.body
                )
            } catch (e: Exception) {
                throw Exception("Request: $request, is incorrect", e)
            }
            return runMocks(mockRequest)
        } catch (e: Throwable) {
            errors.add(e)
            logger.warn { TAG + e.message!! }
            return MockResponse().setResponseCode(404)
        }
    }

    private fun runMocks(mockRequest: Request): MockResponse {
        val assertionErrors = buildList {
            mocks.forEach {
                try {
                    return it.response(mockRequest)
                } catch (e: AssertionError) {
                    add(e)
                }
            }
        }
        throw MultipleFailuresError(
            "Request: ${mockRequest.method} ${mockRequest.url}, " + if (assertionErrors.isEmpty()) "there are no mocks" else "no mock is matching the request",
            assertionErrors
        )
    }
}
