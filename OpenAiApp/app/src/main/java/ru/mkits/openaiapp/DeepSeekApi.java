package ru.mkits.openaiapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DeepSeekApi {
    @Headers({"Content-Type: application/json",
            "Authorization: Bearer sk-e3041aabf6204567a745f84b2f5ee2f4"})
    @POST("v1/chat/completions")
    Call<ChatResponse> senMessage(@Body ChatRequest request);
}
