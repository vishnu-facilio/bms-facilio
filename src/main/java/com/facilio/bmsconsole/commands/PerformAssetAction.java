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
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
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
					.table(module.getTableName());
			
			String expiryDay  = null;
			Long siteId = null;
			int operatorId = -1;
			if(criteria != null && criteria.getConditions() != null && !criteria.getConditions().isEmpty()) {
				 Condition condition = criteria.getConditions().get(1);
				 expiryDay = condition.getValue();
				 operatorId = condition.getOperatorId();
				 if(criteria.getConditions().get(2) != null) {
					 condition = criteria.getConditions().get(2);
					 if(condition.getValue() != null) {
						 siteId = Long.parseLong(condition.getValue());
						 List<BaseSpaceContext> baseSpaces = SpaceAPI.getBaseSpaceWithChildren(siteId);
						 List<Long> baseSpaceids = new ArrayList<>();
						 baseSpaceids.add(siteId);
						 for(BaseSpaceContext baseSpace :baseSpaces) {
							 baseSpaceids.add(baseSpace.getId());
						 }
						 condition.setValue(StringUtils.join(baseSpaceids,","));
					 }
				 }
			}
			selectBuilder.andCriteria(criteria);
			
			List<AssetContext> assets = selectBuilder.get();
			
			User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getId());
			List<String> assetNameList = new ArrayList<>();
			List<String> assetUrlList = new ArrayList<>();
			String domain = AwsUtil.getConfig("clientapp.url");
			for(AssetContext asset :assets) {
				assetNameList.add(asset.getName()+"(#"+asset.getId()+")");
				assetUrlList.add(domain+"/app/at/asset/"+asset.getId()+"/overview");
			}
			if(!assetNameList.isEmpty()) {
				
				String assetNames = StringUtils.join(assetNameList,",");
				
				Map<String, Object> placeHolders = new HashMap<>();
				CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
				placeHolders.put("org.superAdmin.email", "krishnan.e@facilio.com");
				placeHolders.put("org.superAdmin.phone", superAdmin.getPhone());
				placeHolders.put("asset.names", assetNames);
				placeHolders.put("asset.url", "\n"+StringUtils.join(assetUrlList,"\n"));
				if(expiryDay != null) {
					Long millis = null;
					if(operatorId == DateOperators.PAST_N_DAY.getOperatorId()) {
						millis = DateTimeUtil.getDayStartTime(-Integer.parseInt(expiryDay));
					}
					else if(operatorId == DateOperators.IN_N_DAY.getOperatorId()) {
						millis = DateTimeUtil.getDayStartTime(Integer.parseInt(expiryDay));
					}
					String formateDate = DateTimeUtil.getFormattedTime(millis, "dd-MM-yyyy");
					placeHolders.put("expired.date", formateDate);
				}
				if(siteId != null) {
					SiteContext site = SpaceAPI.getSiteSpace(siteId);
					placeHolders.put("site.name", site.getName());
				}
				
				for(ActionContext action :workflowRule.getActions()) {
					
					action.executeAction(placeHolders, context, workflowRule, null);
				}
				
			}
		}
		return false;
	}

}
