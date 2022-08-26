package org.stypox.tridenta.extractor

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient

@Module
@InstallIn(SingletonComponent::class)
class OkHttpClientModule {
    @Provides
    fun bindOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .build()
    }
}
