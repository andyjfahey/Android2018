//package uk.co.healtht.healthtouch.retrofit;
//
//import okhttp3.OkHttpClient;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//import uk.co.healtht.healthtouch.comms.CommsProcessor;
//
//public class RestClient {
//	private static API apiService;
//
//	public static void setupRestClient() {
//		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//		OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//		OkHttpClient client = httpClient.addInterceptor(logging).build();
//		String uri;
//		if (CommsProcessor.SERVER_IS_PROD) {
//			uri = CommsProcessor.SERVER_PROD;
//		} else {
//			uri = CommsProcessor.SERVER_DEV;
//		}
//		Retrofit retrofit = new Retrofit.Builder()
//				.baseUrl(uri)
//				.client(client)
//				.addConverterFactory(GsonConverterFactory.create())
//				.build();
//
//		apiService = retrofit.create(API.class);
//	}
//
//	public static API getApiService() {
//		if (apiService == null) setupRestClient();
//		return apiService;
//	}
//}
