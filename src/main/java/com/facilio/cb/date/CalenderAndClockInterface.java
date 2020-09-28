package com.facilio.cb.date;

import com.facilio.cb.context.ChatBotDateContext;

public interface CalenderAndClockInterface {

	// chatbot Functions
	public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception;
	public int getIntValue(ChatBotDateContext dateContext) throws Exception;
	
}
