package com.rfm.quran.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class TafsirResponse(val tafsirs: List<TafsirAyah>?)
data class TafsirAyah(val text: String?, val verse_key: String?)

interface QuranApiService {
    @GET("api/v4/quran/tafsirs/{id}")
    suspend fun tafsir(
        @Path("id") id: Int,
        @Query("chapter_number") chapter: Int
    ): TafsirResponse
}

object QuranApi {
    val service: QuranApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.quran.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuranApiService::class.java)
    }
}

object TafsirSources {
    val list = listOf(
        169 to "تفسير ابن كثير",
        91 to "تفسير السعدي",
        168 to "التفسير الوسيط"
    )
}
