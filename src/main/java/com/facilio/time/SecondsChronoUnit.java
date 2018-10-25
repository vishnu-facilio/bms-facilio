package com.facilio.time;

import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;

public class SecondsChronoUnit implements TemporalUnit, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Duration duration;
	public SecondsChronoUnit() {
		
	}
	
	public SecondsChronoUnit(long durationInSeconds) {
		// TODO Auto-generated constructor stub
		duration = Duration.ofSeconds(durationInSeconds);
	}
	
	@Override
	public Duration getDuration() {
		// TODO Auto-generated method stub
		return duration;
	}

	@Override
	public boolean isDurationEstimated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDateBased() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTimeBased() {
		// TODO Auto-generated method stub
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R extends Temporal> R addTo(R temporal, long amount) {
		// TODO Auto-generated method stub
		return (R) temporal.plus(amount * duration.getSeconds(), ChronoUnit.SECONDS);
	}

	@Override
	public long between(Temporal temporal1Inclusive, Temporal temporal2Exclusive) {
		// TODO Auto-generated method stub
		return temporal1Inclusive.until(temporal2Exclusive, ChronoUnit.SECONDS) / duration.getSeconds();
	}

}
