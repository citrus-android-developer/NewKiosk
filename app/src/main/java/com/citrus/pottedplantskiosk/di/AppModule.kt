package com.citrus.pottedplantskiosk.di


import androidx.fragment.app.Fragment
import com.citrus.pottedplantskiosk.BuildConfig
import com.citrus.pottedplantskiosk.api.remote.ApiService
import com.citrus.pottedplantskiosk.api.remote.dto.BannerData
import com.citrus.pottedplantskiosk.api.remote.dto.BannerDataDeserializer
import com.citrus.pottedplantskiosk.api.remote.dto.StatusCode
import com.citrus.pottedplantskiosk.api.remote.dto.StatusCodeDeserializer
import com.citrus.pottedplantskiosk.ui.menu.adapter.*
import com.citrus.pottedplantskiosk.util.Constants
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface AppModule {
    companion object {
        private const val DEFAULT_CONNECT_TIME = 10L
        private const val DEFAULT_WRITE_TIME = 30L
        private const val DEFAULT_READ_TIME = 30L

        @Provides
        @Singleton
        fun okHttpClient(): OkHttpClient {
            val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()

            if (BuildConfig.DEBUG) {
                val loggingInterceptor =
                    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
                okHttpClientBuilder.addInterceptor(loggingInterceptor)
            }

            return okHttpClientBuilder
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)
                .build()
        }

        @Provides
        @Singleton
        fun provideGsonAdapter(): Gson =
            GsonBuilder()
                .registerTypeAdapter(BannerData::class.java, BannerDataDeserializer())
                .registerTypeAdapter(StatusCode::class.java,StatusCodeDeserializer<String>())
                .registerTypeAdapter(StatusCode::class.java,StatusCodeDeserializer<Int>())
                .create()


        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit =
            Retrofit.Builder()
                .baseUrl("http://cms.citrus.tw/")
                .client(okHttpClient)
                .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        @Provides
        @Singleton
        fun provideApiService(retrofit: Retrofit): ApiService =
            retrofit.create(ApiService::class.java)

    }
}


@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {
    @Provides
    fun provideGoodsItemAdapter(fragment: Fragment) =
        GoodsItemAdapter(fragment.requireContext())

    @Provides
    fun provideMainGroupItemAdapter(fragment: Fragment) =
        MainGroupItemAdapter(fragment.requireContext())


    @Provides
    fun provideGroupItemAdapter(fragment: Fragment) =
        GroupItemAdapter(fragment.requireContext())

}
