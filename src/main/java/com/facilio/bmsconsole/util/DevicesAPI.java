package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.context.FeedbackKioskContext;
import com.facilio.bmsconsole.context.FeedbackTypeContext;
import com.facilio.bmsconsole.context.ServiceCatalogContext;
import com.facilio.bmsconsole.context.SmartControlKioskContext;
import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.bmsconsoleV3.context.V3CustomKioskContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class DevicesAPI {

	public static DeviceContext getDevice(long deviceID) throws Exception {

		FacilioChain fetchDetail = ReadOnlyChainFactory.fetchModuleDataDetailsChain();
		
		FacilioContext context = fetchDetail.getContext();
		
		context.put(FacilioConstants.ContextNames.ID, deviceID);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ModuleNames.DEVICES);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		context.put(FacilioConstants.ContextNames.LOOKUP_FIELD_META_LIST, Collections.singletonList(modBean.getField("associatedResource", FacilioConstants.ModuleNames.DEVICES)));
		fetchDetail.execute();
		DeviceContext deviceDetail = (DeviceContext) context.get(FacilioConstants.ContextNames.RECORD);
		return deviceDetail;

	}

	public static VisitorKioskContext getVisitorKioskDetails(long deviceId) throws Exception {

		FacilioChain visitorKioskConfigDetailsChain = ReadOnlyChainFactory.getVisitorKioskDetailsChain();
		FacilioContext context = visitorKioskConfigDetailsChain.getContext();
		context.put(FacilioConstants.ContextNames.ID, deviceId);
		visitorKioskConfigDetailsChain.execute();

		Object record = context.get(FacilioConstants.ContextNames.RECORD);
		if (record != null) {
			VisitorKioskContext visitorKioskConfig = (VisitorKioskContext) context
					.get(FacilioConstants.ContextNames.RECORD);
			return visitorKioskConfig;
		} else {
			return null;
		}

	}

	public static FeedbackTypeContext getFeedbackType(long feedbackTypeId) throws Exception {
		return getFeedbackType(feedbackTypeId, false);
	}

	public static FeedbackTypeContext getFeedbackType(long feedbackTypeId, Boolean fillForms) throws Exception {

		GenericSelectRecordBuilder selectFeedbackType = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getFeedbackTypeModule().getTableName())
				.select(FieldFactory.getFeedbackTypeFields())
				.andCondition(CriteriaAPI.getCondition("ID", "id", "" + feedbackTypeId, NumberOperators.EQUALS));

		List<Map<String, Object>> feedbackTypeList = selectFeedbackType.get();

		FeedbackTypeContext feedbackType = FieldUtil.getAsBeanFromMap(feedbackTypeList.get(0),
				FeedbackTypeContext.class);
		feedbackType.setCatalogs(getCatalogsForType(feedbackTypeId, fillForms));

		return feedbackType;

	}

	public static List<ServiceCatalogContext> getCatalogsForType(long feedbackTypeId, Boolean fillForms)
			throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getFeedbackTypeCatalogMappingModule().getTableName())
				.select(FieldFactory.getFeedbackTypeCatalogMappingFields()).
				andCondition(CriteriaAPI.getCondition(
						 "FEEDBACK_TYPE_ID", "feedbackTypeId","" + feedbackTypeId, NumberOperators.EQUALS));

		List<Map<String, Object>> resultMap = selectBuilder.get();

		if (resultMap == null||resultMap.isEmpty()) {
			return null;
		}
		// lookup catalog objects from service_catalog table and return

		List<Long> catalogIds = resultMap.stream().map(catalog -> (Long) catalog.get("catalogId"))
				.collect(Collectors.toList());
		
		
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getServiceCatalogModule().getTableName())
				.select(FieldFactory.getServiceCatalogFields());

		FacilioField catalogIdField = FieldFactory.getIdField(ModuleFactory.getServiceCatalogModule());
		builder.andCondition(CriteriaAPI.getCondition(catalogIdField, catalogIds, NumberOperators.EQUALS));

		List<Map<String, Object>> maps = builder.get();
		
		List<ServiceCatalogContext> serviceCatalogs = FieldUtil.getAsBeanListFromMapList(maps,
				ServiceCatalogContext.class);

		if(fillForms!=null&&fillForms)
		{
			for (ServiceCatalogContext serviceCatalogContext : serviceCatalogs) {
				
				serviceCatalogContext.setForm(FormsAPI.getFormFromDB(serviceCatalogContext.getFormId()));
					
			}
			
		}
		
		return serviceCatalogs;
	}

	public static void addCatalogsToFeedbackType(FeedbackTypeContext feedbackType) throws Exception {

		if(feedbackType.getCatalogs()==null)
		{
			return;
		}
		List<Map<String, Object>> propMapList = new ArrayList<>();
		
		long order=0L;
		for (ServiceCatalogContext serviceCatalog: feedbackType.getCatalogs()) {
			
		
			Map<String, Object> propMap = new HashMap<>();
			propMap.put("feedbackTypeId", feedbackType.getId());
			propMap.put("catalogId", serviceCatalog.getId());
			propMap.put("order",order);
			order++;
			propMapList.add(propMap);
		}
		

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFeedbackTypeCatalogMappingModule().getTableName())
				.fields(FieldFactory.getFeedbackTypeCatalogMappingFields())
				.addRecords(propMapList);

		insertBuilder.save();

	}
	
	public static void deleteCatalogsFromFeedbackType(FeedbackTypeContext feedbackType) throws Exception{
		
		GenericDeleteRecordBuilder deleteBuilder =new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getFeedbackTypeCatalogMappingModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("FEEDBACK_TYPE_ID","feedbackTypeId",feedbackType.getId()+"",NumberOperators.EQUALS));
		deleteBuilder.delete();
				
	}
	
	public static FeedbackKioskContext getFeedbackKioskDetails(long deviceId) throws Exception {
		FacilioChain chain=ReadOnlyChainFactory.getFeedbackKioskDetailsChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, deviceId);
		chain.execute();
		

		return (FeedbackKioskContext)context.get(ContextNames.RECORD);
	}
	
	public static SmartControlKioskContext getSmartControlKiosk(long deviceId) throws Exception {
		FacilioChain chain=ReadOnlyChainFactory.getSmartControlKioskDetailsChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, deviceId);
		chain.execute();
		

		return (SmartControlKioskContext)context.get(ContextNames.RECORD);
	}

	public static V3CustomKioskContext getCustomKioskDetails(long deviceId) throws Exception{

		FacilioChain chain= ReadOnlyChainFactoryV3.getCustomKioskDetailsChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, deviceId);
		chain.execute();
		FacilioContext V3CustomKioskContext = (FacilioContext) context.get(FacilioConstants.ContextNames.SUMMARY_CONTEXT);
		Map<String, List> recordMap = (Map<String, List>)  V3CustomKioskContext.get(Constants.RECORD_MAP);
		List<V3CustomKioskContext> customKioskmoduleContexts = recordMap.get(ContextNames.CUSTOM_KIOSK);
        return customKioskmoduleContexts.get(0);

	}


}
