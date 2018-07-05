package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class PerformAssetAction implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<WorkflowRuleContext> workflowRules = (List<WorkflowRuleContext>) context.get("workflowRules");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET);
		for(WorkflowRuleContext workflowRule :workflowRules) {
			
			if(!workflowRule.isActive()) {
				continue;
			}
			
			Criteria criteria = workflowRule.getCriteria();
			
			SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
					.moduleName(module.getName())
					.beanClass(AssetContext.class)
					.select(modBean.getAllFields(module.getName()))
					.table(module.getTableName())
					.andCriteria(criteria);
			
			String value  = null;
			if(criteria != null && criteria.getConditions() != null && !criteria.getConditions().isEmpty()) {
				 Condition condition = criteria.getConditions().get(1);
				 value = condition.getValue();
			}
			
			List<AssetContext> assets = selectBuilder.get();
			
			User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getId());
			List<String> assetNameList = new ArrayList<>();
			for(AssetContext asset :assets) {
				assetNameList.add(asset.getName());
				
			}
			if(!assetNameList.isEmpty()) {
				
				String assetNames = StringUtils.join(assetNameList,",");
				
				Map<String, Object> placeHolders = new HashMap<>();
				CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
				placeHolders.put("org.superAdmin.email", "krishnan.e@facilio.com");
				placeHolders.put("org.superAdmin.phone", superAdmin.getPhone());
				placeHolders.put("asset.names", assetNames);
				if(value != null) {
					placeHolders.put("expired.day", value);
				}
				
				for(ActionContext action :workflowRule.getActions()) {
					
					action.executeAction(placeHolders, context, workflowRule, null);
				}
				
			}
		}
		return false;
	}

}
