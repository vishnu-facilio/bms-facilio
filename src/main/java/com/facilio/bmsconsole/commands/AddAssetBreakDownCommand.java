package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.sound.midi.Soundbank;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext.SourceType;
import com.facilio.bmsconsole.util.AssetBreakdownAPI;
import com.facilio.chain.FacilioContext;
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
		if (SourceType.ASSET.equals(SourceType.valueOf((int) assetBDSourceDetails.getSourceType()))) {
			JSONObject info = new JSONObject();
			List<Object> changeList = new ArrayList<Object>();
			JSONObject changeObj = new JSONObject();
			changeObj.put("field", "downTime");
			changeObj.put("displayName", "Downtime");
			changeObj.put("from", assetBDSourceDetails.getFromtime());
			changeObj.put("to", assetBDSourceDetails.getTotime());
			changeList.add(changeObj);
			info.put("changeSet", changeList);
			CommonCommandUtil.addActivityToContext(assetBDSourceDetails.getAssetid(), -1, AssetActivityType.ASSET_DOWNTIME, info, (FacilioContext) context);
		}
		if (assetBreakdownStatus) {
			if (lastAssetBdSourceId != null && lastAssetBdSourceId != -1) {
				if (assetBDSourceDetails.getTotime() > 0) {
					Map<String, Object> props = FieldUtil.getAsProperties(assetBDSourceDetails);
					List<FacilioField> fieldsToUpdate = new ArrayList<>();
					fieldsToUpdate.add(modBean.getField("totime", ContextNames.ASSET_BD_SOURCE_DETAILS));

					GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
							.table(module.getTableName()).fields(fieldsToUpdate)
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
			assetBreakdown = AssetBreakdownAPI.getAssetBreakdown(assetBreakdownModule, assetBreakdownFields,assetBDSourceDetails.getParentId());
			if(assetBDSourceDetails.getCondition()!=null&&!assetBreakdown.getCondition().contains(assetBDSourceDetails.getCondition())){
				StringJoiner joiner = new StringJoiner(",");
				joiner.add(assetBreakdown.getCondition()).add(assetBDSourceDetails.getCondition());
				assetBreakdown.setCondition(joiner.toString());
				Map<String, Object> props = FieldUtil.getAsProperties(assetBreakdown);
				List<FacilioField> fieldsToUpdate = new ArrayList<>();
				fieldsToUpdate.add(modBean.getField("condition", ContextNames.ASSET_BREAKDOWN));

				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(assetBreakdownModule.getTableName())
						.fields(fieldsToUpdate)
						.andCondition(CriteriaAPI.getIdCondition(assetBDSourceDetails.getParentId(),assetBreakdownModule));
				updateBuilder.update(props);
			}
			
			List<AssetBDSourceDetailsContext> AssetBDdetailsList = AssetBreakdownAPI.getAssetBDdetails(module,fields, assetBDSourceDetails.getParentId());
			boolean allBreakDownCompleted = AssetBDdetailsList.stream().allMatch(ast -> ast.getTotime()>0);
			if (allBreakDownCompleted) {
				assetBreakdown.setId(assetBDSourceDetails.getParentId());
				AssetBreakdownAPI.updateTotimeAndDuration(assetBreakdown,AssetBDdetailsList);
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
			assetBreakdown.setCondition(assetBDSourceDetails.getCondition());
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
			context.put(FacilioConstants.ContextNames.ASSET_DOWNTIME_STATUS, assetBreakdown.getTotime() <0);
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