package com.example.kmmexlvistech

expect class Context
expect object AppContext {
    fun get(): Context
}

