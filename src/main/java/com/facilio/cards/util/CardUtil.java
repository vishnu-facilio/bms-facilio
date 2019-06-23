package com.facilio.cards.util;

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
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.Set;

public class CardUtil {
	
	public static boolean isGetDataFromEnum(String key) {
		
		return CardType.CARD_TYPE_BY_NAME.keySet().contains(key);
	}
	
	public static boolean isExtraCard(String key) {
		
		if(key.equals("readingWithGraphCard") || key.contains("emrill") || key.equals("resourceAlarmBar")) {
			return true;
		}
		return false;
	}
	
	public static boolean isDynamicWorkflowGenCard(String key) {
		
		if(CardType.getCardType(key).isDynamicWfGeneratingCard()) {
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
				return UnitsUtil.getOrgDisplayUnit(AccountUtil.getCurrentOrg().getId(), metricId);
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
}
