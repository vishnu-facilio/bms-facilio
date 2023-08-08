package com.facilio.bmsconsoleV3.context.weather;

import com.facilio.v3.context.V3Context;
import com.facilio.weather.util.ServiceType;
import lombok.Data;

@Data
public class V3WeatherServiceContext extends V3Context {
	
	private static final long serialVersionUID = 1L;

	private ServiceType name;
	private String displayName;
	private long dataInterval;
	private String apiKey;
	private boolean status;

}
