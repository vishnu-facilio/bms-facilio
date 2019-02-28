package com.facilio.activity;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class AddActivitiesCommand implements Command {

	private static final Logger LOGGER = LogManager.getLogger(AddActivitiesCommand.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<ActivityContext> activities = (List<ActivityContext>) context.get(FacilioConstants.ContextNames.ACTIVITY_LIST);
		
		if (StringUtils.isNotEmpty(moduleName) && CollectionUtils.isNotEmpty(activities)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioModule> subModules = modBean.getSubModules(moduleName, ModuleType.ACTIVITY);
			
			if (CollectionUtils.isEmpty(subModules)) {
				LOGGER.info("No Activity Module for module : "+moduleName+" and so not adding activities for that");
			}
			
			FacilioModule activityModule = subModules.get(0);
			InsertRecordBuilder<ActivityContext> insertBuilder = new InsertRecordBuilder<ActivityContext>()
																		.fields(modBean.getAllFields(activityModule.getName()))
																		.module(activityModule)
																		.addRecords(activities)
																		;
			insertBuilder.save();
		}
		
		return false;
	}

}
