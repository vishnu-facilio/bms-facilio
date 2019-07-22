package com.facilio.bmsconsole.context;

import java.util.List;

public class HistoricalLoggersWrapper {
	
	public HistoricalLoggerContext historicalLoggerParentMeter;
	public List<HistoricalLoggerContext> historicalLoggerChildMeters;
	
	public HistoricalLoggerContext getHistoricalLoggerParentMeter() {
		return historicalLoggerParentMeter;
	}
	public void setHistoricalLoggerParentMeter(HistoricalLoggerContext historicalLoggerParentMeter) {
		this.historicalLoggerParentMeter = historicalLoggerParentMeter;
	}
	
	public List<HistoricalLoggerContext> getHistoricalLoggerChildMeters() {
		return historicalLoggerChildMeters;
	}
	public void setHistoricalLoggerChildMeters(List<HistoricalLoggerContext> historicalLoggerChildMeters) {
		this.historicalLoggerChildMeters = historicalLoggerChildMeters;
	}
	
}	
	
