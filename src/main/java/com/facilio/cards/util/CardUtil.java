package com.facilio.cards.util;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.Operator;

public class CardUtil {

	
	public static boolean isGetDataFromEnum(String key) {
		
		return CardType.CARD_TYPE_BY_NAME.keySet().contains(key);
	}
	
	public static String replaceWorflowPlaceHolders(String worflow, JSONObject params) {
		
		for(Object key: params.keySet()) {
			
			String keyString = key.toString();
			
			String value = params.get(key).toString();
			
			if(key.equals("dateOperator")) {
				
				value = Operator.OPERATOR_MAP.get(Integer.parseInt(value)).getOperator();
			}
			
			worflow = worflow.replace("%"+keyString+"%", value);
		}
		return worflow;
	}
}
