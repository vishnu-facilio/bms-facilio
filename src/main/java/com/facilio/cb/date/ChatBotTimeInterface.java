package com.facilio.cb.date;

import com.facilio.cb.context.ChatBotTimeContext;

public interface ChatBotTimeInterface {
	
	public long getMillisec(ChatBotTimeContext dateContext) throws Exception;
	public int getIntValue(ChatBotTimeContext dateContext) throws Exception;
}
