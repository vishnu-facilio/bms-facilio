package com.facilio.bmsconsole.util;

import java.util.HashMap;
import java.util.Map;

import com.facilio.modules.FieldType;
import com.facilio.util.FacilioUtil;

public class PsychrometricUtil {
	
	private static final double RGAS = 8.31441;            							// Universal gas constant in J/mol/K
	private static final double MOLMASSAIR = 0.028964;     							// mean molar mass of dry air in kg/mol
	private static final double KILO = 1.e+03;             							// exact
	private static final double ZEROC = 273.15;          							  // Zero ºC expressed in K
	private static final double INVALID = -99999;         								 // Invalid value

	private static double ZERO_CELSIUS = 273.15;            // Zero ºC expressed in K

	public static Double getKelvinFromCelsius(Double celsius) {
		
		return celsius + ZERO_CELSIUS;
	}
	
	public static void main(String[] args) {
		Map<String,Object> weatherReading = new HashMap<String, Object>();
		weatherReading.put("temperature", 30.89);
		weatherReading.put("humidity", 0.36);
		weatherReading.put("pressure", 100845.00);
		Double wbt = getWetBulbTemperatureFromRelativeHumidity(weatherReading);
		System.out.println("WBT::::" +wbt);
		
		Double dp = getDewPointTemperatureFromRelativeHumudity(weatherReading);
		System.out.println("dp::::" +dp);
	}
	
	public static Double getMoistAirEnthalpy(Map<String,Object> weatherReading) {
		
		Double dryBulbTemperature = (Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("temperature"));
		Double pressure = ((Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("pressure"))) * 100;
		Double relativeHumidity = (Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("humidity"));
		
		Double humidityRatio = getHumidityRatioFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
		
		return getMoistAirEnthalpy(dryBulbTemperature,humidityRatio);
	}
	
	public static Double getMoistAirEnthalpy(Double dryBulbTemperature,Double pressure,Double relativeHumidity) {
		
		Double humidityRatio = getHumidityRatioFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
		
		return getMoistAirEnthalpy(dryBulbTemperature,humidityRatio);
	}
	
	public static Double getMoistAirEnthalpy(Double dryBulbTemperature,Double humidityRatio) {
		
		return (1.006*dryBulbTemperature + humidityRatio*(2501.0 + 1.86*dryBulbTemperature))*KILO;
	}
	
	public static Double getDewPointTemperatureFromRelativeHumudity(Map<String,Object> weatherReading) {
		
		Double dryBulbTemperature = (Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("temperature"));
		Double pressure = ((Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("pressure"))) * 100;
		Double relativeHumidity = (Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("humidity"));
		
		Double humidityRatio = getHumidityRatioFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
		return getDewPointTemperatureFromHumidityRatio(dryBulbTemperature, humidityRatio, pressure);
	}
	
	public static Double getDewPointTemperatureFromRelativeHumidity(Double dryBulbTemperature, Double relativeHumidity, Double pressure) {
		Double humidityRatio = getHumidityRatioFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
		return getDewPointTemperatureFromHumidityRatio(dryBulbTemperature, humidityRatio, pressure);
	}
	
	public static Double getWetBulbTemperatureFromRelativeHumidity(Map<String,Object> weatherReading) {
		
		Double dryBulbTemperature = (Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("temperature"));
		Double pressure = ((Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("pressure"))) * 100;
		Double relativeHumidity = (Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("humidity"));
		
		Double humidityRatio = getHumidityRatioFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
		return getWetBulbTemperatureFromHumidityRatio(dryBulbTemperature, humidityRatio, pressure);
	}
	
	public static Double getWetBulbTemperatureFromRelativeHumidity (Double dryBulbTemperature, Double relativeHumidity, Double pressure) {
		Double humidityRatio = getHumidityRatioFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
		return getWetBulbTemperatureFromHumidityRatio(dryBulbTemperature, humidityRatio, pressure);
	}
	
	public static Double getEnthalpy(Map<String,Object> weatherReading) {
		double dryBulbTemperature = (Double) FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, weatherReading.get("temperature"));
		return getEnthalpy(dryBulbTemperature);
	}
	
	public static Double getEnthalpy(Double dryBulbTemperature) {
		return dryBulbTemperature * KILO * 1.006;
	}
	public static Double getWetBulbTemperatureFromHumidityRatio(Double dryBulbTemperature, Double humidityRatio, Double pressure) {
		
		if (!(humidityRatio >= 0)) {
			return new Double(0); // Humidity ratio is negative
		}
		Double dewPointTemperature = getDewPointTemperatureFromHumidityRatio(dryBulbTemperature, humidityRatio, pressure);
		
		// Initial guesses
		Double wetBulbTemperatureSup = dryBulbTemperature;
		Double wetBulbTemperatureInf = dewPointTemperature;
		Double wetBulbTemperature = (wetBulbTemperatureInf + wetBulbTemperatureSup)/2.;
		
		// Bisection loop
		while(wetBulbTemperatureSup - wetBulbTemperatureInf > 0.001)
		{
			// Compute humidity ratio at temperature Tstar
			Double wStar = getHumidityRatioFromWetBulbTemperature(dryBulbTemperature, wetBulbTemperature, pressure);
			// Get new bounds
			if (wStar > humidityRatio) {
				wetBulbTemperatureSup = wetBulbTemperature;
			}
			else {
				wetBulbTemperatureInf = wetBulbTemperature;
			}

			// New guess of wet bulb temperature
			wetBulbTemperature = (wetBulbTemperatureSup + wetBulbTemperatureInf) / 2.;
		}
		return wetBulbTemperature;
	}
	
	public static Double getHumidityRatioFromWetBulbTemperature(Double dryBulbTemperature, Double wetBulbTemperature, Double pressure) {
		
		if (!(wetBulbTemperature <= dryBulbTemperature)) {
			new Double(0); // Wet bulb temperature is above dry bulb temperature
		}
		Double wsStar = getSaturatedHumidityRatio(wetBulbTemperature, pressure);
		return ((2501. - 2.326 * wetBulbTemperature) * wsStar - 1.006 * (dryBulbTemperature - wetBulbTemperature))
			       / (2501. + 1.86 * dryBulbTemperature - 4.186 * wetBulbTemperature);
	}
	
	public static Double getSaturatedHumidityRatio(Double temperature, Double pressure) {
		
		Double saturatedVapourPressure = getSaturatedVapourPressure(temperature);
		return 0.621945 * saturatedVapourPressure / (pressure-saturatedVapourPressure);
	}
	
	public static Double getDewPointTemperatureFromHumidityRatio(Double dryBulbTemperature, Double humidityRatio, Double pressure) {
		
		Double vapourPressure = getVapourPressureFromHumidityRatio(humidityRatio, pressure);
		return getDewPointTemperatureFromVapourPressure(dryBulbTemperature, vapourPressure);
	}
	
	public static Double getDewPointTemperatureFromVapourPressure(Double dryBulbTemperature, Double vapourPressure) {
		
		if (!(vapourPressure >= 0)) {
			return new Double(0); // Partial pressure of water vapor in moist air is negative
		}
		Double dewPointTemperature;
		Double vp = vapourPressure/1000;
		Double alpha = Math.log(vp);
		if (dryBulbTemperature >= 0 && dryBulbTemperature <= 93) {
			dewPointTemperature = 6.54+14.526*alpha+0.7389*alpha*alpha+0.09486*Math.pow(alpha,3)
		    +0.4569*Math.pow(vp, 0.1984);    
		}
		else if (dryBulbTemperature < 0) {
			dewPointTemperature = 6.09+12.608*alpha+0.4959*alpha*alpha; 
		}
		else {
			return new Double(0);                              // Invalid value
		}
		return Math.min(dewPointTemperature, dryBulbTemperature);
	}
	
	public static Double getVapourPressureFromHumidityRatio(Double humidityRatio, Double pressure) {
		
		return pressure*humidityRatio/(0.621945 +humidityRatio);
	}
	
	public static Double getHumidityRatioFromRelativeHumidity(Double dryBulbTemperature, Double relativeHumidity, Double pressure) {
		
		Double vapourPressure = getVapourPressureFromRelativeHumidity(dryBulbTemperature, relativeHumidity);
		return getHumidityRatioFromVapourPressure(vapourPressure, pressure);
	}
	
	public static Double getHumidityRatioFromVapourPressure(Double vapourPressure, Double pressure) {
		
		return 0.621945*vapourPressure/(pressure-vapourPressure);
	}
	
	public static Double getVapourPressureFromRelativeHumidity(Double dryBulbTemperature, Double relativeHumidity) {
		
		return relativeHumidity * getSaturatedVapourPressure(dryBulbTemperature);
	}
	
	public static Double getSaturatedVapourPressure(Double dryBulbTemperature) {
		
		Double lnPws;
		if (dryBulbTemperature == null || !(dryBulbTemperature >= -100. && dryBulbTemperature <= 200.)) {
			return new Double(0);             // TDryBulb is out of range [-100, 200]
		}
		Double temperature = getKelvinFromCelsius(dryBulbTemperature);
		if (dryBulbTemperature >= -100. && dryBulbTemperature <= 0.) {
			lnPws = (-5.6745359E+03/temperature + 6.3925247 - 9.677843E-03*temperature + 6.2215701E-07*temperature*temperature
		        + 2.0747825E-09*Math.pow(temperature, 3) - 9.484024E-13*Math.pow(temperature, 4) + 4.1635019*Math.log(temperature));
		}
		else if (dryBulbTemperature > 0. && dryBulbTemperature <= 200.) {
			lnPws = -5.8002206E+03/temperature + 1.3914993 - 4.8640239E-02*temperature + 4.1764768E-05*temperature*temperature
		      - 1.4452093E-08*Math.pow(temperature, 3) + 6.5459673*Math.log(temperature);
		}
		else {
		    return new Double(0);             // TDryBulb is out of range [-100, 200]
		}
		return Math.exp(lnPws);
	}
}
