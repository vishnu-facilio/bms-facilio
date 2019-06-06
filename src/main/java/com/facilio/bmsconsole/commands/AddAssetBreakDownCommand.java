package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.bmsconsole.util.AssetBreakdownAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

public class AddAssetBreakDownCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		AssetBreakdownContext assetBreakdown = (AssetBreakdownContext) context.get(FacilioConstants.ContextNames.ASSET_BREAKDOWN);
		Boolean assetBreakdownStatus = (Boolean) context.get(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS);
		Long assetBreakdownId = (Long) context.get(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.ASSET_BREAKDOWN);
		if (assetBreakdownStatus) {
			if (assetBreakdown.getTotime() != -1) {
				assetBreakdown.setDuration(AssetBreakdownAPI.calculateDurationInSeconds(assetBreakdown.getFromtime(),
						assetBreakdown.getTotime()));
				Map<String, Object> props = FieldUtil.getAsProperties(assetBreakdown);
				List<FacilioField> fields = new ArrayList<>();
				fields.add(modBean.getField("totime", ContextNames.ASSET_BREAKDOWN));
				fields.add(modBean.getField("duration", ContextNames.ASSET_BREAKDOWN));
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName())
						.fields(fields).andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
						.andCondition(CriteriaAPI.getIdCondition(assetBreakdownId, module));
				updateBuilder.update(props);
				context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, false);
			}
		} else {
			List<FacilioField> fields = modBean.getAllFields(ContextNames.ASSET_BREAKDOWN);
			fields.add(FieldFactory.getModuleIdField(module));
			
			updateAssetBetweenFailureTime(module, fields, assetBreakdown);
			
			Map<String, Object> props = FieldUtil.getAsProperties(assetBreakdown);
			props.put("moduleId", module.getModuleId());
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
					.fields(fields).addRecord(props);
			insertBuilder.save();
			long assetBreakdownid = (long) props.get("id");
			context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_ID, assetBreakdownid);
			context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, assetBreakdown.getTotime() == -1);
		}
		return false;
	}

	private void updateAssetBetweenFailureTime (FacilioModule module, List<FacilioField> fields, AssetBreakdownContext newAssetBreakdown) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		
		AssetBreakdownContext prevBreakdown = getAssetBreakdown(module, fields, fieldMap, newAssetBreakdown.getParentId(), CriteriaAPI.getCondition(fieldMap.get("totime"), String.valueOf(newAssetBreakdown.getFromtime()), DateOperators.IS_BEFORE));
		if (prevBreakdown != null) {
			long failureTime = AssetBreakdownAPI.calculateDurationInSeconds(prevBreakdown.getTotime(), newAssetBreakdown.getFromtime());
			prevBreakdown.setTimeBetweenFailure(failureTime);
			UpdateRecordBuilder<AssetBreakdownContext> updateBuilder = new UpdateRecordBuilder<AssetBreakdownContext>()
					.module(module)
					.fields(fields)
					.andCondition(CriteriaAPI.getIdCondition(prevBreakdown.getId(), module));
			updateBuilder.update(prevBreakdown);
		}
		if (newAssetBreakdown.getTotime() > 0) {
			newAssetBreakdown.setDuration(AssetBreakdownAPI.calculateDurationInSeconds(newAssetBreakdown.getFromtime(), newAssetBreakdown.getTotime()));
			
			AssetBreakdownContext nextBreakdown = getAssetBreakdown(module, fields, fieldMap, newAssetBreakdown.getParentId(), CriteriaAPI.getCondition(fieldMap.get("fromtime"), String.valueOf(newAssetBreakdown.getTotime()), DateOperators.IS_AFTER));
			if (nextBreakdown != null) {
				long failureTime = AssetBreakdownAPI.calculateDurationInSeconds(newAssetBreakdown.getTotime(), nextBreakdown.getFromtime());
				newAssetBreakdown.setTimeBetweenFailure(failureTime);
			}
		}
		
	}
	
	private AssetBreakdownContext getAssetBreakdown(FacilioModule module, List<FacilioField> fields, Map<String, FacilioField> fieldMap, long parentId, Condition condition) throws Exception {
		SelectRecordsBuilder<AssetBreakdownContext> selectBuilder = new SelectRecordsBuilder<AssetBreakdownContext>()
				.select(fields)
				.module(module)
				.beanClass(AssetBreakdownContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS))
				.orderBy(fieldMap.get("fromtime").getColumnName() + " desc")
				.limit(1)
				;
		
		selectBuilder.andCondition(condition);
		
		return selectBuilder.fetchFirst();
	}
	
}