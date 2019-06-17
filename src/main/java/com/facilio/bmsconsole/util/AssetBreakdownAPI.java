package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext.SourceType;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class AssetBreakdownAPI {
       
       public static long calculateDurationInSeconds(Long fromTime,Long toTime){
               return (toTime-fromTime) / 1000;
       }
       
	public static List<AssetBDSourceDetailsContext> getAssetBDdetails(FacilioModule module, List<FacilioField> fields,
			long breakDownId) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<AssetBDSourceDetailsContext> selectBuilder = new SelectRecordsBuilder<AssetBDSourceDetailsContext>()
				.select(fields)
				.module(module)
				.beanClass(AssetBDSourceDetailsContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(breakDownId), NumberOperators.EQUALS))
				.orderBy(fieldMap.get("totime").getColumnName() + " desc");
		return selectBuilder.get();
	}
	public static void updateTotimeAndDuration(AssetBreakdownContext assetBreakdown,List<AssetBDSourceDetailsContext> AssetBDdetailsList)throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule assetBreakdownModule = modBean.getModule(ContextNames.ASSET_BREAKDOWN);
		assetBreakdown.setTotime(AssetBDdetailsList.get(0).getTotime());
		assetBreakdown.setDuration(AssetBreakdownAPI.calculateDurationInSeconds(assetBreakdown.getFromtime(), assetBreakdown.getTotime()));

		Map<String, Object> props = FieldUtil.getAsProperties(assetBreakdown);
		List<FacilioField> fieldsToUpdate = new ArrayList<>();
		fieldsToUpdate.add(modBean.getField("totime", ContextNames.ASSET_BREAKDOWN));
		fieldsToUpdate.add(modBean.getField("duration", ContextNames.ASSET_BREAKDOWN));

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(assetBreakdownModule.getTableName())
				.fields(fieldsToUpdate)
				.andCondition(CriteriaAPI.getIdCondition(assetBreakdown.getId(),assetBreakdownModule));
		updateBuilder.update(props);
	}
	public static AssetBreakdownContext getAssetBreakdown(FacilioModule module, List<FacilioField> fields,long id) throws Exception {
		SelectRecordsBuilder<AssetBreakdownContext> selectBuilder = new SelectRecordsBuilder<AssetBreakdownContext>()
				.select(fields)
				.module(module)
				.beanClass(AssetBreakdownContext.class)
                .andCondition(CriteriaAPI.getIdCondition(id, module));
		return selectBuilder.fetchFirst();
	}
	public static long getAssetLastBreakdownIdByTime(FacilioModule module, List<FacilioField> fields,long assetId)throws Exception{
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<AssetBreakdownContext> selectBuilder = new SelectRecordsBuilder<AssetBreakdownContext>()
				.select(fields)
				.module(module)
				.beanClass(AssetBreakdownContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(assetId), NumberOperators.EQUALS))
				.orderBy(fieldMap.get("fromtime").getColumnName() + " desc")
				.limit(1)
				;
		AssetBreakdownContext assetBd=selectBuilder.fetchFirst();
		if(assetBd!=null){
			return assetBd.getId();
		}else{
			return -1;
		}
		
	}
	public static List<AssetBDSourceDetailsContext> getAssetBDSourceDetailsBySourceidAndType(long sourceid,SourceType sourceType)throws Exception{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.ASSET_BD_SOURCE_DETAILS);
		List<FacilioField> fields = modBean.getAllFields(ContextNames.ASSET_BD_SOURCE_DETAILS);
		fields.add(FieldFactory.getModuleIdField(module));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<AssetBDSourceDetailsContext> selectBuilder = new SelectRecordsBuilder<AssetBDSourceDetailsContext>()
				.select(fields)
				.module(module)
				.beanClass(AssetBDSourceDetailsContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceId"), String.valueOf(sourceid), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), String.valueOf(sourceType.getValue()), NumberOperators.EQUALS));
		return selectBuilder.get();
	}
}
