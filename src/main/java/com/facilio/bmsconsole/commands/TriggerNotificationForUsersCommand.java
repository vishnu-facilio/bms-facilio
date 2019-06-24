package com.facilio.bmsconsole.commands;

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
import com.facilio.bmsconsole.util.NotificationConfigAPI;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class TriggerNotificationForUsersCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long configId = (Long)context.get(FacilioConstants.ContextNames.NOTIFICATION_JOB_ID);
		List<NotificationUserContext> usersToBeNotified = FieldUtil.getAsBeanListFromMapList(NotificationConfigAPI.getUsersConfigured(configId), NotificationUserContext.class);
        
        NotificationConfigContext notificationConfig = FieldUtil.getAsBeanFromMap(NotificationConfigAPI.getNotificationConfigDetails(configId), NotificationConfigContext.class);
        ActionContext action = ActionAPI.getAction(notificationConfig.getActionId());
        if(notificationConfig != null) {
        	long parentId = notificationConfig.getParentId();
        	String parentModuleName = notificationConfig.getConfigModuleName();
        	if(fetchRecord(parentId, parentModuleName, notificationConfig.getCriteria(), notificationConfig.getOrgId())) {
		        if(CollectionUtils.isNotEmpty(usersToBeNotified)) {
			        for(NotificationUserContext user : usersToBeNotified) {
			        	//set as current user to get user specific data
			        	User userObj = AccountUtil.getUserBean().getUser(user.getToUser().getOuid());
			        	Account currentAccount = new Account(AccountUtil.getOrgBean().getOrg(action.getOrgId()), userObj);
					    AccountUtil.setCurrentAccount(currentAccount);
			        	Map<String, Object> placeHolders = WorkflowRuleAPI.getOrgPlaceHolders();
			        	placeHolders.put("parentId",notificationConfig.getParentId());
			        	 placeHolders.put("parentModuleName",notificationConfig.getConfigModuleName());
			        	action.getTemplate().setFtl(true);
				        action.executeAction(placeHolders, null, null, null);
			        }
		        }
        	}
        }
        else {
        	throw new IllegalArgumentException("Invalid configuration");
        }
		return false;
	}

	private boolean fetchRecord(long id, String moduleName, Criteria criteria, long orgId)  throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		
		Class beanClassName = FacilioConstants.ContextNames.getClassFromModuleName(moduleName);
		if (beanClassName == null) {
			beanClassName = ModuleBaseWithCustomFields.class;
		}
		SelectRecordsBuilder<? extends ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
															.module(module)
															.beanClass(beanClassName)
															.select(fields)
															.andCondition(CriteriaAPI.getIdCondition(Long.valueOf(id), module))
															;
		if(criteria != null && criteria.getCriteriaId() > 0) {
			 criteria = CriteriaAPI.getCriteria(orgId,criteria.getCriteriaId());
			 builder.andCriteria(criteria);
		}
		List<? extends ModuleBaseWithCustomFields> records = builder.get();
		if(CollectionUtils.isNotEmpty(records)) {
			return true;
		}
		return false;
	}
}
