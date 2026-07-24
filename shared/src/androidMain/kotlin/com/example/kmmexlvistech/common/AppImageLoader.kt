package com.example.kmmexlvistech

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import okhttp3.OkHttpClient

actual fun createImageLoader(context: PlatformContext): ImageLoader {
    return ImageLoader.Builder(context)
        .crossfade(true)
        .components {
            add(OkHttpNetworkFetcherFactory(OkHttpClient()))
        }
        .build()
}