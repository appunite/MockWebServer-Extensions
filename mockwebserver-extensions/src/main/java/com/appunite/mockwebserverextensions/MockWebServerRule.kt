package com.appunite.mockwebserverextensions

import com.appunite.mockwebserver_interceptor.TestInterceptor
import com.appunite.mockwebserverextensions.intercept.UrlOverrideInterceptor
import com.appunite.mockwebserverextensions.util.MultipleFailuresError
import com.appunite.mockwebserverextensions.util.ResponseGenerator
import okhttp3.Interceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.logging.Logger

open class MockWebServerRule(
    private val interceptor: Interceptor? = null
) : TestWatcher(), MockRegistry {

    companion object {
        val LOG: Logger = Logger.getLogger(MockWebServerRule::class.java.name)
    }

    val mockDispatcher: MockDispatcher = MockDispatcher()

    override fun register(response: ResponseGenerator) = mockDispatcher.register(response)

    override fun clear() = mockDispatcher.clear()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                MockWebServer().use { server ->
                    server.dispatcher = mockDispatcher
                    TestInterceptor.testInterceptor =
                        interceptor ?: UrlOverrideInterceptor(server.url("/"))
                    LOG.info("TestInterceptor installed")
                    try {
                        base.evaluate()
                    } catch (e: Throwable) {
                        if (mockDispatcher.errors.isEmpty()) {
                            throw e
                        } else {
                            throw MultipleFailuresError(
                                "An test exception occurred, but we also found some not mocked requests",
                                buildList {
                                    add(e)
                                    addAll(mockDispatcher.errors)
                                }
                            )
                        }
                    } finally {
                        LOG.info("TestInterceptor uninstalled")
                        TestInterceptor.testInterceptor = null
                    }
                }
            }
        }
    }
}
