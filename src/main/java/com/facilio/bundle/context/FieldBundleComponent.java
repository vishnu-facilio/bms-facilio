package com.facilio.bundle.context;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bundle.interfaces.BundleComponentInterface;
import com.facilio.bundle.utils.BundleConstants;
import com.facilio.chain.FacilioContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class FieldBundleComponent implements BundleComponentInterface {

	@Override
	public JSONObject getFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
		FacilioField field = (FacilioField) context.get(BundleConstants.COMPONENT_OBJECT);
		
		JSONObject returnJson = FieldUtil.getAsJSON(field);
		
		return returnJson;
	}

	@Override
	public JSONArray getAllFormatedObject(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		JSONObject parentObject = (JSONObject)context.get(BundleConstants.PARENT_COMPONENT_OBJECT);
		String moduleName = (String) parentObject.get("name");
		
		List<FacilioField> fields = Constants.getModBean().getAllFields(moduleName);
		
		JSONArray returnList = new JSONArray();
		for(FacilioField field : fields) {
			context.put(BundleConstants.COMPONENT_OBJECT, field);
			context.put(BundleConstants.COMPONENT_ID, field.getFieldId());
			
			JSONObject formattedObject = getFormatedObject(context);
			
			returnList.add(formattedObject);
		}
		
		
		return returnList;
	}

	@Override
	public void install(FacilioContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
