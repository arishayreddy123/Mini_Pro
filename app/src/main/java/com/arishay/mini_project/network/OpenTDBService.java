package com.arishay.mini_project.network;

import com.arishay.mini_project.model.OpenTDBResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenTDBService {

    @GET("api.php")
    Call<OpenTDBResponse> getQuestions(
            @Query("amount") int amount,
            @Query("category") int category, // category ID
            @Query("difficulty") String difficulty,
            @Query("type") String type // "multiple", "boolean" or "mixed"
    );
}
