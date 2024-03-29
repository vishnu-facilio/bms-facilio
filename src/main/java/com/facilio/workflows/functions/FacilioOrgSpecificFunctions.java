package com.facilio.workflows.functions;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators.CommonAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.time.DateTimeUtil;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.ORG_SPECIFIC_FUNCTION)
public class FacilioOrgSpecificFunctions {
	public Object etisalat_supplierActivePayments(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {

		Long supplierId = (long) Double.parseDouble(objects[0].toString());
		Long startTime = (long) Double.parseDouble(objects[1].toString());
		Long endTime = (long) Double.parseDouble(objects[2].toString());

		Map<String,Map<String,Map<String,Object>>> billPropMap = new HashMap<>();
		Map<String,String> regionPropMap = new HashMap<>();
		Map<String,String> costCentreMap = new HashMap<>();
		Map<String,Object> alertMap = new HashMap<>();
		Map<Long,List<Map<String, Object>>> invoiceDateMap = new HashMap<>();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		// bills
		FacilioModule billModule = modBean.getModule("custom_utilitybills");

		Map<String, FacilioStatus> billStatusMap = getStatusMap(billModule);

		Map<String, FacilioField> billFieldMap = FieldFactory.getAsMap(modBean.getAllFields("custom_utilitybills"));

		List<FacilioField> billSelectFields = new ArrayList<>();

		billSelectFields.add(DateAggregateOperator.MONTHANDYEAR.getSelectField(billFieldMap.get("date_5")));
		billSelectFields.add(billFieldMap.get("moduleState"));
		billSelectFields.add(CommonAggregateOperator.COUNT.getSelectField(billFieldMap.get("name")));
		billSelectFields.add(NumberAggregateOperator.SUM.getSelectField(billFieldMap.get("decimal_11")));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(billSelectFields)
				.table(billModule.getTableName())
				.andCustomWhere("MODULEID = ?", billModule.getModuleId())
				.andCondition(CriteriaAPI.getCondition(billFieldMap.get("supplier"), supplierId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(billFieldMap.get("date_5"), startTime+","+endTime, DateOperators.BETWEEN))
				.groupBy("date_5,moduleState")
				;

		List<Map<String, Object>> billProps = selectBuilder.get();


		for(Map<String, Object> billProp : billProps) {
			Object cost = billProp.get("decimal_11");
			Object count = billProp.get("name");
			Long moduleState = (Long) billProp.get("moduleState");
			String billMonth = (String) billProp.get("date_5");

			Map<String, Map<String, Object>> newMap = billPropMap.get(billMonth);

			if(newMap == null) {
				newMap = new HashMap<>();
			}

			Map<String,Object> temp = new HashMap<>();
			temp.put("count", count);
			temp.put("cost", cost);

			if(moduleState.equals(billStatusMap.get("alertprocessed").getId())) {
				newMap.put("disputeFree", temp);
			}
			else if(moduleState.equals(billStatusMap.get("disputed").getId())) {
				newMap.put("disputed", temp);
			}
			else if(moduleState.equals(billStatusMap.get("canceled").getId())) {
				newMap.put("canceled", temp);
			}

			billPropMap.put(billMonth, newMap);
		}

		billSelectFields = new ArrayList<>();

		billSelectFields.add(DateAggregateOperator.MONTHANDYEAR.getSelectField(billFieldMap.get("date_5")));
		billSelectFields.add(billFieldMap.get("singleline_4"));

		selectBuilder = new GenericSelectRecordBuilder()
				.select(billSelectFields)
				.table(billModule.getTableName())
				.andCustomWhere("MODULEID = ?", billModule.getModuleId())
				.andCondition(CriteriaAPI.getCondition(billFieldMap.get("supplier"), supplierId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(billFieldMap.get("date_5"), startTime+","+endTime, DateOperators.BETWEEN))
				.andCondition(CriteriaAPI.getCondition(billFieldMap.get("moduleState"), billStatusMap.get("alertprocessed").getId()+"", NumberOperators.EQUALS))
				.groupBy("date_5,singleline_4")
		;

		List<Map<String, Object>> regionProps = selectBuilder.get();

		for(Map<String, Object> regionProp : regionProps) {
			String month = (String) regionProp.get("date_5");
			String thisregion = (String)regionProp.get("singleline_4");
			if(thisregion != null) {
				String region = regionPropMap.get(month);
				if(region == null) {
					region = thisregion;
				}
				else {
					region = region + "," + thisregion;
				}
				regionPropMap.put(month, region);
			}
		}

		billSelectFields = new ArrayList<>();

		billSelectFields.add(DateAggregateOperator.MONTHANDYEAR.getSelectField(billFieldMap.get("date_5")));
		billSelectFields.add(billFieldMap.get("singleline_3"));

		selectBuilder = new GenericSelectRecordBuilder()
				.select(billSelectFields)
				.table(billModule.getTableName())
				.andCustomWhere("MODULEID = ?", billModule.getModuleId())
				.andCondition(CriteriaAPI.getCondition(billFieldMap.get("supplier"), supplierId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(billFieldMap.get("date_5"), startTime+","+endTime, DateOperators.BETWEEN))
				.andCondition(CriteriaAPI.getCondition(billFieldMap.get("moduleState"), billStatusMap.get("alertprocessed").getId()+"", NumberOperators.EQUALS))
				.groupBy("date_5,singleline_3")
		;

		List<Map<String, Object>> costCentreProps = selectBuilder.get();

		for(Map<String, Object> costCentreProp : costCentreProps) {
			String month = (String) costCentreProp.get("date_5");
			String thiscostCentre = (String)costCentreProp.get("singleline_3");
			if(thiscostCentre != null) {
				String costCentre = costCentreMap.get(month);
				if(costCentre == null) {
					costCentre = thiscostCentre;
				}
				else {
					costCentre = costCentre + "," + thiscostCentre;
				}
				costCentreMap.put(month, costCentre);
			}
		}

		// alerts

		FacilioModule alertModule = modBean.getModule("custom_alert");

		Map<String, FacilioStatus> alertStatusMap = getStatusMap(alertModule);

		Map<String, FacilioField> alertFieldMap = FieldFactory.getAsMap(modBean.getAllFields("custom_alert"));

		List<FacilioField> alertSelectFields = new ArrayList<>();

		alertSelectFields.add(DateAggregateOperator.MONTHANDYEAR.getSelectField(alertFieldMap.get("date_3")));
		alertSelectFields.add(CommonAggregateOperator.COUNT.getSelectField(alertFieldMap.get("name")));

		selectBuilder = new GenericSelectRecordBuilder()
				.select(alertSelectFields)
				.table(alertModule.getTableName())
				.andCustomWhere("MODULEID = ?", alertModule.getModuleId())
				.andCondition(CriteriaAPI.getCondition(alertFieldMap.get("lookup_1"), supplierId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(alertFieldMap.get("date_3"), startTime+","+endTime, DateOperators.BETWEEN))
				.andCondition(CriteriaAPI.getCondition(alertFieldMap.get("moduleState"), alertStatusMap.get("open").getId()+","+alertStatusMap.get("under").getId(), NumberOperators.EQUALS))
				.groupBy("date_3")
		;

		List<Map<String, Object>> alertProps = selectBuilder.get();

		for(Map<String, Object> alertProp :alertProps) {
			alertMap.put((String)alertProp.get("date_3"), alertProp.get("name"));
		}

		// invoice

		FacilioModule invoiceModule = modBean.getModule("custom_invoices");

		List<FacilioField> invoiceFields = modBean.getAllFields("custom_invoices");

		Map<String, FacilioField> invoiceFieldMap = FieldFactory.getAsMap(invoiceFields);

		Map<String, FacilioStatus> invoiceStatusMap = getStatusMap(invoiceModule);

		selectBuilder = new GenericSelectRecordBuilder()
				.select(invoiceFields)
				.table(invoiceModule.getTableName())
				.andCustomWhere("MODULEID = ?", invoiceModule.getModuleId())
				.andCondition(CriteriaAPI.getCondition(invoiceFieldMap.get("lookup"), supplierId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(invoiceFieldMap.get("date"), startTime+","+endTime, DateOperators.BETWEEN))
				.andCondition(CriteriaAPI.getCondition(invoiceFieldMap.get("moduleState"), invoiceStatusMap.get("close").getId()+","+invoiceStatusMap.get("rejected").getId(), NumberOperators.NOT_EQUALS))
		;

		List<Map<String, Object>> props = selectBuilder.get();

		for(Map<String, Object> invoice :props) {
			List<Map<String, Object>> invoiceList = invoiceDateMap.get((long)invoice.get("date"));
			if(invoiceList == null) {
				invoiceList = new ArrayList<Map<String,Object>>();
			}
			invoiceList.add(invoice);
			invoiceDateMap.put((long)invoice.get("date"), invoiceList);
		}

//			Map<String,Map<String,Map<String,Object>>> billPropMap = new HashMap<>();
//			Map<String,String> regionPropMap = new HashMap<>();
//			Map<String,String> costCentreMap = new HashMap<>();
//			Map<String,Object> alertMap = new HashMap<>();
//			Map<Long,List<Map<String, Object>>> invoiceDateMap = new HashMap<>();

		startTime = DateTimeUtil.getMonthStartTimeOf(startTime);

		JSONObject resultMap = new JSONObject();

		while(startTime < endTime) {
			JSONObject resJSON = new JSONObject();
			JSONObject statusJSON = new JSONObject();

			String month = DateTimeUtil.getFormattedTime(startTime,"yyyy MM");

			List<Map<String, Object>> invoices = invoiceDateMap.get(startTime);

			if(invoices != null) {
				resJSON.put("invoices", invoices);
			}

			Map<String, Map<String, Object>> statusMap = billPropMap.get(month);

			if(statusMap != null) {

				// disputed handling
				Map<String, Object> countCostMap = statusMap.get("disputed");

				if(countCostMap != null) {
					JSONObject disputedMap = new JSONObject();
					disputedMap.put("cost", countCostMap.get("cost"));
					disputedMap.put("count", countCostMap.get("count"));
					disputedMap.put("alertCount", alertMap.get(month));

					statusJSON.put("disputed", disputedMap);
				}

				// canceled handling
				countCostMap = statusMap.get("canceled");
				if(countCostMap != null) {
					JSONObject canceled = new JSONObject();
					canceled.put("cost", countCostMap.get("cost"));
					canceled.put("count", countCostMap.get("count"));

					statusJSON.put("canceled", canceled);
				}

				// disputeFree handling

				countCostMap = statusMap.get("disputeFree");

				if(countCostMap != null) {
					JSONObject disputeFree = new JSONObject();
					disputeFree.put("cost", countCostMap.get("cost"));
					disputeFree.put("count", countCostMap.get("count"));
					disputeFree.put("region", regionPropMap.get(month));
					disputeFree.put("costCenter", costCentreMap.get(month));

					statusJSON.put("disputeFree", disputeFree);
				}

				resJSON.put("counts", statusJSON);
			}

			if(!resJSON.isEmpty()) {
				resultMap.put(""+startTime, resJSON);
			}

			startTime = DateTimeUtil.addMonths(startTime, 1);
		}

		return resultMap;
	}

	private Map<String, FacilioStatus> getStatusMap(FacilioModule module) throws Exception {

		List<FacilioStatus> statuses = TicketAPI.getAllStatus(module, false);

		Map<String,FacilioStatus> statusMap = new HashMap<>();

		for(FacilioStatus status :statuses) {
			statusMap.put(status.getStatus(), status);
		}
		return statusMap;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects == null || objects.length == 0) {
			throw new FunctionParamException("Required Object is null or empty");
		}
	}
}