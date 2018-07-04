package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.criteria.Criteria;
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
			
			Criteria criteria = workflowRule.getCriteria();
			
			SelectRecordsBuilder<AssetContext> selectBuilder = new SelectRecordsBuilder<AssetContext>()
					.moduleName(module.getName())
					.beanClass(AssetContext.class)
					.select(modBean.getAllFields(module.getName()))
					.table(module.getTableName())
					.andCriteria(criteria);
			
			List<AssetContext> assets = selectBuilder.get();
			
			User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getId());
			for(AssetContext asset :assets) {
				Map<String, Object> placeHolders = new HashMap<>();
				CommonCommandUtil.appendModuleNameInKey(FacilioConstants.ContextNames.ASSET, FacilioConstants.ContextNames.ASSET, FieldUtil.getAsProperties(asset), placeHolders);
				CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
				placeHolders.put("org.superAdmin.email", superAdmin.getEmail());
				placeHolders.put("org.superAdmin.phone", superAdmin.getPhone());
				for(ActionContext action :workflowRule.getActions()) {
					
					action.executeAction(placeHolders, context, workflowRule, asset);
				}
				
			}
		}
		return false;
	}

}
