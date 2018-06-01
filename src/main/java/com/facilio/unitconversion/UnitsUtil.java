package com.facilio.unitconversion;

import java.math.BigDecimal;

import com.udojava.evalex.Expression;

public class UnitsUtil {

	public final static String FORMULA_SI_PLACE_HOLDER_STRING = "si";
	public final static String FORMULA_THIS_PLACE_HOLDER_STRING = "this";
	
	public static Double convert(Object value,Unit from,Unit to) {
		
		if(from.equals(to)) {
			return (Double)value;
		}
		
		if(!from.isSiUnit()) {
			String toSiUnitFormula = from.getToSiUnit();
			toSiUnitFormula = toSiUnitFormula.replaceAll(FORMULA_THIS_PLACE_HOLDER_STRING, value.toString());
			Expression expression = new Expression(toSiUnitFormula);
			value = expression.eval();
		}
		
		if(!to.isSiUnit()) {
			String fromSiUnitFormula = to.getFromSiUnit();
			fromSiUnitFormula = fromSiUnitFormula.replaceAll(FORMULA_SI_PLACE_HOLDER_STRING, value.toString());
			Expression expression = new Expression(fromSiUnitFormula);
			value = expression.eval();
		}
		return Double.valueOf(value.toString());
	}
	
	public static Double convertToSiUnit(Object value,Unit from) {
		
		return convert(value, from, from.getMetric().getSiUnit());
	}
}
