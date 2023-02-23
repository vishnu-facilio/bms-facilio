package com.facilio.bmsconsoleV3.context.weather;

import com.facilio.v3.context.V3Context;
import lombok.Data;

@Data
public class V3WeatherStationContext extends V3Context {
	
	private static final long serialVersionUID = 1L;

	private Double lat;
	private Double lng;
	private String stationCode;
	private long serviceId;
	private String name;
	private String description;
	private V3WeatherServiceContext service;

}
