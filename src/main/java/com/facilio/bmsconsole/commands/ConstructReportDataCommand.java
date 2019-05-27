package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.AggregateOperator.DateAggregateOperator;
import com.facilio.modules.AggregateOperator.NumberAggregateOperator;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.report.context.*;
import com.facilio.report.util.ReportUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.text.DecimalFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class ConstructReportDataCommand implements Command {

	private Collection<Map<String, Object>> initList(String sortAlias, boolean isTimeSeries) { //In case we wanna implement a sorted list
	    if (isTimeSeries) {
            return new TreeSet<Map<String, Object>>((data1, data2) -> Long.compare((long) data1.get(sortAlias), (Long) data2.get(sortAlias)));
        }
	    else {
	        return new ArrayList<>();
        }
	}
	
//	private Map<Long, Map<Long, Object>> labelMap = new HashMap<>();

    private boolean isTimeSeries (List<ReportDataContext> reportData) { //Temporary check
	    if (CollectionUtils.isNotEmpty(reportData)) {
	        ReportDataPointContext dp = reportData.get(0).getDataPoints().get(0);
	        if (CollectionUtils.isNotEmpty(dp.getGroupByFields())) {
	        	return false;
	        }
	        if (dp.getxAxis().getDataTypeEnum() == FieldType.DATE_TIME || dp.getxAxis().getDataTypeEnum() == FieldType.DATE) {
	            return true;
            }
        }
	    return false;
    }

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ReportDataContext> reportData = (List<ReportDataContext>) context.get(FacilioConstants.ContextNames.REPORT_DATA);
		ReportContext report = (ReportContext) context.get(FacilioConstants.ContextNames.REPORT);
		String xAlias = getxAlias(report);
		Collection<Map<String, Object>> transformedData = initList(xAlias, isTimeSeries(reportData));
		Map<String, Object> intermediateData = new HashMap<>();
		for (ReportDataContext data : reportData ) {
			Map<String, List<Map<String, Object>>> reportProps = data.getProps();
			if (reportProps != null && !reportProps.isEmpty()) {
				for (ReportDataPointContext dataPoint : data.getDataPoints()) {
					for (Map.Entry<String, List<Map<String, Object>>> entry : reportProps.entrySet()) {
						List<Map<String, Object>> props = entry.getValue();
						if (FacilioConstants.Reports.ACTUAL_DATA.equals(entry.getKey())) {
							constructData(report, dataPoint, props, null, transformedData, intermediateData);
						}
						else {
							constructData(report, dataPoint, props, data.getBaseLineMap().get(entry.getKey()), transformedData, intermediateData);
						}
					}
				}
			}
		}
		JSONObject data = new JSONObject();
		data.put(FacilioConstants.ContextNames.DATA_KEY, transformedData);
//		data.put(FacilioConstants.ContextNames.LABEL_MAP, labelMap);
		context.put(FacilioConstants.ContextNames.REPORT_SORT_ALIAS, xAlias);
		context.put(FacilioConstants.ContextNames.REPORT_DATA, data);
		return false;
	}
	
	private void constructData(ReportContext report, ReportDataPointContext dataPoint, List<Map<String, Object>> props, ReportBaseLineContext baseLine, Collection<Map<String, Object>> transformedData, Map<String, Object> directHelperData) throws Exception {
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				Object xVal = prop.get(dataPoint.getxAxis().getField().getName());
				if (xVal != null) {
					xVal = getBaseLineAdjustedXVal(xVal, dataPoint.getxAxis(), baseLine);
					Object formattedxVal = formatVal(dataPoint.getxAxis(), report.getxAggrEnum(), xVal, null, false);
					Object yVal = prop.get(ReportUtil.getAggrFieldName(dataPoint.getyAxis().getField(), dataPoint.getyAxis().getAggrEnum()));
					Object minYVal = null, maxYVal = null;
					if (yVal != null) {
						yVal = formatVal(dataPoint.getyAxis(), dataPoint.getyAxis().getAggrEnum(), yVal, xVal, dataPoint.isHandleEnum());
						if (dataPoint.getyAxis().isFetchMinMax()) {
							minYVal = formatVal(dataPoint.getyAxis(), NumberAggregateOperator.MIN, prop.get(dataPoint.getyAxis().getField().getName()+"_min"), xVal, dataPoint.isHandleEnum());
							maxYVal = formatVal(dataPoint.getyAxis(), NumberAggregateOperator.MAX, prop.get(dataPoint.getyAxis().getField().getName()+"_max"), xVal, dataPoint.isHandleEnum());
						}

						StringJoiner key = new StringJoiner("|");
						key.add(formattedxVal.toString());
						Map<String, Object> data = null;
						if (dataPoint.getGroupByFields() != null && !dataPoint.getGroupByFields().isEmpty()) {
							data = new HashMap<>();
							for (ReportGroupByField groupBy : dataPoint.getGroupByFields()) {
								FacilioField field = groupBy.getField();
								Object groupByVal = prop.get(field.getName());
								groupByVal = formatVal(groupBy, null, groupByVal, xVal, dataPoint.isHandleEnum());
								data.put(groupBy.getAlias(), groupByVal);
								key.add(groupBy.getAlias()+"_"+groupByVal.toString());
							}
						}
						constructAndAddData(key.toString(), data, formattedxVal, yVal, minYVal, maxYVal, getyAlias(dataPoint, baseLine), report, dataPoint, transformedData, directHelperData);
					}
				}
			}
		}
	}
	
	private void constructAndAddData(String key, Map<String, Object> existingData, Object xVal, Object yVal, Object minYVal, Object maxYVal, String yAlias, ReportContext report, ReportDataPointContext dataPoint, Collection<Map<String, Object>> transformedData, Map<String, Object> intermediateData) {
		Map<String, Object> data = (Map<String, Object>) intermediateData.get(key);
		if (data == null) {
			data = existingData == null ? new HashMap<>() : existingData;
			data.put(getxAlias(report), xVal);
			intermediateData.put(key, data);
			transformedData.add(data);
		}
		
		if (dataPoint.isHandleEnum()) {
			List<SimpleEntry<Long, Integer>> value = (List<SimpleEntry<Long, Integer>>) data.get(yAlias);
			if (value == null) {
				value = new ArrayList<>();
				data.put(yAlias, value);
			}
			value.add((SimpleEntry<Long, Integer>) yVal);
		}
		else {
			data.put(yAlias, yVal);

			if (dataPoint.getyAxis().isFetchMinMax()) {
				data.put(yAlias+".min", minYVal);
				data.put(yAlias+".max", maxYVal);
			}
		}
	}
	
	private String getyAlias(ReportDataPointContext dataPoint, ReportBaseLineContext baseLine) {
		return baseLine == null ? dataPoint.getAliases().get(FacilioConstants.Reports.ACTUAL_DATA) : dataPoint.getAliases().get(baseLine.getBaseLine().getName());
	}
	
	private String getxAlias(ReportContext report) {
		return report.getxAlias() == null ? FacilioConstants.ContextNames.REPORT_DEFAULT_X_ALIAS : report.getxAlias();
	}
	
	private Object getBaseLineAdjustedXVal(Object xVal, ReportFieldContext xAxis, ReportBaseLineContext baseLine) throws Exception {
		if (baseLine != null) {
			switch (xAxis.getField().getDataTypeEnum()) {
				case DATE:
				case DATE_TIME:
					return (long) xVal + baseLine.getDiff();
				default:
					break;
			}
		}
		return xVal;
	}
	
	private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
	private Object formatVal(ReportFieldContext reportFieldContext, AggregateOperator aggr, Object val, Object actualxVal, boolean handleEnum) throws Exception {
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
					val = 1;
				}
				else if (val.toString().equals("false")) {
					val = 0;
				}
				if (handleEnum && actualxVal != null) {
					val = new SimpleEntry<Long, Integer>((Long)actualxVal, (Integer) val);
				}
				break;
			case ENUM:
				if (handleEnum && actualxVal != null) {
					val = new SimpleEntry<Long, Integer>((Long)actualxVal, (Integer) val);
				}
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
					updateLookupMap(reportFieldContext, field.getFieldId(), null, modBean.getModule("site"));
				}
				break;
			case LOOKUP:
				LookupField lookupField = (LookupField) field;
				updateLookupMap(reportFieldContext, lookupField.getFieldId(), lookupField.getSpecialType(), lookupField.getLookupModule());
				if (val instanceof Map) {
					val = ((Map) val).get("id");
				}
				break;
			default:
				break;
		}
		return val;
	}
	
	private void updateLookupMap(ReportFieldContext reportFieldContext, long fieldId, String specialType, FacilioModule lookupModule) throws Exception {
		if (MapUtils.isEmpty(reportFieldContext.getLookupMap())) {
			String moduleName = null;
			if (LookupSpecialTypeUtil.isSpecialType(specialType)) {
				moduleName = specialType;
			} else {
				moduleName = lookupModule.getName();
			}
			
			Map<Long, Object> lookupMap;
			if (LookupSpecialTypeUtil.isSpecialType(specialType)) {
				List list = LookupSpecialTypeUtil.getObjects(specialType, null);
				lookupMap = LookupSpecialTypeUtil.getPrimaryFieldValues(specialType, list);
			} else {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioField mainField = modBean.getPrimaryField(moduleName);
				
				List<FacilioField> selectFields = new ArrayList<>();
				selectFields.add(mainField);
				selectFields.add(FieldFactory.getIdField(lookupModule));
				SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder()
						.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(lookupModule.getName()))
						.select(selectFields)
						.module(lookupModule)
						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(lookupModule));

				List<Map<String,Object>> asProps = builder.getAsProps();
				lookupMap = new HashMap<>();
				for (Map<String, Object> map : asProps) {
					lookupMap.put((Long) map.get("id"), (String) map.get(mainField.getName()));
				}
			}
//			labelMap.put(fieldId, lookupMap);
			reportFieldContext.setLookupMap(lookupMap);
		}
	}

}
