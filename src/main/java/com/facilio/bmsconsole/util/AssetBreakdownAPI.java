package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.AssetBDSourceDetailsContext;
import com.facilio.bmsconsole.context.AssetBreakdownContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
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
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(breakDownId), NumberOperators.EQUALS))
				.orderBy(fieldMap.get("totime").getColumnName() + " desc");
		return selectBuilder.get();
	}
	public static AssetBreakdownContext getAssetBreakdown(FacilioModule module, List<FacilioField> fields,long id) throws Exception {
		SelectRecordsBuilder<AssetBreakdownContext> selectBuilder = new SelectRecordsBuilder<AssetBreakdownContext>()
				.select(fields)
				.module(module)
				.beanClass(AssetBreakdownContext.class)
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
		return selectBuilder.fetchFirst();
	}
	public static long getAssetLastBreakdownIdByTime(FacilioModule module, List<FacilioField> fields,long parentId)throws Exception{
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		SelectRecordsBuilder<AssetBreakdownContext> selectBuilder = new SelectRecordsBuilder<AssetBreakdownContext>()
				.select(fields)
				.module(module)
				.beanClass(AssetBreakdownContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), String.valueOf(parentId), NumberOperators.EQUALS))
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
}
