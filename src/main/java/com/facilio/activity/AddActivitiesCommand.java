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
	
	private String activityModule;
	
	public AddActivitiesCommand() {
		// TODO Auto-generated constructor stub
	}
	
	public AddActivitiesCommand(String activityModule) {
		// TODO Auto-generated constructor stub
		this.activityModule = activityModule;
	}
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ActivityContext> activities = (List<ActivityContext>) context.get(FacilioConstants.ContextNames.ACTIVITY_LIST);
		if (CollectionUtils.isNotEmpty(activities)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule activityModule = getActivityModule(modBean, context);
			if (activityModule != null) {
				InsertRecordBuilder<ActivityContext> insertBuilder = new InsertRecordBuilder<ActivityContext>()
																			.fields(modBean.getAllFields(activityModule.getName()))
																			.module(activityModule)
																			.addRecords(activities)
																			;
				insertBuilder.save();
			}
		}
		
		return false;
	}
	
	private FacilioModule getActivityModule (ModuleBean modBean, Context context) throws Exception {
		if (StringUtils.isNotEmpty(activityModule)) {
			FacilioModule module = modBean.getModule(activityModule);
			
			if (module == null) {
				LOGGER.info("No valid Activity Module with name "+activityModule+" and so not adding activities for that");
			}
			
			return module;
		}
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if (StringUtils.isEmpty(moduleName)) {
			LOGGER.info("No valid Activity/ Module name specified and so not adding activities for that");
			return null;
		}
		
		List<FacilioModule> subModules = modBean.getSubModules(moduleName, ModuleType.ACTIVITY);
		if (CollectionUtils.isEmpty(subModules)) {
			LOGGER.info("No Activity Module was found for module "+moduleName+" and so not adding activities for that");
			return null;
		}
		return subModules.get(0);
	}

}
