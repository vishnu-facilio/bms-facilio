package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext.SourceType;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.bmsconsole.util.AssetBreakdownAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class RemoveAssetBreakdownCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			for(long id:recordIds){
				List<AssetBDSourceDetailsContext> assetBDlist=AssetBreakdownAPI.getAssetBDSourceDetailsBySourceidAndType(id, SourceType.ALARM);
			    if(assetBDlist!=null&&!assetBDlist.isEmpty()){
			    	AssetBDSourceDetailsContext assetBDSourceDetail=assetBDlist.get(0);
			    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(ContextNames.ASSET_BD_SOURCE_DETAILS);
					List<FacilioField> fields = modBean.getAllFields(ContextNames.ASSET_BD_SOURCE_DETAILS);
					fields.add(FieldFactory.getModuleIdField(module));
					
			    	GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
							.table(module.getTableName())
							.andCondition(CriteriaAPI.getIdCondition(assetBDSourceDetail.getId(), module)).andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), module));
					builder.delete();
					
					FacilioModule assetBreakdownModule = modBean.getModule(ContextNames.ASSET_BREAKDOWN);
					List<FacilioField> assetBreakdownFields = modBean.getAllFields(ContextNames.ASSET_BREAKDOWN);
					assetBreakdownFields.add(FieldFactory.getModuleIdField(assetBreakdownModule));
					
					AssetBreakdownContext assetBreakdown=AssetBreakdownAPI.getAssetBreakdown(assetBreakdownModule, assetBreakdownFields, assetBDSourceDetail.getParentId());
					List<AssetBDSourceDetailsContext> AssetBDdetailsList = AssetBreakdownAPI.getAssetBDdetails(module,fields, assetBDSourceDetail.getParentId());
			    	assetBDSourceDetail.setAssetid(assetBreakdown.getParentId());
			    	context.put(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS,assetBDSourceDetail);
					if (AssetBDdetailsList != null && !AssetBDdetailsList.isEmpty()) {
						boolean allBreakDownCompleted = AssetBDdetailsList.stream().allMatch(ast -> ast.getTotime() > 0);
						if (allBreakDownCompleted) {
							AssetBreakdownAPI.updateTotimeAndDuration(assetBreakdown, AssetBDdetailsList);
							context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, false);
							context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID, assetBreakdown.getId());
						}else{
							context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, true);
							context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID, assetBreakdown.getId());
						}
					} else {
						builder = new GenericDeleteRecordBuilder().table(assetBreakdownModule.getTableName())
								.andCondition(CriteriaAPI.getIdCondition(assetBreakdown.getId(), assetBreakdownModule))
								.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(),assetBreakdownModule));
						builder.delete();
						long lastDowntimeId=AssetBreakdownAPI.getAssetLastBreakdownIdByTime(assetBreakdownModule, assetBreakdownFields, assetBreakdown.getParentId());
						if(lastDowntimeId>0){
							context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, false);
							context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID, lastDowntimeId);
						}
					}
			    }
			}
		}
		return false;
	}

}
