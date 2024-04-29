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

    val mockDispatcher: MockDispatcher = MockDispatcher()
    private val onOffInternetSimulator = OnOffInternetSimulator(mockDispatcher)
    val mockWebServer: MockWebServer = MockWebServer().apply {
        dispatcher = wrapDispatcher(onOffInternetSimulator)
    }

    open fun simulateNetworkDown() = onOffInternetSimulator.simulateNetworkDown()

    open fun simulateNetworkUp() = onOffInternetSimulator.simulateNetworkUp()

    val server: MockWebServer get() = mockWebServer

    override fun register(response: ResponseGenerator) = mockDispatcher.register(response)

    override fun clear() = mockDispatcher.clear()

    override fun starting(description: Description?) {
        installMockServer()
        LOG.info("TestInterceptor installed")
    }

    open fun installMockServer() {
        TestInterceptor.testInterceptor = interceptor ?: UrlOverrideInterceptor(mockWebServer.url("/"))
    }

    override fun finished(description: Description) {
        LOG.info("TestInterceptor uninstalled")
        TestInterceptor.testInterceptor = null

        mockWebServer.closeQuietly()
        MultipleFailureException.assertEmpty(mockDispatcher.errors)
    }

    open fun wrapDispatcher(mockDispatcher: Dispatcher): Dispatcher = mockDispatcher
}
