package com.network;

/**
 * Created by sumon.chatterjee on 1/16/16.
 */

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.network.utils.ApiEndPoints;

/***
 * Helper class that create single volley request and set tag this if user not
 * set the tag for canceling the requestes.
 * **/
public class VolleyQueueWrapper {

	private static RequestQueue requestQueue;
	private static String BASE_URL = ApiEndPoints.BASE_URL;


	//TimesDemo project name google console
	//private static final String GOOGLE_PLACES_API = "AIzaSyAvBeQqjfwiAylnE2W8tU2bUZPc-0UibKk";

	//LocationDEmo project name google console
	private static final String GOOGLE_PLACES_API = "AIzaSyDHovD1WHn59oVuqKgYzgPpmnHvXGELmNo";

	public static void setBaseUrl(String baseUrl) {
		BASE_URL = baseUrl;
	}

	public static String getBaseUrl() {
		return BASE_URL;
	}

	private VolleyQueueWrapper(Context context, String savedBaseUrl) {

		if (requestQueue == null) {
			BASE_URL = savedBaseUrl;
			requestQueue = Volley.newRequestQueue(context);
			requestQueue.start();
		}
	}

	public static VolleyQueueWrapper
			newInstance(Context context, String baseUrl) {
		return new VolleyQueueWrapper(context, baseUrl);
	}

	// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=28.6451518,76.8098805&rankby=distance&name=food&key=AIzaSyAvBeQqjfwiAylnE2W8tU2bUZPc-0UibKk

	private static Builder commonUriBuilder(String path) {
		return appendRequiredParams(Uri.parse(appendBaseUrlIfNeeded(path))
				.buildUpon());
	}

	private static Builder appendRequiredParams(Builder uriBuilder) {
		uriBuilder.appendQueryParameter("key", GOOGLE_PLACES_API);
		return uriBuilder;
	}

	public static String buildSearchUrl(String name, double lat, double lng,
			String pageToken) {
		Builder uriBuilder = commonUriBuilder(ApiEndPoints.SEARCH_PATH);
		uriBuilder.appendQueryParameter("location", lat + "," + lng);
		uriBuilder.appendQueryParameter("rankby", "distance");
		uriBuilder.appendQueryParameter("name", name);

		if (!TextUtils.isEmpty(pageToken)) {
			uriBuilder.appendQueryParameter("pageToken", pageToken);
		}
		return uriBuilder.build().toString();
	}

	public static String imageUrl(String photoReference, int imageMaxWidth) {
		// https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU&key=YOUR_API_KEY
		Builder uriBuilder = commonUriBuilder(ApiEndPoints.IMAGE_PATH);
		uriBuilder.appendQueryParameter("photoreference", photoReference);
		uriBuilder.appendQueryParameter("maxwidth",
				String.valueOf(imageMaxWidth));
		return uriBuilder.build().toString();
	}

	public static String appendBaseUrlIfNeeded(String urlPath) {
		if (!TextUtils.isEmpty(urlPath) && !urlPath.startsWith("http")) {
			urlPath = BASE_URL + urlPath;
		}
		return urlPath;
	}

	public static String placeDetailUrl(String placeId) {
		// placeid
		Builder uriBuilder = commonUriBuilder(ApiEndPoints.PLACE_DETAIL_PATH);
		uriBuilder.appendQueryParameter("placeid", placeId);
		return uriBuilder.build().toString();
	}

	public void addRequest(final Request<?> request) {
		if (request.getTag() == null) {
			request.setTag(this);
		}
		requestQueue.add(request);
	}

	public void cancel() {
		requestQueue.cancelAll(this);
	}

}
