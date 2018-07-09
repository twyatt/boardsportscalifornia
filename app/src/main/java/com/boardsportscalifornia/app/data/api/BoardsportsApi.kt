package com.boardsportscalifornia.app.data.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET

interface BoardsportsApi {

    @GET("windreport.png")
    fun windGraph(): Call<ResponseBody>
}
