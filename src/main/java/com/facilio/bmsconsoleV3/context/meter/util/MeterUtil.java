package com.facilio.bmsconsoleV3.context.meter.util;

import java.util.*;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class MeterUtil {
	
	public static V3MeterContext getMeter(Long id, boolean fetchDeleted) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.Meter.METER);
		
		SelectRecordsBuilder<V3MeterContext> resourceBuilder = new SelectRecordsBuilder<V3MeterContext>()
				.select(modBean.getAllFields(FacilioConstants.Meter.METER)).module(module)
				.beanClass(V3MeterContext.class).andCondition(CriteriaAPI.getIdCondition(id, module));
		if (fetchDeleted) {
			resourceBuilder.fetchDeleted();
		}

		return resourceBuilder.fetchFirst();
		
	}
	
	
	public static List<V3MeterContext> getMeterListOfType(long typeId,long buildingId) throws Exception {
		List<Long> buildingIds = null;
		if(buildingId > 0) {
			buildingIds = Collections.singletonList(buildingId);
		}
		return getMeterListOfType(typeId, buildingIds);
	}
	
	public static List<V3MeterContext> getMeterListOfType(long typeId,List<Long> buildingIds) throws Exception {
		return getMeterListOfType(typeId, buildingIds, -1);
	}
	
	public static List<V3MeterContext> getMeterListOfType(long typeId,List<Long> buildingIds, long siteId) throws Exception
	{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.Meter.METER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Meter.METER);
		FacilioField utilityTypeField = FieldFactory.getAsMap(fields).get("utilityType");
		
		
		SelectRecordsBuilder<V3MeterContext> selectBuilder = new SelectRecordsBuilder<V3MeterContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(V3MeterContext.class)
				.andCondition(CriteriaAPI.getCondition(utilityTypeField, String.valueOf(typeId), PickListOperators.IS));
		
		if(buildingIds != null && !buildingIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("METER_LOCATION_ID", "meterLocation", StringUtils.join(buildingIds, ","), BuildingOperator.BUILDING_IS));
		}
		if (siteId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(siteId), PickListOperators.IS));
		}
		List<V3MeterContext> meters = selectBuilder.get();
		return meters;
		
	}

	public static List<V3MeterContext> getMeters(List<Long> meterIds, boolean fetchDeleted) throws  Exception{
		List<V3MeterContext> meters = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(meterIds)){
			for(Long meterId: meterIds){
				V3MeterContext meter = getMeter(meterId,fetchDeleted);
				if(meter != null){
					meters.add(meter);
				}
			}
		}
		return meters;
	}

	public static Map<Long,V3MeterContext> getMetersFromIds(List<Long> meterIds, boolean fetchDeleted) throws  Exception{
		Map<Long, V3MeterContext> meterMap = new HashMap<>();
		List <V3MeterContext> meters = getMeters(meterIds,fetchDeleted);
		if(CollectionUtils.isNotEmpty(meters)){
			for(V3MeterContext meter: meters){
				meterMap.put(meter.getId(), meter);
			}
		}
		return meterMap;
	}

}
