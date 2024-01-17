package com.facilio.cards.util;

import java.util.*;

import com.facilio.bmsconsole.context.WidgetCardContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.WidgetStaticContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;

public class CardUtil {
	
	public static boolean isGetDataFromEnum(String key) {
		
		return CardType.CARD_TYPE_BY_NAME.keySet().contains(key);
	}
	
	public static boolean isExtraCard(String key) {
		
		if(key.equals("readingWithGraphCard") || key.contains("emrill") || key.equals("resourceAlarmBar") || key.equals("alarmbarwidget")) {
			return true;
		}
		return false;
	}
	
	public static boolean isDynamicWorkflowGenCard(String key) {
		
		if(CardType.getCardType(key) != null && CardType.getCardType(key).isDynamicWfGeneratingCard()) {
			return true;
		}
		return false;
	}
	
	public static Object getUnit(JSONObject params) throws Exception {
		
		if(params == null) {
			return null;
		}
		if(params.get("moduleName") != null && params.get("fieldName") != null) {
			FacilioField field = getField((String) params.get("moduleName"), (String) params.get("fieldName"));
			if(field instanceof NumberField) {
				NumberField numberField = (NumberField) field;
				if(numberField.getUnitId() > 0) {
					return numberField.getUnitEnum();
				}
			}
			int metricId = getMetic((String) params.get("moduleName"), (String) params.get("fieldName"));
			
			if(metricId > 0) {
				return AccountUtil.getOrgBean().getOrgDisplayUnit(metricId);
			}
			if(field instanceof NumberField) {
				NumberField numberField = (NumberField) field;
				if(numberField.getUnit() != null) {
					Unit unit = Unit.getUnitFromSymbol(numberField.getUnit());
					if(unit == null) {
						return numberField.getUnit();
					}
					return unit;
				}
			}
			
		}
		return null;
	}
	
	public static int getMetic(String moduleName,String fieldName) throws Exception {
		
		if(moduleName != null && fieldName != null) {
			
			FacilioField field = getField(moduleName, fieldName);
			return getMetric(field);
		}
		return -1;
	}
	
	public static int getMetric(FacilioField field) {
		if(field instanceof NumberField) {
			NumberField numberField = (NumberField) field;
			return numberField.getMetric();
		}
		return -1;
	}
	
	public static int getMetic(Long fieldId) throws Exception {
		
		if(fieldId != null) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField field = modBean.getField(fieldId);
			getMetric(field);
		}
		return -1;
	}
	
	public static FacilioField getField(String moduleName,String fieldName) throws Exception {
		
		if(moduleName != null && fieldName != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField field = modBean.getField(fieldName, moduleName);
			return field;
		}
		return null;
	}

	public static Object getBooleanStringValue(Object wfResult, JSONObject paramsJson)  throws Exception {
		
		FacilioField field = getField((String) paramsJson.get("moduleName"), (String) paramsJson.get("fieldName"));
		return FieldUtil.getBooleanResultString(wfResult, field);
	}
	
	public static Object getWorkflowResultForClient(Object wfResult,WidgetStaticContext widgetStaticContext) throws Exception {
		if(wfResult instanceof Boolean) {
			wfResult = CardUtil.getBooleanStringValue(wfResult,widgetStaticContext.getParamsJson());
		}
		else if(wfResult instanceof Double) {
			Double value =  (Double) wfResult;
			wfResult = FacilioUtil.DECIMAL_FORMAT.format(value);
		}
		else if(wfResult instanceof Map) {
			Map<String, Object> expResult = (Map<String, Object>) wfResult;
			Set<String> keys = expResult.keySet();
			for(String key : keys) {
				Object obj = expResult.get(key);
				
				if(obj instanceof Double) {
					Double value =  (Double) obj;
					expResult.put(key, FacilioUtil.DECIMAL_FORMAT.format(value));
				}
				else if(obj instanceof String && FacilioUtil.isNumeric(obj.toString())) {
					Double value =  Double.parseDouble(obj.toString());
					expResult.put(key, FacilioUtil.DECIMAL_FORMAT.format(value));
				}
			}
			return expResult;
		}
		return wfResult;
	}

	public static JSONObject fillParamJsonForFahuCard(long orgId, JSONObject paramsJson) {
		
		if(orgId == 146l) {
			paramsJson = paramsJson != null ? paramsJson : new JSONObject();
			
			paramsJson.put("runStatusModule", "runstatus");
			paramsJson.put("runStatusField", "runstatus");
			paramsJson.put("valveFeedbackModule", "valvefeedback");
			paramsJson.put("valveFeedbackField", "valvefeedback");
			paramsJson.put("tripStatusModule", "tripstatus");
			paramsJson.put("tripStatusField", "tripstatus");
			paramsJson.put("autoStatusModule", "automanualstatus");
			paramsJson.put("autoStatusField", "automanualstatus");
		}
		return paramsJson;
	}
	
	public static String appendCardPrefixSuffixScript (String script) {
		StringBuilder sb = new StringBuilder();
		sb.append("Map cardLayout(Map params) {");
		sb.append("\n");
		sb.append("result = {};");
		sb.append("\n");
		
		sb.append(script);
		
		sb.append("\n");
		sb.append("return result;");
		sb.append("\n");
		sb.append("}");
		
		return sb.toString();
	}
	
	public static String getParamsAsScriptVariables(HashMap<String, Object> valueMap) {
		StringBuilder sb = new StringBuilder();
		if (valueMap != null && !valueMap.isEmpty()) {
			Iterator<String> itr = valueMap.keySet().iterator();
			while (itr.hasNext()) {
				String key = itr.next();
				sb.append("\n");
				sb.append(key);
				sb.append(" = ");
				if (valueMap.containsKey(key)) {
					sb.append(valueMap.get(key));
				}
				else {
					sb.append("null");
				}
				sb.append(";");
				sb.append("\n");
			}
		}
		return sb.toString();
	}
	public static List<WidgetCardContext> getChildCards(Long parentId) throws Exception {
		List<WidgetCardContext> childCards= new ArrayList<>();
		if(parentId != null && parentId > 0){
			GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
					.table(ModuleFactory.getWidgetCardModule().getTableName())
					.select(FieldFactory.getWidgetCardFields())
					.andCondition(CriteriaAPI.getCondition("PARENT_ID","parent_id", String.valueOf(parentId), NumberOperators.EQUALS));
			List<Map<String, Object>> props = selectRecordBuilder.get();
			if (props != null && !props.isEmpty()) {
				for (Map<String, Object>prop: props){
					childCards.add(FieldUtil.getAsBeanFromMap(prop, WidgetCardContext.class));
				}
			}
		}
		return childCards;
	}
	public static List<JSONObject> getChildCardsResponse(List<WidgetCardContext> childCards) {
		List<JSONObject> childResponse = new ArrayList<>();
		for(WidgetCardContext childCard : childCards){
			JSONObject widgetJson = new JSONObject();
			widgetJson.put("cardLayout", childCard.getCardLayout());
			widgetJson.put("link_name", childCard.getLinkName());
			widgetJson.put("title", childCard.getHeaderText());
			widgetJson.put("id", childCard.getId());
			widgetJson.put("label", childCard.getWidgetName());
			childResponse.add(widgetJson);
		}
		return childResponse;
	}
	public static JSONObject getDrillDownObj(JSONObject cardDrillDown,JSONObject cardParams, long cardId) {
		JSONObject resultJson = new JSONObject();
		if(cardDrillDown != null) {
			JSONObject drillDownObj = (JSONObject) cardDrillDown.get("default");
			String action = (String) drillDownObj.get("actionType");
			if(action != null && !action.equals("") && !action.equalsIgnoreCase("none") && drillDownObj.get("data") != null) {
				JSONObject data = (JSONObject) drillDownObj.get("data");
				switch (action.toString()) {
					case "showReport":
						if (data.get("type").equals(2l)) {
							resultJson.put("web_url", "dashboard/" + AccountUtil.getCurrentApp().getLinkName() + "/webView/card/module/" + data.get("reportId"));
						} else {
							resultJson.put("web_url", "dashboard/" + AccountUtil.getCurrentApp().getLinkName() + "/webView/card/telemetry/" + data.get("reportId"));
						}
						break;
					case "showListView":
						resultJson.put("viewName",data.get("view"));
						resultJson.put("moduleName",cardParams.get("moduleName"));
						break;
					case "showTrend":
						resultJson.put("web_url","dashboard/" + AccountUtil.getCurrentApp().getLinkName() + "/webView/card/trend/" + cardId);
					default:
						break;
				}
			}
		}
		return resultJson;
	}
}
