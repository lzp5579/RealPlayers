package test.realplayers.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by slon on 22.03.2017.
 */

public class Api {
    private static ApiRequests apiRequests;

    public static ApiRequests get() {
        if (apiRequests == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Interceptor.Chain chain) throws IOException {
                            Request request = chain.request().newBuilder()
                                    .addHeader("X-Response-Control", "minified")
                                    .addHeader("X-Auth-Token", "bcf2784db14c42d092118857dee46240").build();

                            return chain.proceed(request);
                        }
                    });

            OkHttpClient client = builder.build();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://api.football-data.org/v1/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiRequests = retrofit.create(ApiRequests.class);
        }
        return apiRequests;
    }
}
