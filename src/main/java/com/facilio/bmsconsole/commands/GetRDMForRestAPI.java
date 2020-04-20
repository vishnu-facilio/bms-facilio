package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;

public class GetRDMForRestAPI extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long resourceId = (long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		
		long fieldId = (long) context.get(FacilioConstants.ContextNames.FIELD_ID);
		
		List<ReadingDataMeta> rdmList = new ArrayList<ReadingDataMeta>();
		if(fieldId > 0) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			FacilioField field = modBean.getField(fieldId);
			
			ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(resourceId, field);
			
			rdmList.add(rdm);
		}
		else {
			
			rdmList = ReadingsAPI.getReadingDataMetaList(resourceId, null);
		}
		
		JSONArray resJsonArray = new JSONArray();
		
		for(ReadingDataMeta rdm :rdmList) {
			
			ReadingsAPI.convertUnitForRdmData(rdm);
			
			JSONObject resJSON = new JSONObject();
			
			JSONObject fieldJson = new JSONObject();
			
			fieldJson.put("fieldId", rdm.getField().getFieldId());
			fieldJson.put("name", rdm.getField().getName());
			fieldJson.put("displayName", rdm.getField().getDisplayName());
			
			if(rdm.getField() instanceof NumberField) {
				NumberField numberfield = (NumberField) rdm.getField();
				if(numberfield.getUnitEnum() != null) {
					fieldJson.put("unit", numberfield.getUnitEnum().getSymbol());
				}
			}
			
			resJSON.put("value", rdm.getValue());
			resJSON.put("time", rdm.getTtime());
			resJSON.put("field", fieldJson);
			
			resJsonArray.add(resJSON);
		}
		
		
		if(fieldId > 0) {
			
			context.put(FacilioConstants.ContextNames.READING_DATA_META, resJsonArray.get(0));
		}
		else {
			
			context.put(FacilioConstants.ContextNames.READING_DATA_META_LIST, resJsonArray);
		}
		
		return false;
	}

}
