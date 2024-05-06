/*
 * Copyright 2024 AppUnite S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appunite.mockwebserverextensions

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import okhttp3.mockwebserver.SocketPolicy
import okio.ByteString
import java.util.concurrent.TimeoutException

class OnOffInternetSimulator(private val source: Dispatcher) : Dispatcher() {

    private val activeConnections: MutableSet<WebSocket> = mutableSetOf()
    private var hasInternet = true

    private inner class OnOffInternetWebSocket(private val source: WebSocket) : WebSocket {
        override fun cancel() = source.cancel()
        override fun close(code: Int, reason: String?): Boolean = source.close(code, reason)
        override fun queueSize(): Long = source.queueSize()
        override fun request(): okhttp3.Request = source.request()
        override fun send(text: String): Boolean = if (hasInternet) {
            source.send(text)
        } else {
            true
        }

        override fun send(bytes: ByteString): Boolean = if (hasInternet) {
            source.send(bytes)
        } else {
            true
        }
    }

    private inner class OnOffInternetWebSocketListener(private val source: WebSocketListener) :
        WebSocketListener() {
        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) =
            source.onClosed(OnOffInternetWebSocket(webSocket), code, reason)

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            activeConnections.remove(webSocket)
            source.onClosing(OnOffInternetWebSocket(webSocket), code, reason)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) =
            source.onFailure(OnOffInternetWebSocket(webSocket), t, response)

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (hasInternet) {
                source.onMessage(OnOffInternetWebSocket(webSocket), text)
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            if (hasInternet) {
                source.onMessage(OnOffInternetWebSocket(webSocket), bytes)
            }
        }

        override fun onOpen(webSocket: WebSocket, response: Response) {
            if (hasInternet) {
                activeConnections.add(webSocket)
                source.onOpen(OnOffInternetWebSocket(webSocket), response)
            } else {
                webSocket.close(4000, "Internet lost")
            }
        }
    }

    override fun dispatch(request: RecordedRequest): MockResponse {
        if (!hasInternet) {
            return MockResponse().apply {
                this.socketPolicy = SocketPolicy.DISCONNECT_AFTER_REQUEST
            }
        }
        val mockResponse = source.dispatch(request)
        val originalWebSocketListener = mockResponse.webSocketListener
        return if (originalWebSocketListener != null) {
            // If Fake API Response is WebSocket upgrade, we would like to wrap it with our
            // OnOffInternetWebSocketListener, so we can simulate internet On/Off for websockets.
            mockResponse.withWebSocketUpgrade(
                OnOffInternetWebSocketListener(
                    originalWebSocketListener
                )
            )
        } else {
            mockResponse
        }
    }

    override fun peek(): MockResponse = if (!hasInternet) {
        MockResponse().apply { this.socketPolicy = SocketPolicy.DISCONNECT_AT_START }
    } else {
        super.peek()
    }

    fun simulateNetworkDown() {
        hasInternet = false
        activeConnections.forEach { it.close(4000, "Internet lost") }
        waitForCondition(timeoutMessage = "Waiting for disconnection timeout") { activeConnections.isEmpty() }
    }

    fun simulateNetworkUp() {
        hasInternet = true
    }

}

fun waitForCondition(timeoutMillis: Long = 10_000L, timeoutMessage: String = "Condition did not become true within timeout", precondition: () -> Boolean, ): Unit {
    val startTime = System.currentTimeMillis()
    while (!precondition()) {
        if (System.currentTimeMillis() - startTime > timeoutMillis) {
            throw TimeoutException(timeoutMessage)
        }
        Thread.sleep(100)
    }
}
