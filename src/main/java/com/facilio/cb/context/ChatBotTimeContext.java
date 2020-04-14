package com.facilio.cb.context;

import com.facilio.cb.date.TimeEnum;

public class ChatBotTimeContext {
	int hour = -1;
	int min = -1;
	TimeEnum timeEnum;
	
	public int getHour() {
		return hour;
	}
	public void setHour(int hour) {
		this.hour = hour;
	}
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public TimeEnum getTimeEnum() {
		return timeEnum;
	}
	public void setTimeEnum(String timeEnum) {
		this.timeEnum = TimeEnum.getDateEnum(timeEnum);
	}
}
