package com.appunite.mockwebserverextensions

import com.appunite.mockwebserverextensions.util.ResponseGenerator

interface MockRegistry {
    fun register(response: ResponseGenerator)
    fun clear()
}
