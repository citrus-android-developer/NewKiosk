package com.citrus.pottedplantskiosk.di


import androidx.fragment.app.Fragment
import com.citrus.pottedplantskiosk.api.remote.ApiService
import com.citrus.pottedplantskiosk.ui.menu.adapter.*
import com.citrus.pottedplantskiosk.util.Constants
import com.skydoves.sandwich.coroutines.CoroutinesResponseCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
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
        fun okHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIME, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_WRITE_TIME, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_READ_TIME, TimeUnit.SECONDS)
                .build()

        @Provides
        @Singleton
        fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(CoroutinesResponseCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
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
