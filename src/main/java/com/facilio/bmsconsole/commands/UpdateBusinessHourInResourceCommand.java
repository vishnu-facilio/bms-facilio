package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

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
		if(resourceid!=-1){ 
	    props.put("operatingHour",businesshoursId);
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(module.getTableName()).fields(fields)
				.andCustomWhere("ID = ?", resourceid);
        builder.update(props);
		}
		return false;
	}
	

}
