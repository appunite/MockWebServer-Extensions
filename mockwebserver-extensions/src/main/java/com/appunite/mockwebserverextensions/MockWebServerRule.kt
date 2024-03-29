package com.appunite.mockwebserverextensions

import com.appunite.mockwebserver_interceptor.TestInterceptor
import com.appunite.mockwebserverextensions.intercept.UrlOverrideInterceptor
import com.appunite.mockwebserverextensions.util.ResponseGenerator
import okhttp3.Interceptor
import okhttp3.internal.closeQuietly
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.MultipleFailureException
import java.util.logging.Logger

open class MockWebServerRule(
    private val interceptor: Interceptor? = null
) : TestWatcher(), MockRegistry {

    companion object {
        val LOG: Logger = Logger.getLogger(MockWebServerRule::class.java.name)
    }

    private var mockDispatcher: MockDispatcher = MockDispatcher()
    private var mockWebServer: MockWebServer? = null

    override fun register(response: ResponseGenerator) = mockDispatcher.register(response)

    override fun clear() = mockDispatcher.clear()

    override fun starting(description: Description?) {
        val server = MockWebServer()
        server.dispatcher = wrapDispatcher(mockDispatcher)
        TestInterceptor.testInterceptor = interceptor ?: UrlOverrideInterceptor(server.url("/"))
        LOG.info("TestInterceptor installed")
        mockWebServer = server
    }

    override fun finished(description: Description) {
        LOG.info("TestInterceptor uninstalled")
        TestInterceptor.testInterceptor = null

        mockWebServer?.closeQuietly()
        mockWebServer = null

        MultipleFailureException.assertEmpty(mockDispatcher.errors)
    }

    open fun wrapDispatcher(mockDispatcher: MockDispatcher): Dispatcher = mockDispatcher
}
