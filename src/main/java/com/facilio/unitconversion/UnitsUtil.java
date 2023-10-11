package com.facilio.unitconversion;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.OrgUnitsContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;
import com.udojava.evalex.Expression;

import lombok.extern.log4j.Log4j;

@Log4j
public class UnitsUtil {

	public final static String FORMULA_SI_PLACE_HOLDER_STRING = "si";
	public final static String FORMULA_THIS_PLACE_HOLDER_STRING = "this";
	
	public final static int unitMultiplierboundPercentage = 20; 
	
	
	public static Unit getUnitMultiplierResult(Unit currentUnit,double multiplier) {
		double currentUnitMultiplerTimes = currentUnit.getMultiplierTimes();
		double neededUnitMultiplier = currentUnitMultiplerTimes * multiplier;
		
		double neededUnitMultiplierLowerLimit = neededUnitMultiplier - (neededUnitMultiplier * unitMultiplierboundPercentage /100);
		double neededUnitMultiplierUpperLimit = neededUnitMultiplier + (neededUnitMultiplier * unitMultiplierboundPercentage /100);
		
		Collection<Unit> units = Unit.getUnitsForMetric(currentUnit.getMetric());
		
		for(Unit unit :units) {
			double multiplerTimes = unit.getMultiplierTimes();
			if(currentUnit != unit && multiplerTimes > 0 && multiplerTimes > neededUnitMultiplierLowerLimit && multiplerTimes < neededUnitMultiplierUpperLimit) {
				return unit;
			}
		}
		return null;
	}
	
	public static Double convert(Object value,Unit from,Unit to) {
		try {
			if(value == null ) {
				return null;
			}
			if(from.getMetric().getMetricId() == Metric.CURRENCY.getMetricId()) {
				return Double.parseDouble(value.toString());
			}
			if(from.equals(to)) {
				return Double.parseDouble(value.toString());
			}
			
			if(!from.isSiUnit()) {
				String toSiUnitFormula = from.getToSiUnit();
				toSiUnitFormula = toSiUnitFormula.replaceAll(FORMULA_THIS_PLACE_HOLDER_STRING, value.toString());
				Expression expression = new Expression(toSiUnitFormula).setPrecision(11);
				value = expression.eval();
			}
			
			if(!to.isSiUnit()) {
				String fromSiUnitFormula = to.getFromSiUnit();
				fromSiUnitFormula = fromSiUnitFormula.replaceAll(FORMULA_SI_PLACE_HOLDER_STRING, value.toString());
				Expression expression = new Expression(fromSiUnitFormula).setPrecision(11);
				value = expression.eval();
			}
			double returnValue =  Double.parseDouble(value.toString());
			
			if(to.isRoundOffNeeded()) {
				returnValue = Double.parseDouble(String.valueOf(Math.round(returnValue)));
			}
			
			return returnValue;
		}
		catch(Exception e) {
			LOGGER.error("value -- "+value + " from -- "+from +" to -- "+to);
			LOGGER.error(e.getMessage(), e);
			throw e;
		}
	}
	
	public static Double convert(Object value,int from,int to) {
		if(value == null ) {
			return null;
		}
		if(from < 0 || to < 0) {
			return Double.parseDouble(value.toString());
		}
		return convert(value,Unit.valueOf(from),Unit.valueOf(to));
	}
	
	public static Double convertToSiUnit(Object value,Unit from) {
		if(value == null) {
			return null;
		}
		if(from.getMetric().getMetricId() == Metric.CURRENCY.getMetricId()) {
			return Double.parseDouble(value.toString());
		}
		return convert(value, from, Unit.valueOf(from.getMetric().getSiUnitId()));
	}
	public static Double convertToOrgDisplayUnit(Object value,Unit from) throws Exception {
		if(value == null) {
			return null;
		}
		if(from.getMetric().getMetricId() == Metric.CURRENCY.getMetricId()) {
			return Double.parseDouble(value.toString());
		}
		Unit orgDisplayUnit = AccountUtil.getOrgBean().getOrgDisplayUnit(from.getMetric().getMetricId());
		return convert(value, from, orgDisplayUnit);
	}

	private static Number castConvertedValue (Object oldVal, Double convertedValue) {
		if (oldVal instanceof Integer) {
			return FacilioUtil.parseInt(convertedValue);
		}
		else if (oldVal instanceof  Long) {
			return FacilioUtil.parseLong(convertedValue);
		}
		return convertedValue;
	}
	
	public static Unit getDisplayUnit(NumberField numberField) throws Exception {
		
		if(numberField.getMetric() > 0) {
			Unit displayUnit;
			if(numberField.getUnitId() > 0) {
				displayUnit = Unit.valueOf(numberField.getUnitId());
			}
			else {
				displayUnit = AccountUtil.getOrgBean().getOrgDisplayUnit(numberField.getMetric());
			}
			return displayUnit;
		}
		return null;
	}

	public static Number convertToDisplayUnit(Object value,NumberField numberField) throws Exception {

		if(numberField.getMetric() > 0 && value != null) {
			if(numberField.getMetric() == Metric.CURRENCY.getMetricId()) {
				return parseNumber(value, numberField);
			}
			Double convertedValue = -1d;
			if(numberField.getUnitId() > 0) {
				Unit siUnit = Unit.valueOf(Metric.valueOf(numberField.getMetric()).getSiUnitId());
				convertedValue = convert(value, siUnit.getUnitId(), numberField.getUnitId());
			}
			else {
				convertedValue = convertToOrgDisplayUnitFromSi(value, numberField.getMetric());
			}
			return castConvertedValue(value, convertedValue);
		}
		
		return parseNumber(value, numberField);
	}

	private static Number parseNumber (Object value, NumberField numberField) {
		return value instanceof Number ? (Number) value : (Number) (numberField.getDataTypeEnum() == null ? FacilioUtil.castOrParseValueAsPerType(FieldType.DECIMAL, value) : FacilioUtil.castOrParseValueAsPerType(numberField.getDataTypeEnum(), value));
	}
	
	public static Double convertToOrgDisplayUnitFromSi(Object value,int metricId) throws Exception {
		if(value == null) {
			return null;
		}
		if(metricId == Metric.CURRENCY.getMetricId()) {
			return Double.parseDouble(value.toString());
		}
		Unit orgDisplayUnit = AccountUtil.getOrgBean().getOrgDisplayUnit(metricId);
		return convert(value, Unit.valueOf(Metric.valueOf(metricId).getSiUnitId()) , orgDisplayUnit);
	}
	
	public static void updateOrgUnitsList(JSONObject metricUnitMap) throws Exception {
		
		List<OrgUnitsContext> orgUnitsContexts = AccountUtil.getOrgBean().getOrgUnitsList();
		
		for(OrgUnitsContext orgUnitsContext :orgUnitsContexts) {
			
			Integer unit = (Integer) metricUnitMap.get(orgUnitsContext.getMetric());
			
			if(!unit.equals(orgUnitsContext.getUnit())) {
				
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getOrgUnitsModule().getTableName())
						.fields(FieldFactory.getOrgUnitsFields())
						.andCustomWhere("ID = ?", orgUnitsContext.getId());

				Map<String, Object> props = new HashMap<>();
				props.put("unit", unit);
				updateBuilder.update(props);
			}
		}
	}
	
	public static boolean updateOrgUnit(int metric,int unit) throws Exception {
		
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getOrgUnitsModule().getTableName())
					.fields(FieldFactory.getOrgUnitsFields())
					.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
					.andCustomWhere("METRIC = ? ", metric);

			Map<String, Object> props = new HashMap<>();
			props.put("unit", unit);
			int updatedRows = updateBuilder.update(props);
			if(updatedRows > 0) {
				return true;
			}
			return false;
	}
	
	public static Unit getOrgDisplayUnit ( int metricId ) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOrgUnitsModule().getTableName())
				.select(FieldFactory.getOrgUnitsFields())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getOrgUnitsModule()))
				.andCondition(CriteriaAPI.getCondition("METRIC", "metric", metricId+"", NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			
			Map<String, Object> prop = props.get(0);
			int unitid = (int) prop.get("unit");
			return Unit.valueOf(unitid);
		}
		return Unit.valueOf(Metric.valueOf(metricId).getSiUnitId());
	}
	
	public static Unit getOrgDisplayUnit ( Metric metric ) throws Exception {
		return  getOrgDisplayUnit(metric.getMetricId());
	} 
	
	public static List<OrgUnitsContext> getOrgUnitsList() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOrgUnitsModule().getTableName())
				.select(FieldFactory.getOrgUnitsFields());
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getOrgUnitsModule()));

		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			List<OrgUnitsContext> orgUnitsContexts = new ArrayList<>();
			for(Map<String, Object> prop:props) {
				OrgUnitsContext reportContext = FieldUtil.getAsBeanFromMap(prop, OrgUnitsContext.class);
				orgUnitsContexts.add(reportContext);
			}
			return orgUnitsContexts;
		}
		return null;
	}
	
	
	
	
}
