package com.facilio.unitconversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.OrgUnitsContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.udojava.evalex.Expression;

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
	
	public static Double convertToSiUnit(Object value,Unit from) {
		return convert(value, from, Unit.valueOf(from.getMetric().getSiUnitId()));
	}
	public static Double convertToOrgDisplayUnit(Object value,Unit from) throws Exception {
		Unit orgDisplayUnit = getOrgDisplayUnit(AccountUtil.getCurrentOrg().getOrgId(),from.getMetric().getMetricId());
		return convert(value, from, orgDisplayUnit);
	}
	
	public static Double convertToOrgDisplayUnitFromSi(Object value,int metricId) throws Exception {
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
				int updatedRows = updateBuilder.update(props);
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
