package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;

public class LoadAssetFields extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(LoadAssetFields.class.getName());
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		modBean.getModule(moduleName);
		List<FacilioField> fields = new ArrayList(modBean.getAllFields(moduleName));
//		if (!moduleName.equals(FacilioConstants.ContextNames.ASSET)) {
//			List<FacilioField> customFields = modBean.getAllCustomFields("asset");
//			if (customFields != null) {
//				fields.addAll(modBean.getAllCustomFields("asset"));
//			}
//		}
		context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, fields);
		long timeTaken = System.currentTimeMillis() - startTime;
		LOGGER.debug("Time taken to execute LoadAssetFields : "+timeTaken);
		
//		fields.addAll(modBean.getAllCustomFields("asset"));
		
		return false;
	}

}
