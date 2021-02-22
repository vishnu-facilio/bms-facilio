package com.facilio.bmsconsole.commands;

import java.text.DecimalFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeSet;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators.DateAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.BmsAggregateOperators.SpaceAggregateOperator;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.ReportBaseLineContext;
import com.facilio.report.context.ReportContext;
import com.facilio.report.context.ReportDataContext;
import com.facilio.report.context.ReportDataPointContext;
import com.facilio.report.context.ReportFieldContext;
import com.facilio.report.context.ReportGroupByField;
import com.facilio.report.util.ReportUtil;

public class ConstructTabularResultDataCommand extends ConstructReportDataCommand {
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportDataContext> reportData = (List<ReportDataContext>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		String xAlias = getxAlias(report);
		Map<String, Object> lookupMap = new HashMap<>();
		Collection<Map<String, Object>> transformedData = initList(xAlias, isTimeSeries(reportData));
		Map<String, Object> intermediateData = new HashMap<>();
		for (ReportDataContext data : reportData ) {
			Map<String, List<Map<String, Object>>> reportProps = data.getProps();
			if (reportProps != null && !reportProps.isEmpty()) {
				for (ReportDataPointContext dataPoint : data.getDataPoints()) {
					for (Map.Entry<String, List<Map<String, Object>>> entry : reportProps.entrySet()) {
						List<Map<String, Object>> props = entry.getValue();
						if (FacilioConstants.Reports.ACTUAL_DATA.equals(entry.getKey())) {
							constructData(report, dataPoint, props, null, transformedData, intermediateData,lookupMap);
						}
						else {
							constructData(report, dataPoint, props, data.getBaseLineMap().get(entry.getKey()), transformedData, intermediateData,lookupMap);
						}
					}
				}
			}
		}
		for (Map<String, Object> data : transformedData) {
			if(data.get("rows") != null) {
				Map<String, Object> rows = (Map<String, Object>) data.get("rows");
				for(String id : rows.keySet()) {
					if(lookupMap.get(id) != null) {
						Map<String, Object> lmap =(Map<String, Object>) lookupMap.get(id);
						rows.put(id, lmap.get(rows.get(id)));
					}
				}
			}
		}
		context.put(FacilioConstants.ContextNames.PIVOT_TABLE_DATA, transformedData);
		return false;
	}
	
	private void constructData(ReportContext report, ReportDataPointContext dataPoint, List<Map<String, Object>> props, ReportBaseLineContext baseLine, Collection<Map<String, Object>> transformedData, Map<String, Object> directHelperData, Map<String, Object> lookupMap) throws Exception {
		HashMap<ReportFieldContext, List<Long>> dpLookUpMap = new HashMap();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				Object xVal = prop.get(dataPoint.getxAxis().getField().getName());
				if (xVal != null) {
					xVal = getBaseLineAdjustedXVal(xVal, dataPoint.getxAxis(), baseLine);
					Object formattedxVal = formatVal(dataPoint.getxAxis(), report.getxAggrEnum(), xVal, null, false, dpLookUpMap,lookupMap);
					Object yVal = prop.get(ReportUtil.getAggrFieldName(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum()));
					Object minYVal = null, maxYVal = null;
					if (yVal != null) {
						yVal = formatVal(dataPoint.getyAxis(), dataPoint.getyAxis().getAggrEnum(), yVal, xVal, dataPoint.isHandleEnum(), dpLookUpMap,lookupMap);
						if (dataPoint.getyAxis().isFetchMinMax()) {
							minYVal = formatVal(dataPoint.getyAxis(), NumberAggregateOperator.MIN, prop.get(dataPoint.getyAxis().getField().getName()+"_min"), xVal, dataPoint.isHandleEnum(), dpLookUpMap,lookupMap);
							maxYVal = formatVal(dataPoint.getyAxis(), NumberAggregateOperator.MAX, prop.get(dataPoint.getyAxis().getField().getName()+"_max"), xVal, dataPoint.isHandleEnum(), dpLookUpMap,lookupMap);
						}

						StringJoiner key = new StringJoiner("|");
						Map<String, Object> data = null;
						if (dataPoint.getGroupByFields() != null && !dataPoint.getGroupByFields().isEmpty()) {
							data = new HashMap<>();
							Map<String, Object> rows = new HashMap<>();
							data.put("rows", rows);
							for (ReportGroupByField groupBy : dataPoint.getGroupByFields()) {
								FacilioField field = groupBy.getField();
								Object groupByVal = prop.get(field.getName());
								groupByVal = formatVal(groupBy, null, groupByVal, xVal, dataPoint.isHandleEnum(), dpLookUpMap,lookupMap);
								rows.put(groupBy.getAlias(), groupByVal);
								key.add(groupBy.getAlias()+"_"+groupByVal.toString());
							}
							data.put("rows", rows);
						}
						
							key.add(formattedxVal.toString());
							constructAndAddData(key.toString(), data, formattedxVal, yVal, minYVal, maxYVal, getyAlias(dataPoint, baseLine), report, dataPoint, transformedData, directHelperData);			
						}
				}
			}
			updateLookupMap(dpLookUpMap, report.getxAggrEnum(),lookupMap);
		}
	}
	
	private void constructAndAddData(String key, Map<String, Object> existingData, Object xVal, Object yVal, Object minYVal, Object maxYVal, String yAlias, ReportContext report, ReportDataPointContext dataPoint, Collection<Map<String, Object>> transformedData, Map<String, Object> intermediateData) {
		Map<String, Object> data = (Map<String, Object>) intermediateData.get(key);
		Map<String, Object> rows = new HashMap<>();
		Map<String, Object> datas = new HashMap<>();
		if (data == null) {
			data = existingData == null ? new HashMap<>() : existingData;
			if(data != null) {
				rows = data.get("rows")== null ? new HashMap<>() : (Map<String, Object>) data.get("rows");
				datas = data.get("data")== null ? new HashMap<>() : (Map<String, Object>) data.get("data");
			}
			rows.put(getxAlias(report), xVal);
			data.put("rows", rows);
			data.put("data", datas);
			intermediateData.put(key, data);
			transformedData.add(data);
		} else {
			rows = data.get("rows")== null ? new HashMap<>() : (Map<String, Object>) data.get("rows");
			datas = data.get("data")== null ? new HashMap<>() : (Map<String, Object>) data.get("data");
		}
		if (dataPoint.isHandleEnum()) {
			List<SimpleEntry<Long, Integer>> value = (List<SimpleEntry<Long, Integer>>) data.get(yAlias);
			if (value == null) {
				value = new ArrayList<>();
				datas.put(yAlias, value);
				data.put("data", datas);
			}
			value.add((SimpleEntry<Long, Integer>) yVal);
		}
		else {
			datas.put(yAlias, yVal);
			data.put("data", datas);
		}
	}
	
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private Object formatVal(ReportFieldContext reportFieldContext, AggregateOperator aggr, Object val, Object actualxVal, boolean handleEnum, HashMap<ReportFieldContext, List<Long>> dpLookUpMap , Map<String, Object> labelMap ) throws Exception {
		FacilioField field = reportFieldContext.getField();
		if (val == null) {
			return "";
		}
		
		switch (field.getDataTypeEnum()) {
			case DECIMAL:
				val = DECIMAL_FORMAT.format(val);
				break;
			case BOOLEAN:
				if (val.toString().equals("true")) {
					val = val.toString();
				}
				else if (val.toString().equals("false")) {
					val = val.toString();
				}
				if (handleEnum && actualxVal != null) {
					val = new SimpleEntry<Long, Integer>((Long)actualxVal, (Integer) val);
				}
				break;
			case ENUM:
			case SYSTEM_ENUM:
				Map<Integer,Object> enumMap = reportFieldContext.getEnumMap();
				val = enumMap.get(val);
				break;
			case DATE:
			case DATE_TIME:
				if (aggr != null && aggr instanceof DateAggregateOperator) {
					val = ((DateAggregateOperator)aggr).getAdjustedTimestamp((long) val);
				}
				break;
			case NUMBER:
				if (StringUtils.isNotEmpty(field.getName()) && field.getName().equals("siteId")) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					updateLookupMap(reportFieldContext, field.getFieldId(), null, modBean.getModule("site"),labelMap);
				}
				break;
			case LOOKUP:
				if (val instanceof Map) {
					val = ((Map) val).get("id");
				}
				List<Long> ids = dpLookUpMap.getOrDefault(reportFieldContext, new ArrayList<Long>());
				ids.add((Long)val);
				dpLookUpMap.put(reportFieldContext, ids);
				break;
			default:
				break;
		}
		return val;
	}
	
	private void updateLookupMap(ReportFieldContext reportFieldContext, long fieldId, String specialType, FacilioModule lookupModule, Map<String, Object> labelMap ) throws Exception {
		if (MapUtils.isEmpty(reportFieldContext.getLookupMap())) {
			Map<Long, Object> lookupMap = new HashMap<>();
			if (LookupSpecialTypeUtil.isSpecialType(specialType)) {
				List list = LookupSpecialTypeUtil.getObjects(specialType, null);
				lookupMap = LookupSpecialTypeUtil.getPrimaryFieldValues(specialType, list);
			} 
			else {
				lookupMap = this.getLookUpMap(specialType, lookupModule, null);
			}
			FacilioField field = reportFieldContext.getField();
			labelMap.put(reportFieldContext.getAlias(), lookupMap);
		}
	}
	
	private void updateLookupMap(HashMap<ReportFieldContext, List<Long>> dpLookUpMap, AggregateOperator aggr, Map<String, Object> labelMap ) throws Exception {
		if(MapUtils.isNotEmpty(dpLookUpMap)) {
			for (Map.Entry<ReportFieldContext, List<Long>> entry : dpLookUpMap.entrySet()) {
				Map<Long, Object> lookupMap;
				
				ReportFieldContext reportFieldContext = entry.getKey();
				List<Long> ids = entry.getValue();
				
				LookupField lookupField = (LookupField)reportFieldContext.getField();
				String specialType = lookupField.getSpecialType();
				FacilioModule lookupModule = lookupField.getLookupModule();
				
				if(aggr != null && aggr instanceof SpaceAggregateOperator && (reportFieldContext.getModuleName().equals(FacilioConstants.ModuleNames.ASSET_BREAKDOWN))) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					lookupModule = modBean.getModule(aggr.getStringValue());
				}
				if (LookupSpecialTypeUtil.isSpecialType(specialType)) {
					List list = LookupSpecialTypeUtil.getObjects(specialType, null);
					lookupMap = LookupSpecialTypeUtil.getPrimaryFieldValues(specialType, list);
				} else {
					lookupMap = this.getLookUpMap(specialType, lookupModule, ids);
				}
				labelMap.put(reportFieldContext.getAlias(), lookupMap);
			}
		}
	}

}
