package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class getLastBreakDownFromCurrentSourceCommand implements Command {
	@Override
	public boolean execute(Context context) throws Exception {
		AssetBDSourceDetailsContext assetBDSourceDetails = (AssetBDSourceDetailsContext) context.get(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS);
		Boolean assetBreakdownStatus = (Boolean) context.get(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS);
       if(assetBreakdownStatus){
   		Long assetBreakdownId = (Long) context.get(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID);
    	Long sourceId=assetBDSourceDetails.getSourceId();
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.ASSET_BD_SOURCE_DETAILS);
		List<FacilioField> fields = modBean.getAllFields(ContextNames.ASSET_BD_SOURCE_DETAILS);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
    	SelectRecordsBuilder<AssetBDSourceDetailsContext> selectBuilder = new SelectRecordsBuilder<AssetBDSourceDetailsContext>()
				.select(fields)
				.module(module)
				.beanClass(AssetBDSourceDetailsContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(assetBreakdownId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceId"), String.valueOf(sourceId), NumberOperators.EQUALS))
				.limit(1)
				;
    	List<AssetBDSourceDetailsContext> assetBDSourceDetailFromThisSource= selectBuilder.get();
		if(!assetBDSourceDetailFromThisSource.isEmpty()){
			boolean allCompleted=assetBDSourceDetailFromThisSource.stream().allMatch(bd->bd.getTotime()>0);
			if(!allCompleted){
				AssetBDSourceDetailsContext lastRecord=assetBDSourceDetailFromThisSource.stream().filter(bd->bd.getTotime()<0).findFirst().get();
				assetBDSourceDetails.setFromtime(lastRecord.getFromtime());
				context.put(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS, assetBDSourceDetails);
				context.put(FacilioConstants.ContextNames.LAST_ASSET_BD_SOURCE_DETAILS_ID,lastRecord.getId());
			}
		}
       }
		return false;
	}
}