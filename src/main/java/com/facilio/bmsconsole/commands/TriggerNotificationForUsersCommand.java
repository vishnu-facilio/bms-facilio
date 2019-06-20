package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.NotificationConfigContext;
import com.facilio.bmsconsole.context.NotificationUserContext;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.ParameterContext;

public class TriggerNotificationForUsersCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long configId = (Long)context.get(FacilioConstants.ContextNames.NOTIFICATION_JOB_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.NOTIFICATION_USER);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<NotificationUserContext> builder = new SelectRecordsBuilder<NotificationUserContext>()
				.module(module)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("CONFIGURATION_ID", "configurationId", String.valueOf(configId),NumberOperators.EQUALS));
				;
        List<NotificationUserContext> usersToBeNotified = builder.get();
        
        FacilioModule configModule = modBean.getModule(FacilioConstants.ContextNames.NOTIFICATION_CONFIG);
		List<FacilioField> configFields = modBean.getAllFields(configModule.getName());
		SelectRecordsBuilder<NotificationConfigContext> configBuilder = new SelectRecordsBuilder<NotificationConfigContext>()
				.module(configModule)
				.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(configModule.getName()))
				.select(configFields)
			    .andCondition(CriteriaAPI.getIdCondition(configId, configModule));
				;
        List<NotificationConfigContext> notificationConfig = configBuilder.get();
        ActionContext action = ActionAPI.getAction(notificationConfig.get(0).getActionId());
        if(CollectionUtils.isNotEmpty(notificationConfig)) {
	        if(CollectionUtils.isNotEmpty(usersToBeNotified)) {
		        for(NotificationUserContext user : usersToBeNotified) {
		        	//set as current user to get user specific data
		        	User userObj = AccountUtil.getUserBean().getUser(user.getToUser().getOuid());
		        	Account currentAccount = new Account(AccountUtil.getOrgBean().getOrg(action.getOrgId()), userObj);
				    AccountUtil.setCurrentAccount(currentAccount);
		        	Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
		        	placeHolders.put("parentId",notificationConfig.get(0).getParentId());
		        	placeHolders.put("parentModuleName",notificationConfig.get(0).getConfigModuleName());
		        	action.getTemplate().setFtl(true);
			        action.executeAction(placeHolders, null, null, null);
		        }
	        }
        }
        else {
        	throw new IllegalArgumentException("Invalid configuration");
        }
		return false;
	}

}
