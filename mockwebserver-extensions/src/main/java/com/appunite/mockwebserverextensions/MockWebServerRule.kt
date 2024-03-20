package com.appunite.mockwebserverextensions

import com.appunite.mockwebserverextensions.intercept.TestInterceptor
import com.appunite.mockwebserverextensions.intercept.UrlOverrideInterceptor
import com.appunite.mockwebserverextensions.util.MultipleFailuresError
import com.appunite.mockwebserverextensions.util.ResponseGenerator
import okhttp3.Interceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import java.util.logging.Logger

class MockWebServerRule(
    private val interceptor: Interceptor? = null
) : TestRule {

    companion object {
        val LOG: Logger = Logger.getLogger(MockWebServerRule::class.java.name)
    }

    private val dispatcher: MockDispatcher = MockDispatcher()

    fun register(response: ResponseGenerator) = dispatcher.register(response)

    fun clear() = dispatcher.clear()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                MockWebServer().use { server ->
                    server.dispatcher = dispatcher
                    TestInterceptor.testInterceptor = interceptor ?: UrlOverrideInterceptor(server.url("/"))
                    LOG.info("TestInterceptor installed")
                    try {
                        base.evaluate()
                    } catch (e: Throwable) {
                        if (dispatcher.errors.isEmpty()) {
                            throw e
                        } else {
                            throw MultipleFailuresError(
                                "An test exception occurred, but we also found some not mocked requests",
                                buildList {
                                    add(e)
                                    addAll(dispatcher.errors)
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
