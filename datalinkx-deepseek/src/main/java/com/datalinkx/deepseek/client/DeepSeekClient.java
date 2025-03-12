package com.datalinkx.deepseek.client;

import com.datalinkx.deepseek.client.request.ChatReq;
import com.datalinkx.deepseek.client.response.DeepSeekResponse;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface DeepSeekClient {
    @POST("/chat/completions")
    DeepSeekResponse chat(@Body ChatReq chatReq, @Header("Authorization") String apiKey);
}
