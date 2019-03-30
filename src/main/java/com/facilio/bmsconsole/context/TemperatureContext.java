package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemperatureContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long ttime;
	private double temperature;
	
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(new Long(getId()).toString());
		buf.append(",");
		buf.append(new Long(ttime).toString());
		buf.append(",");
		buf.append(new Double(temperature).toString());
		return buf.toString();
	}
	
	public static List<TemperatureContext> convertToObjectFromMap(List< Map<String, Object> > siteInfo) {
		List<TemperatureContext> listOfTemperatureContext = new ArrayList<>();
		for(Map<String, Object> mapObject: siteInfo) {
			TemperatureContext obj = new TemperatureContext();
			for(String key: mapObject.keySet()) {
				if(key.equals("temperature")) {
					obj.setTemperature((Double) mapObject.get(key));
				}else if(key.equals("ttime")) {
					obj.setTtime((Long) mapObject.get(key));
				}
			}
			
			listOfTemperatureContext.add(obj);
		}
		
		return listOfTemperatureContext;
	}
}