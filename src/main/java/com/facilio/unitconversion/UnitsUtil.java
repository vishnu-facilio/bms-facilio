package com.facilio.unitconversion;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.OrgUnitsContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.NumberField;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.udojava.evalex.Expression;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnitsUtil {

	public final static String FORMULA_SI_PLACE_HOLDER_STRING = "si";
	public final static String FORMULA_THIS_PLACE_HOLDER_STRING = "this";
	
	public static Double convert(Object value,Unit from,Unit to) {
		if(value == null ) {
			return null;
		}
		if(from.equals(to)) {
			return Double.parseDouble(value.toString());
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
	
	public static Double convert(Object value,int from,int to) {
		if(value == null ) {
			return null;
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
		Unit orgDisplayUnit = getOrgDisplayUnit(AccountUtil.getCurrentOrg().getOrgId(),from.getMetric().getMetricId());
		return convert(value, from, orgDisplayUnit);
	}
	public static Object convertToDisplayUnit(Object value,NumberField numberField) throws Exception {

		if(numberField.getMetric() > 0 && value != null) {
			if(numberField.getMetric() == Metric.CURRENCY.getMetricId()) {
				return Double.parseDouble(value.toString());
			}
			if(numberField.getUnitId() > 0) {
				Unit siUnit = Unit.valueOf(Metric.valueOf(numberField.getMetric()).getSiUnitId());
				value = convert(value, siUnit.getUnitId(), numberField.getUnitId());
			}
			else {
				value = convertToOrgDisplayUnitFromSi(value, numberField.getMetric());
			}
			return Double.parseDouble(value.toString());
		}
		
		return value;
	}
	
	public static Double convertToOrgDisplayUnitFromSi(Object value,int metricId) throws Exception {
		if(value == null) {
			return null;
		}
		if(metricId == Metric.CURRENCY.getMetricId()) {
			return Double.parseDouble(value.toString());
		}
		Unit orgDisplayUnit = getOrgDisplayUnit(AccountUtil.getCurrentOrg().getOrgId(),metricId);
		return convert(value, Unit.valueOf(Metric.valueOf(metricId).getSiUnitId()) , orgDisplayUnit);
	}
	
	public static void updateOrgUnitsList(JSONObject metricUnitMap) throws Exception {
		
		List<OrgUnitsContext> orgUnitsContexts = UnitsUtil.getOrgUnitsList();
		
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
	
	public static Unit getOrgDisplayUnit(Long orgid, int metricId) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOrgUnitsModule().getTableName())
				.select(FieldFactory.getOrgUnitsFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getOrgUnitsModule()))
				.andCondition(CriteriaAPI.getCondition("METRIC", "metric", metricId+"", NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			
			Map<String, Object> prop = props.get(0);
			int unitid = (int) prop.get("unit");
			return Unit.valueOf(unitid);
		}
		return Unit.valueOf(Metric.valueOf(metricId).getSiUnitId());
	}
	
	public static Unit getOrgDisplayUnit(Long orgid, Metric metric) throws Exception {
		return getOrgDisplayUnit(orgid, metric.getMetricId());
	} 
	
	public static List<OrgUnitsContext> getOrgUnitsList() throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getOrgUnitsModule().getTableName())
				.select(FieldFactory.getOrgUnitsFields())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getOrgUnitsModule()));

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
