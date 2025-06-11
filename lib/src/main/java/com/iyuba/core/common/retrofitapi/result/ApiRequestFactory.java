package com.iyuba.core.common.retrofitapi.result;



import com.iyuba.core.common.retrofitapi.YzPhoneNumber;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 作者：renzhy on 16/6/22 16:45
 * 邮箱：renzhongyigoo@gmail.com
 */
public class ApiRequestFactory {


	private static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
	private static OkHttpClient okHttpClient = new OkHttpClient();
	private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
	private static YzPhoneNumber yzPhoneNumber;

	public static void initOkHttpClient(){
		interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
		okHttpClient = new OkHttpClient.Builder()
				.connectTimeout(10, TimeUnit.SECONDS)
				.readTimeout(10, TimeUnit.SECONDS)
				.addInterceptor(interceptor)
				.build();
	}
	public static YzPhoneNumber getYzPhoneNumber(){
		if(yzPhoneNumber == null){
			initOkHttpClient();
			Retrofit retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(YzPhoneNumber.YZNUMBER_URL)
					.addConverterFactory(gsonConverterFactory)
					.build();

			yzPhoneNumber = retrofit.create(YzPhoneNumber.class);
		}
		return yzPhoneNumber;
	}

}
