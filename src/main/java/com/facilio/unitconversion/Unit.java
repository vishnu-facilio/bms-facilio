package com.facilio.unitconversion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Unit {
	
	// last id -- 101
	
	KWH(1,"kilowatt-hour","kWh",Metric.ENERGY),
	HECTOWH(76,"hectowatt-hour","hWh",Metric.ENERGY,"si*10","this/10"),
	DECAWH(79,"decawatt-hour","dWh",Metric.ENERGY,"si/100","this*100"),
	WH(2,"watt-hour","Wh",Metric.ENERGY,"si*1000","this/1000"),
	DECIWH(82,"deciwatt-hour","DWh",Metric.ENERGY,"si*100","this/100"),
	MWH(3,"megawatt-hour","MWh",Metric.ENERGY,"si/1000","this*1000"),
	
	CELSIUS(4,"Celsius","\u00B0C",Metric.TEMPERATURE),
	FAHRENHEIT(5,"Fahrenheit","\u00B0F",Metric.TEMPERATURE,"(si*1.8)+32","(this-32)/1.8"),
	KELWIN(6,"Kelvin","K",Metric.TEMPERATURE,"si+273.15","this-273.15"),
	
	METER(7,"meter","m",Metric.LENGTH),
	DECIMETER(8,"decimeter","dm",Metric.LENGTH,"si*10","this/10"),
	CENTIMETER(9,"centimeter","cm",Metric.LENGTH,"si*100","this/100"),
	MILLIMETER(10,"millimeter","mm",Metric.LENGTH,"si*1000","this/1000"),
	DECAMETER(11,"decameter","dam",Metric.LENGTH,"si/10","this*10"),
	HECTOMETER(12,"hectometer","hm",Metric.LENGTH,"si/100","this*100"),
	KILOMETER(13,"kilometer","km",Metric.LENGTH,"si/1000","this*1000"),
	MILE(14,"mile","mi",Metric.LENGTH,"si/1609.34","this*1609.34"),
	YARD(15,"yard","yd",Metric.LENGTH,"si*1.0936132983","this*0.9144"),
	FEET(16,"feet","ft",Metric.LENGTH,"si*3.280839895","this*0.3048"),
	INCH(17,"inch","in",Metric.LENGTH,"si*39.3700787402","this*0.0254"),
	
	HOUR(18,"hour","h",Metric.DURATION),
	MIN(19,"minute","m",Metric.DURATION,"si*60","this/60"),
	SEC(20,"second","s",Metric.DURATION,"si*60*60","this/(60*60)"),
	DAY(21,"day","days",Metric.DURATION,"si/24","this*24"),
	WEEK(22,"week","W",Metric.DURATION,"si/(24*7)","this*(24*7)"),
	YEAR(24,"year","Y",Metric.DURATION,"si/(24*365)","this*(24*365)"),
	MILLIS(80,"millisecond","ms",Metric.DURATION,"si*3600000","this/3600000"),
	
	KILOGRAM(25,"kilogram","kg",Metric.MASS),
	HECTOGRAM(26,"hectogram","hg", Metric.MASS,"si*10","this/10"),
	DEKAGRAM(27,"decagram","dag",Metric.MASS,"si*100","this/100"),
	GRAM(28,"gram","g",Metric.MASS,"si*1000","this/1000"),
	DECIGRAM(29,"decigram","dg",Metric.MASS,"si*10000","this/10000"),
	CENTIGRAM(30,"centigram","cg",Metric.MASS,"si*100000","this/100000"),
	MILLIGRAM(31,"milligram","mg",Metric.MASS,"si*1000000","this/1000000"),
	TON(32,"ton","t",Metric.MASS,"si/1000","this*1000"),
	STONE(33,"Stone","st",Metric.MASS,"si*0.157473","this/0.157473"),
	POUND(34,"Pound","lb",Metric.MASS,"si*2.20462","this/2.20462"),
	OUNCE(35,"Ounce","oz",Metric.MASS,"si*35.2739199982575","this/35.2739199982575"),
	
	VOLT(36,"volt","V",Metric.VOLTAGE),
	MILLIVOLT(37,"millivolt","mV",Metric.VOLTAGE,"si*1000","this/1000"),
	MICROVOLT(38,"microvolt","\u00B5V",Metric.VOLTAGE,"si*1000000","this/1000000"),
	KILOVOLT(39,"kilovolt","kV",Metric.VOLTAGE,"si/1000","this*1000"),
	MEGAVOLT(40,"Megavolt","MV",Metric.VOLTAGE,"si/1000000","this*1000000"),
	
	AMPERE(41,"ampere","A",Metric.CURRENT),
	MILLIAMPERE(42,"milliampere","mA",Metric.CURRENT,"si*1000","this/1000"),
	MICROAMPERE(43,"microampere","\u00B5A",Metric.CURRENT,"si*1000000","this/1000000"),
	KILOAMPERE(44,"kiloampere","kA",Metric.CURRENT,"si/1000","this*1000"),
	MEGAAMPERE(45,"Megaampere","MA",Metric.CURRENT,"si/1000000","this*1000000"),
	
	KILOWATT(46,"kilowatt","kW",Metric.POWER),
	MILLIWATT(47,"milliwatt","mW",Metric.POWER,"si*1000000","this/1000000"),
	MICROWATT(48,"microwatt","\u00B5W",Metric.POWER,"si*1000000000","this/1000000000"),
	WATT(49,"watt","W",Metric.POWER,"si*1000","this/1000"),
	MEGAWATT(50,"Megawatt","MW",Metric.POWER,"si/1000","this*1000"),
	
	HERTZ(51,"hertz","Hz",Metric.FREQUENCY),
	MILLIHERTZ(52,"millihertz","mHz",Metric.FREQUENCY,"si*1000","this/1000"),
	DECAHERTZ(94,"decahertz","daHz",Metric.FREQUENCY,"si/10","this*10"),
	DECIHERTZ(95,"decihertz","dHz",Metric.FREQUENCY,"si*10","this/10"),
	MICROHERTZ(53,"microhertz","\u00B5Hz",Metric.FREQUENCY,"si*1000000","this/1000000"),
	KILOHERTZ(54,"kilohertz","kHz",Metric.FREQUENCY,"si/1000","this*1000"),
	MEGAHERTZ(55,"Megahertz","MHz",Metric.FREQUENCY,"si/1000000","this*1000000"),
	GIGAHERTZ(56,"Gigahertz","GHz",Metric.FREQUENCY,"si/1000000000","this*1000000000"),
	TERAHERTZ(57,"Terahertz","THz",Metric.FREQUENCY,"si/1000000000000","this*1000000000000"),
	
	VOLTAMP(96,"volt-amperes","VA",Metric.APPARENTPOWER),
	KILOVOLTAMP(97,"kilovolt-amperes","kVA",Metric.APPARENTPOWER,"si/1000","this*1000"),
	MEGAVOLTAMP(98,"Megavolt-amperes","MVA",Metric.APPARENTPOWER,"si/1000000","this*1000000"),
	MILLIVOLTAMP(99,"millivolt-amperes","mVA",Metric.APPARENTPOWER,"si*1000000","this/1000000"),
	
	VAR(58,"volt-ampere reactive","var",Metric.REACTIVEPOWER),
	KILOVAR(59,"kilovolt-ampere reactive","kvar",Metric.REACTIVEPOWER,"si/1000","this*1000"),
	MEGAVAR(60,"Megavolt-ampere reactive","Mvar",Metric.REACTIVEPOWER,"si/1000000","this*1000000"),
	
	PASCAL(61,"pascal","Pa",Metric.PRESSURE),
	
	DEGREE(62,"Degree","&deg;",Metric.DEGREE),
	
	SQUARE_METER(63,"square meter","m<sup>2</sup>",Metric.AREA),
	SQUARE_KILOMETER(64,"square kilometer","km<sup>2</sup>",Metric.AREA,"si/1000000","this * 1000000"),
	SQUARE_INCH(65,"square inch","in<sup>2</sup>",Metric.AREA,"si*1550.0031000062","this * 0.00064516"),
	SQUARE_FOOT(66,"square foot","ft<sup>2</sup>",Metric.AREA,"si*10.7639","this/10.7639"),
	HECTARE(67,"hectare","ha",Metric.AREA,"si/10000","this*10000"),
	ACRE(68,"acre","acre",Metric.AREA,"si/4046.86","this*4046.86"),
	
	NEWTON_METER(69,"newton-meter","N m",Metric.TORQUE),
	
	LITRE(70,"litre","l",Metric.VOLUME),
	MILILITRE(71,"mililitre","ml",Metric.VOLUME,"si*1000","this/1000"),
	DECALITRE(77,"decalitre","dl",Metric.VOLUME,"si/10","this*10"),
	KILOLITRE(78,"kilolitre","kl",Metric.VOLUME,"si/1000","this*1000"),
	CUBICMETER(81,"cubic meter","m<sup>3</sup>",Metric.VOLUME,"si/1000","this*1000"),
	CUBICFOOT(72,"cubic foot","ft<sup>3</sup>",Metric.VOLUME,"si/28.3168","this*28.3168"),
	CUBICINCH(73,"cubic inch","in<sup>3</sup>",Metric.VOLUME,"si*61.0237","this/61.0237"),
	USGALLON(74,"US Gallon","US gal",Metric.VOLUME,"si/3.78541","this*3.78541"),
	IMPERIALGALLON(75,"Imperial Gallon","imp gal",Metric.VOLUME,"si/4.54609","this*4.54609"),
	
	AED(83,"AED","AED", Metric.CURRENCY),
	DOLLER(84,"USD","$", Metric.CURRENCY, true),
	INR(85,"INR","₹", Metric.CURRENCY, true),
	EURO(86,"Euro","£", Metric.CURRENCY, true),
	
	PERCENTAGE(87,"Percentage","%",Metric.PERCENTAGE),
	PERCENTAGE_DIV_100(88,"percentage","%",Metric.PERCENTAGE,"si/100","this*100"),	// do not use this in ORG_UNITS
	
	MILLIPERHOUR(89,"millimeters/hour","mm/hr",Metric.PRECIPITATION_INTENSITY),
	
	CUBICFEETPERMIN(90,"cubic feet per minute","ft<sup>3</sup>/min",Metric.FLOWRATE),
	CUBICFEETPERHOUR(91,"cubic feet per hour","ft<sup>3</sup>/hr",Metric.FLOWRATE,"si*60","this/60"),
	GALLONSPERMIN(92,"Gallons per minute","gal/min",Metric.FLOWRATE,"si*7.48","this/7.48"),
	LITRESPERMIN(93,"litres per minute","l/min",Metric.FLOWRATE,"si*28.316","this/28.316"),
	CUBICMETERPERSECOND(100,"cubic meter per second","m<sup>3</sup>/sec",Metric.FLOWRATE,"si/2118.88","this*2118.88"),
	
	PARTSPERMILLION(101,"parts per million","ppm",Metric.CONCENTRATION),
	;
	
	int unitId;
	String displayName;
	String symbol;
	Metric metric;
	boolean isSiUnit;
	boolean isLeft;
	String fromSiUnit;
	String toSiUnit;
	
	private static final Map<Integer, Unit> UNIT_MAP = Collections.unmodifiableMap(initTypeMap());
	
	private static final Map<String, Unit> UNIT_SYMBOL_MAP = Collections.unmodifiableMap(initUnitSymbolMap());
	
	private static Map<Integer, Unit> initTypeMap() {
		Map<Integer, Unit> typeMap = new HashMap<>();
		for(Unit type : values()) {
			typeMap.put(type.getUnitId(), type);
		}
		return typeMap;
	}
	
	private static Map<String, Unit> initUnitSymbolMap() {
		Map<String, Unit> typeMap = new HashMap<>();
		for(Unit type : values()) {
			if(!typeMap.containsKey(type.getSymbol())) {
				typeMap.put(type.getSymbol(), type);
			}
		}
		return typeMap;
	}
	
	public static Unit getUnitFromSymbol(String symbol) {
		return UNIT_SYMBOL_MAP.get(symbol);
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
	Unit(int unitId,String displayName,String symbol,Metric metric, boolean isLeft) {
		this.unitId = unitId;
		this.metric = metric;
		this.isSiUnit = true;
		this.displayName = displayName;
		this.symbol = symbol;
		this.isLeft = isLeft;
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
	Unit(int unitId,String displayName,String symbol,Metric metric,String fromSiUnit,String toSiUnit, boolean isLeft) {
		this.unitId = unitId;
		this.metric = metric;
		this.isSiUnit = false;
		this.fromSiUnit = fromSiUnit;
		this.toSiUnit = toSiUnit;
		this.displayName = displayName;
		this.symbol = symbol;
		this.isLeft = isLeft;
	}
	
	public boolean getIsLeft() {
		return isLeft;
	}
	public void setIsLeft(boolean isLeft) {
		this.isLeft = isLeft;
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