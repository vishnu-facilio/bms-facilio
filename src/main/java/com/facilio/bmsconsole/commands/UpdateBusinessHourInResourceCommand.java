package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.ResourceContext.ResourceType;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateBusinessHourInResourceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> props = new HashMap<String,Object>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE);
		long businesshoursId =(long) context.get(FacilioConstants.ContextNames.ID);
		long resourceid =(long) context.get(FacilioConstants.ContextNames.RESOURCE_ID);
		BusinessHoursContext businessHours = (BusinessHoursContext) context
				.get(FacilioConstants.ContextNames.BUSINESS_HOUR);
		ResourceContext resource = ResourceAPI.getResource(resourceid);
		if (resource.getResourceTypeEnum().equals(ResourceType.ASSET)) {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField field = fieldMap.get("operatingHour");
		List<Object> changeList = new ArrayList<Object>();
		long fieldid = field.getFieldId(); 
		Object oldValue = null;
		Object newValue = null;
		if(resourceid!=-1){
			if (businessHours == null && businesshoursId > 0) {
				List<BusinessHoursContext> businessHour = BusinessHoursAPI.getBusinessHours(Collections.singletonList(businesshoursId));
				newValue = businessHour.get(0);
			}
			else {
				newValue = businessHours;
			}
			JSONObject info = new JSONObject();
			JSONObject changeObj = new JSONObject();
			changeObj.put("field", field.getName());
			changeObj.put("displayName", field.getDisplayName());
			changeObj.put("oldValue", null);
			changeObj.put("newValue", BusinessHoursContext.getAsJsonObject((BusinessHoursContext)newValue));
			changeList.add(changeObj);
			info.put("changeSet", changeList);
			CommonCommandUtil.addActivityToContext(resourceid, -1, AssetActivityType.UPDATE, info, (FacilioContext) context);
			}
		}	
	    props.put("operatingHour",businesshoursId);
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName()).fields(fields)
				.andCustomWhere("ID = ?", resourceid);
        builder.update(props);
		
		return false;
	}
	

}
