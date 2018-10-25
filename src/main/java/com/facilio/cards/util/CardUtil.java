package com.facilio.cards.util;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.fw.BeanFactory;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class CardUtil {

	
	public static boolean isGetDataFromEnum(String key) {
		
		return CardType.CARD_TYPE_BY_NAME.keySet().contains(key);
	}
	
	public static boolean isExtraCard(String key) {
		
		if(key.equals("readingWithGraphCard")) {
			return true;
		}
		return false;
	}
	
	public static Unit getUnit(JSONObject params) throws Exception {
		
		if(params == null) {
			return null;
		}
		int metricId = getMetic((String) params.get("moduleName"), (String) params.get("fieldName"));
		
		if(metricId <= 0 ) {
			return null;
		}
		Unit unit = UnitsUtil.getOrgDisplayUnit(AccountUtil.getCurrentOrg().getId(), metricId);
		
		return unit;
	}
	
	public static int getMetic(String moduleName,String fieldName) throws Exception {
		
		if(moduleName != null && fieldName != null) {
			
			FacilioField field = getField(moduleName, fieldName);
			
			if(field instanceof NumberField) {
				NumberField numberField = (NumberField) field;
				return numberField.getMetric();
			}
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
}
