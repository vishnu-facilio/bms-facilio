package com.facilio.bmsconsole.util;

import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;

import java.util.ArrayList;
import java.util.List;

public class HVACPressurePredictorUtil 
{
	
	public static List<FacilioField> getFields()
	{
		List<FacilioField> fields = new ArrayList<>();
		 
		FacilioModule predictionRelationModule = new FacilioModule();
		predictionRelationModule.setName("Prediction_Relation");
		predictionRelationModule.setDisplayName("Prediction_Relation");
		predictionRelationModule.setTableName("Prediction_Relation");
		
		FacilioField orgId = new FacilioField();
        orgId.setName("orgId");
        orgId.setDataType(FieldType.DECIMAL);
        orgId.setColumnName("ORGID");
        orgId.setModule(predictionRelationModule);
        fields.add(orgId);
        
        FacilioField assetId = new FacilioField();
        assetId.setName("assetId");
        assetId.setDataType(FieldType.DECIMAL);
        assetId.setColumnName("ASSET_ID");
        assetId.setModule(predictionRelationModule);
        fields.add(assetId);
        
        FacilioField filedId = new FacilioField();
        filedId.setName("fieldId");
        filedId.setDataType(FieldType.DECIMAL);
        filedId.setColumnName("FIELDID");
        filedId.setModule(predictionRelationModule);
        fields.add(filedId);
        
        FacilioField predictedFieldId = new FacilioField();
        predictedFieldId.setName("predictedFieldId");
        predictedFieldId.setDataType(FieldType.DECIMAL);
        predictedFieldId.setColumnName("PREDICTED_FIELDID");
        predictedFieldId.setModule(predictionRelationModule);
        fields.add(predictedFieldId);
        
        return fields;
	}
	
	/*public static void getModuleDetails(long orgID) throws Exception
	{
		new Thread()
		{
			public void run()
			{
				try
				{
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule("resources");
					
					SelectRecordsBuilder<ResourceContext> resourceBuilder = new SelectRecordsBuilder<ResourceContext>()
																					.select(modBean.getAllFields(FacilioConstants.ContextNames.RESOURCE))
																					.module(module)
																					.beanClass(ResourceContext.class)
																					.andCondition(CriteriaAPI.getOrgIdCondition(orgID, module))
																					.andCondition(CriteriaAPI.getCondition("RESOURCE_TYPE","resourceType",2+"", NumberOperators.EQUALS));
					
					
					List<ResourceContext> resources = resourceBuilder.get();
					System.out.println("AJVEED "+resources.size()+"::"+resources.get(0));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}.start();
		
		
		/*
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		modBean.getField("bagfilterdifferentialpressure","")
		long differentialPressureId = -1, supplyAirFlow = -1;
		
		List<Long> fieldIds = new ArrayList<Long>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for (Long id : fieldIds) {
			FacilioField field = modBean.getField(id);
			
			List<FacilioField> fields = modBean.getAllFields(field.getModule().getName());
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			
			FacilioField timeField = fieldMap.get("ttime");
			FacilioField parentIdField = fieldMap.get("parentId");
			
			List<FacilioField> selectFields = new ArrayList<FacilioField>();
			selectFields.add(field);
			selectFields.add(timeField);
			
			
			SelectRecordsBuilder<ReadingContext> readingsBuilder = new SelectRecordsBuilder<ReadingContext>()
																		.module(field.getModule())
																		.select(selectFields)
																		.andCondition(CriteriaAPI.getCondition(parentIdField, String.valueOf(-1), PickListOperators.IS))
																		.andCondition(CriteriaAPI.getCondition(timeField, String.valueOf(-1), PickListOperators.IS))
																		.andCondition(CriteriaAPI.getCondition(field, CommonOperators.IS_NOT_EMPTY))
																		;
			List<Map<String, Object>> props = readingsBuilder.getAsProps();
			*/
		//}

}
