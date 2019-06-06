package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.time.DateTimeUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConsumptionAPI {

	
public static Map<String,Object> getTotalConsumptionBySites(Long startTime,Long endTime,String moduleName, String fieldName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<Long,Object> outputTotalConsumptionBySite = new HashMap<Long, Object>();
		
	    
		FacilioModule dataModule = modBean.getModule(moduleName);
		FacilioModule resourcesModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
		
		
		List<FacilioField> resourceFields = modBean.getAllFields(resourcesModule.getName());
		Map<String, FacilioField> resourcesFieldMap = FieldFactory.getAsMap(resourceFields);

		List<FacilioField> moduleFields = modBean.getAllFields(dataModule.getName());
		Map<String, FacilioField> moduleFieldMap = FieldFactory.getAsMap(moduleFields);
		
		
		FacilioField consumptionField = moduleFieldMap.get(fieldName);
		String unit = null;
		
		if (consumptionField instanceof NumberField) {
			
			unit = ((NumberField) consumptionField).getUnit();
		}
		
		
		FacilioField parentField = moduleFieldMap.get("parentId");
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		
		FacilioField totalConsumptionField = new FacilioField();
		totalConsumptionField.setName("total_consumption");
		totalConsumptionField.setColumnName("sum(round("+consumptionField.getColumnName()+",2))");
		fields.add(totalConsumptionField);

		FacilioField siteName = resourcesFieldMap.get("name");
		fields.add(siteName);
		
		GenericSelectRecordBuilder selectBuilder = null;
		
			if(moduleName.equals("energydata"))
			{
				
				FacilioModule energyPurposeModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
				FacilioModule energyMeterModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
				FacilioField siteField = FieldFactory.getSiteIdField(energyMeterModule);
				fields.add(siteField);
				
			
		
				selectBuilder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(dataModule.getTableName())
													.innerJoin(energyMeterModule.getTableName()).on(energyMeterModule.getTableName()+".ID = "+dataModule.getTableName()+".PARENT_METER_ID")
													.innerJoin(energyPurposeModule.getTableName()).on(energyPurposeModule.getTableName()+".ID = "+energyMeterModule.getTableName()+".PURPOSE_ID")
													.innerJoin(resourcesModule.getTableName()).on(siteField.getCompleteColumnName()+" = "+resourcesModule.getTableName()+".ID")
													.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN))
													.andCondition(CriteriaAPI.getCondition(dataModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition(energyMeterModule.getTableName()+".IS_ROOT", "root", ""+1, NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition(energyPurposeModule.getTableName()+".NAME", "name", "Main", StringOperators.IS))
													.andCondition(CriteriaAPI.getCondition(siteField, CommonOperators.IS_NOT_EMPTY))
					                                .groupBy(siteField.getCompleteColumnName()+","+siteName.getCompleteColumnName())
													;
			}
			else {
				FacilioModule waterMeterModule = modBean.getModule(FacilioConstants.ContextNames.WATER_METER);
				FacilioField siteField = FieldFactory.getSiteIdField(waterMeterModule);
				fields.add(siteField);
				
			selectBuilder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(dataModule.getTableName())
													.innerJoin(waterMeterModule.getTableName()).on(parentField.getCompleteColumnName()+" = "+waterMeterModule.getTableName()+".ID")
													.innerJoin(resourcesModule.getTableName()).on(siteField.getCompleteColumnName()+" = "+resourcesModule.getTableName()+".ID")
													.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN))
													.andCondition(CriteriaAPI.getCondition(dataModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition(siteField, CommonOperators.IS_NOT_EMPTY))
					                                .groupBy(siteField.getCompleteColumnName()+","+siteName.getCompleteColumnName())
													;
			
			if(AccountUtil.getCurrentOrg().getId() == 92)//for spi cinemas because RO water reading are put inside readings table initially
			{
				FacilioModule readingsModule = modBean.getModule(FacilioConstants.ContextNames.RO_MODULE_SPI_CINEMAS);
				List<FacilioField> readingFields = modBean.getAllFields(readingsModule.getName());
				Map<String, FacilioField> readingsFieldMap = FieldFactory.getAsMap(readingFields);
				
				FacilioField parentReadingField = readingsFieldMap.get("parentId");
				FacilioField waterReadingField = readingsFieldMap.get("rowaterenpinew");
				
				List<FacilioField> readingSelectFields = new ArrayList<FacilioField>();
				
				FacilioField sumConsumptionField = new FacilioField();
				sumConsumptionField.setName("total_consumption");
				sumConsumptionField.setColumnName("sum(round("+waterReadingField.getColumnName()+",2))");
				readingSelectFields.add(sumConsumptionField);
				
				readingSelectFields.add(siteName);

				
				FacilioField readingSiteField = FieldFactory.getSiteIdField(resourcesModule);
				readingSelectFields.add(readingSiteField);
				
				GenericSelectRecordBuilder readingSelectBuilder = new GenericSelectRecordBuilder()
													.select(readingSelectFields)
													.table(readingsModule.getTableName())
													.innerJoin(resourcesModule.getTableName()).on(parentReadingField.getCompleteColumnName()+" = "+resourcesModule.getTableName()+".ID")
													.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN))
													.andCondition(CriteriaAPI.getCondition(readingsModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition(readingsModule.getTableName()+".MODULEID", "moduleId", ""+readingsModule.getModuleId(), NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition(readingSiteField, CommonOperators.IS_NOT_EMPTY))
					                      			.groupBy(readingSiteField.getCompleteColumnName()+","+siteName.getCompleteColumnName())
													;
				List<Map<String,Object>> totalRoReadingsConsumptionBySite  = readingSelectBuilder.get();
				for(int i=0;i<totalRoReadingsConsumptionBySite.size();i++)
				{
					Map<String,Object> consumptionObj = totalRoReadingsConsumptionBySite.get(i) ;
					Long siteId = (Long)consumptionObj.get("siteId");
					outputTotalConsumptionBySite.put(siteId,consumptionObj);
				}

			}

			
			}
				
		List<Map<String,Object>> totalConsumptionBySite = selectBuilder.get();
		
		
		for(int i=0;i<totalConsumptionBySite.size();i++)
		{
			Map<String,Object> consumptionObj = totalConsumptionBySite.get(i) ;
			Long siteId = (Long)consumptionObj.get("siteId");
			outputTotalConsumptionBySite.put(siteId,consumptionObj);
		}
		Map<String,Object> resp = new HashMap<String, Object>();
		
		resp.put("moduleName",dataModule.getDisplayName());
		resp.put("fieldName",consumptionField.getName());
		resp.put("unit",unit);
		resp.put("totalConsumptionData",outputTotalConsumptionBySite);
     
       return resp;
	}

  	public static Map<String,Object> getEnergyConsumptionByAssetsThisMonth(List<Long> asssetIds) throws Exception
  	{
  		
  		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule dataModule = modBean.getModule("energydata");
		FacilioModule energyPurposeModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER_PURPOSE);
		FacilioModule energyMeterModule = modBean.getModule(FacilioConstants.ContextNames.ENERGY_METER);
		FacilioField siteField = FieldFactory.getSiteIdField(energyMeterModule);
		List<FacilioField> fields = new ArrayList<FacilioField>();
		
		List<FacilioField> energyFields = modBean.getAllFields(dataModule.getName());
		Map<String, FacilioField> energyFieldMap = FieldFactory.getAsMap(energyFields);
		FacilioField consumptionField = energyFieldMap.get("totalEnergyConsumptionDelta");
		
		FacilioField totalConsumptionField = new FacilioField();
		totalConsumptionField.setName("total_consumption");
		totalConsumptionField.setColumnName("sum(round("+consumptionField.getColumnName()+",2))");
		fields.add(totalConsumptionField);

		fields.add(siteField);
		
	
        Long startTime = DateTimeUtil.getMonthStartTime(false);
        Long endTime = System.currentTimeMillis();
		
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(dataModule.getTableName())
														.innerJoin(energyMeterModule.getTableName()).on(energyMeterModule.getTableName()+".ID = "+dataModule.getTableName()+".PARENT_METER_ID")
														.innerJoin(energyPurposeModule.getTableName()).on(energyPurposeModule.getTableName()+".ID = "+energyMeterModule.getTableName()+".PURPOSE_ID")
														.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN))
														.andCondition(CriteriaAPI.getCondition(dataModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(energyMeterModule.getTableName()+".IS_ROOT", "root", ""+1, NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(energyPurposeModule.getTableName()+".NAME", "name", "Main", StringOperators.IS))
														.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(energyMeterModule), asssetIds, NumberOperators.EQUALS))
							                           ;
        List<Map<String,Object>> totalConsumption = selectBuilder.get();
		
        Map<String,Object> resp = new HashMap<String, Object>();
		if(totalConsumption.size() > 0) {
		resp.put("energy",totalConsumption.get(0).get("total_consumption"));
		}
		return resp;
       
  }
  	
  	public static Map<String,Object> getWaterConsumptionByAssetsThisMonth(List<Long> asssetIds) throws Exception
  	{
  		
  		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule dataModule = modBean.getModule("waterreading");
		List<FacilioField> fields = new ArrayList<FacilioField>();
		
		List<FacilioField> waterFields = modBean.getAllFields(dataModule.getName());
		Map<String, FacilioField> waterFieldMap = FieldFactory.getAsMap(waterFields);
		FacilioField consumptionField = waterFieldMap.get("waterConsumptionDelta");
		
		FacilioField totalConsumptionField = new FacilioField();
		totalConsumptionField.setName("total_consumption");
		totalConsumptionField.setColumnName("sum(round("+consumptionField.getColumnName()+",2))");
		fields.add(totalConsumptionField);

		Long startTime = DateTimeUtil.getMonthStartTime(false);
        Long endTime = System.currentTimeMillis();
		
    	FacilioModule waterMeterModule = modBean.getModule(FacilioConstants.ContextNames.WATER_METER);
		FacilioField siteField = FieldFactory.getSiteIdField(waterMeterModule);
		fields.add(siteField);
		
	    GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
											.select(fields)
											.table(dataModule.getTableName())
											.innerJoin(waterMeterModule.getTableName()).on(dataModule.getTableName()+".PARENT_ID = "+waterMeterModule.getTableName()+".ID")
											.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN))
											.andCondition(CriteriaAPI.getCondition(dataModule.getTableName()+".ORGID", "orgId", ""+AccountUtil.getCurrentOrg().getOrgId(), NumberOperators.EQUALS))
											.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(waterMeterModule), asssetIds, NumberOperators.EQUALS))
					                        ;
	List<Map<String,Object>> totalConsumption = selectBuilder.get();
		
        Map<String,Object> resp = new HashMap<String, Object>();
        if(totalConsumption.size() > 0) {
        	resp.put("water",totalConsumption.get(0).get("total_consumption"));
    	}
        return resp;
       
  }
  	

}
