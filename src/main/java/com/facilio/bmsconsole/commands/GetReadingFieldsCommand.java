package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetReadingFieldsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioModule> readings = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if(readings != null && !readings.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for(FacilioModule reading : readings) {
				List<FacilioField> dataPoints = ReadingsAPI.excludeDefaultReadingFields(modBean.getAllFields(reading.getName()));
				reading.setFields(dataPoints);
			}
		}
		return false;
	}
	
	

}
