package com.appunite.mockwebserver_extension

import com.appunite.mockwebserver_extension.intercept.TestInterceptor
import com.appunite.mockwebserver_extension.intercept.UrlOverrideInterceptor
import com.appunite.mockwebserver_extension.util.MultipleFailuresError
import com.appunite.mockwebserver_extension.util.ResponseGenerator
import io.github.oshai.kotlinlogging.KotlinLogging
import okhttp3.Interceptor
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

private const val TAG = "MockWebServerRule"
private val logger = KotlinLogging.logger {}

class MockWebServerRule(
    private val interceptor: Interceptor? = null,
    val dispatcher: MockDispatcher = MockDispatcher()
) : TestRule {

    fun register(response: ResponseGenerator) = dispatcher.register(response)

    fun clear() = dispatcher.clear()

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                MockWebServer().use { server ->
                    server.dispatcher = dispatcher
                    TestInterceptor.testInterceptor = interceptor ?: UrlOverrideInterceptor(server.url("/"))
                    logger.info { TAG + "TestInterceptor installed" }
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
                        logger.info { TAG + "TestInterceptor uninstalled" }
                        TestInterceptor.testInterceptor = null
                    }
                }
            }
        }
    }
}
