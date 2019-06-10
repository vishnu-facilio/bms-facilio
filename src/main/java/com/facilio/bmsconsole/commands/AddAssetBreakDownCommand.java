package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
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
		AssetBreakdownContext assetBreakdown;
		Boolean assetBreakdownStatus = (Boolean) context.get(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS);
		AssetBDSourceDetailsContext assetBDSourceDetails = (AssetBDSourceDetailsContext) context.get(FacilioConstants.ContextNames.ASSET_BD_SOURCE_DETAILS);
		Long lastAssetBdSourceId = (Long) context.get(FacilioConstants.ContextNames.LAST_ASSET_BD_SOURCE_DETAILS_ID);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.ASSET_BD_SOURCE_DETAILS);
		List<FacilioField> fields = modBean.getAllFields(ContextNames.ASSET_BD_SOURCE_DETAILS);
		fields.add(FieldFactory.getModuleIdField(module));
		FacilioModule assetBreakdownModule = modBean.getModule(ContextNames.ASSET_BREAKDOWN);
		List<FacilioField> assetBreakdownFields = modBean.getAllFields(ContextNames.ASSET_BREAKDOWN);
		assetBreakdownFields.add(FieldFactory.getModuleIdField(assetBreakdownModule));
		if (assetBreakdownStatus) {
			if (lastAssetBdSourceId != null && lastAssetBdSourceId != -1) {
				if (assetBDSourceDetails.getTotime() > 0) {
					Map<String, Object> props = FieldUtil.getAsProperties(assetBDSourceDetails);
					List<FacilioField> fieldsToUpdate = new ArrayList<>();
					fieldsToUpdate.add(modBean.getField("totime", ContextNames.ASSET_BD_SOURCE_DETAILS));

					GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
							.table(module.getTableName()).fields(fieldsToUpdate)
							.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
							.andCondition(CriteriaAPI.getIdCondition(lastAssetBdSourceId, module));
					updateBuilder.update(props);
				}
			} else {
					Map<String, Object> props = FieldUtil.getAsProperties(assetBDSourceDetails);
					props.put("moduleId", module.getModuleId());
					GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
							.table(module.getTableName())
							.fields(fields)
							.addRecord(props);
					insertBuilder.save();
			}
			List<AssetBDSourceDetailsContext> AssetBDdetailsList = AssetBreakdownAPI.getAssetBDdetails(module,fields, assetBDSourceDetails.getParentId());
			boolean allBreakDownCompleted = AssetBDdetailsList.stream().allMatch(ast -> ast.getTotime() != -1);
			if (allBreakDownCompleted) {
				assetBreakdown = AssetBreakdownAPI.getAssetBreakdown(assetBreakdownModule, assetBreakdownFields,assetBDSourceDetails.getParentId());
				assetBreakdown.setTotime(AssetBDdetailsList.get(0).getTotime());
				assetBreakdown.setDuration(AssetBreakdownAPI.calculateDurationInSeconds(assetBreakdown.getFromtime(), assetBreakdown.getTotime()));

				Map<String, Object> props = FieldUtil.getAsProperties(assetBreakdown);
				List<FacilioField> fieldsToUpdate = new ArrayList<>();
				fieldsToUpdate.add(modBean.getField("totime", ContextNames.ASSET_BREAKDOWN));
				fieldsToUpdate.add(modBean.getField("duration", ContextNames.ASSET_BREAKDOWN));

				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(assetBreakdownModule.getTableName())
						.fields(fieldsToUpdate)
						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(assetBreakdownModule))
						.andCondition(CriteriaAPI.getIdCondition(assetBDSourceDetails.getParentId(),
								assetBreakdownModule));
				updateBuilder.update(props);
				context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, false);
			}
		} else {
			assetBreakdown=new AssetBreakdownContext();
			assetBreakdown.setParentId(assetBDSourceDetails.getAssetid());
            assetBreakdown.setFromtime(assetBDSourceDetails.getFromtime());
			if (assetBDSourceDetails.getTotime() > 0) {
				assetBreakdown.setTotime(assetBDSourceDetails.getTotime());
				assetBreakdown.setDuration(AssetBreakdownAPI.calculateDurationInSeconds(assetBDSourceDetails.getFromtime(), assetBDSourceDetails.getTotime()));
			}
			updateAssetBetweenFailureTime(assetBreakdownModule, assetBreakdownFields, assetBreakdown);
			Map<String, Object> props = FieldUtil.getAsProperties(assetBreakdown);
			props.put("moduleId", assetBreakdownModule.getModuleId());
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(assetBreakdownModule.getTableName())
					.fields(assetBreakdownFields).addRecord(props);
			insertBuilder.save();
			long assetBreakdownid = (long) props.get("id");
			assetBDSourceDetails.setParentId(assetBreakdownid);
			props = FieldUtil.getAsProperties(assetBDSourceDetails);
			props.put("moduleId", module.getModuleId());
			insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
					.fields(fields).addRecord(props);
			insertBuilder.save();
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