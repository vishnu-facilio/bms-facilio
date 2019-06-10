package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext.SourceType;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class ValidateAssetBreakdownCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AssetBDSourceDetailsContext assetBreakdown = (AssetBDSourceDetailsContext) context.get(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS);
		if (assetBreakdown.getFromtime() == -1) {
			return false;
		}
		
		if(assetBreakdown.getTotime()>0&&assetBreakdown.getFromtime()>assetBreakdown.getTotime()){
          throw new IllegalArgumentException("To time is Greater than from time - " + assetBreakdown.getCondition());
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.ASSET_BREAKDOWN);
		
		List<FacilioField> fields = modBean.getAllFields(ContextNames.ASSET_BREAKDOWN);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<AssetBreakdownContext> selectBuilder = new SelectRecordsBuilder<AssetBreakdownContext>()
				.select(fields)
				.module(module)
				.beanClass(AssetBreakdownContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(assetBreakdown.getAssetid()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fromtime"), String.valueOf(assetBreakdown.getTotime()), NumberOperators.LESS_THAN_EQUAL))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("totime"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("totime"), String.valueOf(assetBreakdown.getFromtime()), NumberOperators.GREATER_THAN_EQUAL))
				;
		
		List<AssetBreakdownContext> breakdowns = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(breakdowns)&&assetBreakdown.getSourceTypeEnum()!=SourceType.ALARM) { // TODO Dont throw if added from alarm..handle once sourcetype is added 
			throw new IllegalArgumentException("Breakdown already noted for the time period - " + breakdowns.get(0).getId());
		}
		
		return false;
	}

}
