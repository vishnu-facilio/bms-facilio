package com.facilio.unitconversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Unit {
	
	// last id -- 87
	
	KWH(1,"Kilo Watt Hour","kWh",Metric.ENERGY),
	HECTOWH(76,"Hecto Watt Hour","hWh",Metric.ENERGY,"si*10","this/10"),
	DECAWH(79,"Deca Watt Hour","dWh",Metric.ENERGY,"si/100","this*100"),
	WH(2,"Watt Hour","Wh",Metric.ENERGY,"si*1000","this/1000"),
	DECIWH(82,"Deci Watt Hour","DWh",Metric.ENERGY,"si*100","this/100"),
	MWH(3,"Mega Watt Hour","mWh",Metric.ENERGY,"si/1000","this*1000"),
	
	CELSIUS(4,"Celsius","&deg;C",Metric.TEMPERATURE),
	FAHRENHEIT(5,"Fahrenheit","&deg;F",Metric.TEMPERATURE,"(si*1.8)+32","(this-32)/1.8"),
	KELWIN(6,"Kelwin","K",Metric.TEMPERATURE,"si+273.15","this-273.15"),
	
	METER(7,"Meter","m",Metric.LENGTH),
	DECIMETER(8,"Decimetre","dm",Metric.LENGTH,"si*10","this/10"),
	CENTIMETER(9,"Centimetre","cm",Metric.LENGTH,"si*100","this/100"),
	MILLIMETER(10,"Millimeter","mm",Metric.LENGTH,"si*1000","this/1000"),
	DECAMETER(11,"Decameter","dkm",Metric.LENGTH,"si/10","this*10"),
	HECTOMETER(12,"Hectometer","hm",Metric.LENGTH,"si/100","this*100"),
	KILOMETER(13,"kilometer","km",Metric.LENGTH,"si/1000","this*1000"),
	MILE(14,"Mile","mi",Metric.LENGTH,"si/1609.34","this*1609.34"),
	YARD(15,"Yard","yd",Metric.LENGTH,"si*1.0936132983","this*0.9144"),
	FEET(16,"Feet","ft",Metric.LENGTH,"si*3.280839895","this*0.3048"),
	INCH(17,"Inch","in",Metric.LENGTH,"si*39.3700787402","this*0.0254"),
	
	HOUR(18,"Hour","h",Metric.DURATION),
	MIN(19,"Minute","m",Metric.DURATION,"si*60","this/60"),
	SEC(20,"Second","s",Metric.DURATION,"si*60*60","this/(60*60)"),
	DAY(21,"Day","D",Metric.DURATION,"si/24","this*24"),
	WEEK(22,"Week","W",Metric.DURATION,"si/(24*7)","this*(24*7)"),
	YEAR(24,"Year","Y",Metric.DURATION,"si/(24*365)","this*(24*365)"),
	MILLIS(80,"Milli Second","ms",Metric.DURATION,"si*3600000","this/3600000"),
	
	KILOGRAM(25,"Kilo Gram","kg",Metric.MASS),
	HECTOGRAM(26,"Minute","hg",Metric.MASS,"si*10","this/10"),
	DEKAGRAM(27,"dekagram","dag",Metric.MASS,"si*100","this/100"),
	GRAM(28,"gram","g",Metric.MASS,"si*1000","this/1000"),
	DECIGRAM(29,"decigram","dg",Metric.MASS,"si*10000","this/10000"),
	CENTIGRAM(30,"centigram","cg",Metric.MASS,"si*100000","this/100000"),
	MILLIGRAM(31,"milligram","mg",Metric.MASS,"si*1000000","this/1000000"),
	TON(32,"Ton","t",Metric.MASS,"si/1000","this*1000"),
	STONE(33,"Stone","st",Metric.MASS,"si*0.157473","this/0.157473"),
	POUND(34,"Pound","lb",Metric.MASS,"si*2.20462","this/2.20462"),
	OUNCE(35,"Ounce","oz",Metric.MASS,"si*35.2739199982575","this/35.2739199982575"),
	
	VOLT(36,"Volt","V",Metric.VOLTAGE),
	MILLIVOLT(37,"Milli Volt","mV",Metric.VOLTAGE,"si*1000","this/1000"),
	MICROVOLT(38,"Micro Volt","&#x3BC;V",Metric.VOLTAGE,"si*1000000","this/1000000"),
	KILOVOLT(39,"Kilo Volt","kV",Metric.VOLTAGE,"si/1000","this*1000"),
	MEGAVOLT(40,"Mega Volt","MV",Metric.VOLTAGE,"si/1000000","this*1000000"),
	
	AMPERE(41,"Ampere","A",Metric.CURRENT),
	MILLIAMPERE(42,"Milli Ampere","mA",Metric.CURRENT,"si*1000","this/1000"),
	MICROAMPERE(43,"Micro Ampere","&#x3BC;A",Metric.CURRENT,"si*1000000","this/1000000"),
	KILOAMPERE(44,"Kilo Ampere","kA",Metric.CURRENT,"si/1000","this*1000"),
	MEGAAMPERE(45,"Mega Ampere","MA",Metric.CURRENT,"si/1000000","this*1000000"),
	
	KILOWATT(46,"Kilo Watt","kW",Metric.POWER),
	MILLIWATT(47,"Milli Watt","mW",Metric.POWER,"si*1000000","this/1000000"),
	MICROWATT(48,"Micro Watt","&#x3BC;W",Metric.POWER,"si*1000000000","this/1000000000"),
	WATT(49,"Watt","W",Metric.POWER,"si*1000","this/1000"),
	MEGAWATT(50,"Mega Watt","MW",Metric.POWER,"si/1000","this*1000"),
	
	HERTZ(51,"Hertz","Hz",Metric.FREQUENCY),
	MILLIHERTZ(52,"Milli Hertz","mHz",Metric.FREQUENCY,"si*1000","this/1000"),
	MICROHERTZ(53,"Micro Hertz","&#x3BC;Hz",Metric.FREQUENCY,"si*1000000","this/1000000"),
	KILOHERTZ(54,"Kilo Hertz","kHz",Metric.FREQUENCY,"si/1000","this*1000"),
	MEGAHERTZ(55,"Mega Hertz","MHz",Metric.FREQUENCY,"si/1000000","this*1000000"),
	GIGAHERTZ(56,"Giga Hertz","GHz",Metric.FREQUENCY,"si/1000000000","this*1000000000"),
	TERAHERTZ(57,"Tera Hertz","THz",Metric.FREQUENCY,"si/1000000000000","this*1000000000000"),
	
	VAR(58,"var","var",Metric.REACTIVEPOWER),
	KILOVAR(59,"Kilo var","kvar",Metric.REACTIVEPOWER,"si/1000","this*1000"),
	MEGAVAR(60,"Mega","Mvar",Metric.REACTIVEPOWER,"si/1000000","this*1000000"),
	
	PASCAL(61,"Pascal","pa",Metric.PRESSURE),
	
	DEGREE(62,"Degree","&deg;",Metric.DEGREE),
	
	SQUARE_METER(63,"Square Meter","m<sup>2</sup>",Metric.AREA),
	SQUARE_KILOMETER(64,"Square Kilometer","km<sup>2</sup>",Metric.AREA,"si/1000000","this * 1000000"),
	SQUARE_INCH(65,"Square Inch","in<sup>2</sup>",Metric.AREA,"si*1550.0031000062","this * 0.00064516"),
	SQUARE_FOOT(66,"Square Foot","ft<sup>2</sup>",Metric.AREA,"si*10.7639","this/10.7639"),
	HECTARE(67,"Hectare","ha",Metric.AREA,"si/10000","this*10000"),
	ACRE(68,"Acre","acre",Metric.AREA,"si/4046.86","this*4046.86"),
	
	NEWTON_METER(69,"Newton Meter","N m",Metric.TORQUE),
	
	LITRE(70,"Litre","l",Metric.LIQUID),
	MILILITRE(71,"Mili Litre","ml",Metric.LIQUID,"si*1000","this/1000"),
	DECALITRE(77,"Deca Litre","dl",Metric.LIQUID,"si/10","this*10"),
	KILOLITRE(78,"Kilo Litre","kl",Metric.LIQUID,"si/1000","this*1000"),
	CUBICMETER(81,"Cubic Meter","m<sup>3</sup>",Metric.LIQUID,"si/1000","this*1000"),
	CUBICFOOT(72,"Cubic Foot","ft<sup>3</sup>",Metric.LIQUID,"si/28.3168","this*28.3168"),
	CUBICINCH(73,"Cubic Inch","in<sup>3</sup>",Metric.LIQUID,"si*61.0237","this/61.0237"),
	USGALLON(74,"US Gallon","G",Metric.LIQUID,"si/3.78541","this*3.78541"),
	IMPERIALGALLON(75,"Imperial Gallon","IG",Metric.LIQUID,"si/4.54609","this*4.54609"),
	
	AED(83,"AED","AED",Metric.CURRENCY),
	DOLLER(84,"Doller","$",Metric.CURRENCY),
	INR(85,"inr","₹",Metric.CURRENCY),
	EURO(86,"euro","£",Metric.CURRENCY),
	
	PERCENTAGE(87,"percentage","%",Metric.PERCENTAGE),
	;
	
	int unitId;
	String displayName;
	String symbol;
	Metric metric;
	boolean isSiUnit;
	String fromSiUnit;
	String toSiUnit;
	
	private static final Map<Integer, Unit> UNIT_MAP = Collections.unmodifiableMap(initTypeMap());
	private static Map<Integer, Unit> initTypeMap() {
		Map<Integer, Unit> typeMap = new HashMap<>();
		for(Unit type : values()) {
			typeMap.put(type.getUnitId(), type);
		}
		return typeMap;
	}
	
	private static final Map<Metric,Collection<Unit>> METRIC_UNIT_MAP = initMap();
	
	public static Unit valueOf(int unitId) {
		return UNIT_MAP.get(unitId);
	}
	private static Map<Metric,Collection<Unit>> initMap() {
		Map<Metric,Collection<Unit>> typeMap = new HashMap<>();
		for(Unit unit : values()) {
			
			Collection<Unit> units = typeMap.get(unit.metric) == null ? new ArrayList<>() :typeMap.get(unit.metric);
			units.add(unit);
			
			if(typeMap.get(unit.metric) == null) {
				typeMap.put(unit.metric, units);
			}
		}
		return typeMap;
	}
	
	public static Map<Metric,Collection<Unit>> getMetricUnitMap() {
		return METRIC_UNIT_MAP;
	}
	
	public static Collection<Unit> getUnitsForMetric(Metric metric) {
		return METRIC_UNIT_MAP.get(metric);
	}
	
	Unit(int unitId,String displayName,String symbol,Metric metric) {
		
		this.unitId = unitId;
		this.metric = metric;
		this.isSiUnit = true;
		this.displayName = displayName;
		this.symbol = symbol;
	}
	
	Unit(int unitId,String displayName,String symbol,Metric metric,String fromSiUnit,String toSiUnit) {
		this.unitId = unitId;
		this.metric = metric;
		this.isSiUnit = false;
		this.fromSiUnit = fromSiUnit;
		this.toSiUnit = toSiUnit;
		this.displayName = displayName;
		this.symbol = symbol;
	}
	
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getUnitId() {
		return unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public boolean isSiUnit() {
		return isSiUnit;
	}

	public void setSiUnit(boolean isSiUnit) {
		this.isSiUnit = isSiUnit;
	}

	public String getFromSiUnit() {
		return fromSiUnit;
	}

	public void setFromSiUnit(String fromSiUnit) {
		this.fromSiUnit = fromSiUnit;
	}

	public String getToSiUnit() {
		return toSiUnit;
	}

	public void setToSiUnit(String toSiUnit) {
		this.toSiUnit = toSiUnit;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

}