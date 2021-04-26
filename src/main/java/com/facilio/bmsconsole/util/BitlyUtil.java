package com.facilio.bmsconsole.util;

//import net.swisstech.bitly.BitlyClient;
//import net.swisstech.bitly.model.Response;
//import net.swisstech.bitly.model.v3.ShortenResponse;

public class BitlyUtil {

	private static final String AUTH_KEY = "e32d4429277efba8d865685005d825cc066aa510";
	
	public static String getSmallUrl(String longUrl) throws Exception {
		
//		BitlyClient client = new BitlyClient(AUTH_KEY);
//		Response<ShortenResponse> resp = client.shorten()
//		                          .setLongUrl(longUrl)
//		                          .call();
//		
//		if(resp != null &&  resp.data != null && resp.data.url != null) {
//			return resp.data.url;
//		}
		return null;
	}
}
