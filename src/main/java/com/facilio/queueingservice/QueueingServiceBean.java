package com.facilio.queueingservice;

import java.util.List;
import java.util.Map;


public interface QueueingServiceBean {

	public void addScriptLogs(List<Map<String,Object>> record);
	
	public void addScriptLog(Map<String,Object> record) throws Exception;
}
