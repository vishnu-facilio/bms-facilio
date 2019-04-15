package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FacilioModule.ModuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class ConstructCriteriaAndSetModuleNameForActivity implements Command {

	private static final Logger LOGGER = LogManager.getLogger(ConstructCriteriaAndSetModuleNameForActivity.class.getName());
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
		if (parentId != null && parentId != -1) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			FacilioModule activityModule = null;
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			if (StringUtils.isEmpty(moduleName)) {
				String parentModuleName = (String) context.get(FacilioConstants.ContextNames.PARENT_MODULE);
				List<FacilioModule> subModules = modBean.getSubModules(parentModuleName, ModuleType.ACTIVITY);
				
				if (CollectionUtils.isEmpty(subModules)) {
					LOGGER.info("No Activity Module for module : "+parentModuleName+" and so not adding activities for that");
					return true;
				}
				
				activityModule = subModules.get(0);
				context.put(FacilioConstants.ContextNames.MODULE_NAME, activityModule.getName());
			}
			else {
				activityModule = modBean.getModule(moduleName);
			}
			
			Criteria filterCriteria = new Criteria();
			filterCriteria.addAndCondition(CriteriaAPI.getCondition(modBean.getField("parentId", activityModule.getName()), String.valueOf(parentId), PickListOperators.IS));
			
			context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, filterCriteria);
			context.put(FacilioConstants.ContextNames.SORTING_QUERY, modBean.getField("ttime", activityModule.getName()).getCompleteColumnName()+" DESC");
		}
		return false;
	}

}
