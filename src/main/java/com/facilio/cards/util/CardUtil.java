package com.facilio.cards.util;

import org.json.simple.JSONObject;

public class CardUtil {

	
	public static boolean isGetDataFromEnum(String key) {
		
		return CardType.CARD_TYPE_BY_NAME.keySet().contains(key);
	}
	
	public static String replaceWorflowPlaceHolders(String worflow, JSONObject params) {
		
		for(Object key: params.keySet()) {
			
			String keyString = key.toString();
			
			String value = params.get(key).toString();
			
			worflow = worflow.replace("%"+keyString+"%", value);
		}
		return worflow;
	}
}
