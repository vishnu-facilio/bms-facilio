package com.facilio.cb.date;

import java.util.Calendar;

import com.facilio.cb.context.ChatBotDateContext;

public interface ChatBotDateInterface {

	public void fillCalenderObj(ChatBotDateContext dateContext) throws Exception;
	public int getIntValue(ChatBotDateContext dateContext) throws Exception;
}
