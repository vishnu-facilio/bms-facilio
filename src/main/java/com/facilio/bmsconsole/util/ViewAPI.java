package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.SingleSharingContext.SharingType;
import com.facilio.bmsconsole.context.ViewGroups.ViewGroupType;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.recordcustomization.RecordCustomizationAPI;
import com.facilio.v3.util.TimelineViewUtil;
import com.facilio.weekends.WeekendUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

import static com.facilio.bmsconsole.view.FacilioView.ViewType.getViewType;

public class ViewAPI {

	private static Logger log = LogManager.getLogger(ViewAPI.class.getName());
	
	public static List<FacilioView> getAllViews(Long appId, String moduleName,ViewType type, boolean getOnlyBasicViewDetails) throws Exception {
		return getAllViews(appId, -1,type, getOnlyBasicViewDetails, moduleName);
	}
	
	public static long addViewGroup(ViewGroups viewGroup, long orgId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module= modBean.getModule(moduleName);
		long moduleId = module.getModuleId();
		
		if (moduleId > 0) {
			viewGroup.setModuleId(moduleId);
		}
	    if (moduleName != null) {
			viewGroup.setModuleName(moduleName);
		}
		viewGroup.setOrgId(orgId);
		viewGroup.setId(-1);
		if(viewGroup.getGroupType() == null)
		{
			viewGroup.setGroupType(ViewGroupType.TABLE_GROUP.getIntVal());
		}
		if (viewGroup.getAppId() < 0) {
			ApplicationContext app = viewGroup.getAppId() <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(viewGroup.getAppId());
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			viewGroup.setAppId(app.getId());
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getViewGroupsModule().getTableName())
				.fields(FieldFactory.getViewGroupFields());
		
		Map<String, Object> prop = FieldUtil.getAsProperties(viewGroup);
		insertBuilder.addRecord(prop);
		insertBuilder.save();
		
		return (long) prop.get("id");
		
	}
	
public static void customizeViewGroups(List<ViewGroups> viewGroups) throws Exception {
		
		for(ViewGroups viewGroup: viewGroups)
		{
			int order = viewGroup.getSequenceNumber();
			viewGroup.setSequenceNumber(order + 1000);
			Map<String, Object> prop = FieldUtil.getAsProperties(viewGroup);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getViewGroupsModule().getTableName())
					.fields(FieldFactory.getViewGroupFields())
					.andCustomWhere("ID = ?", viewGroup.getId());

			updateBuilder.update(prop);
		}
		
	}
	
	public static List<ViewGroups> getAllGroups(long moduleId,long appId, String moduleName) throws Exception {
		return getAllGroups(moduleId, appId, moduleName, ViewGroups.ViewGroupType.TABLE_GROUP);
	}

	public static List<ViewGroups> getAllGroups(long moduleId, long appId, String moduleName, ViewGroups.ViewGroupType groupType) throws Exception {
	
		List<ViewGroups> viewGroups = new ArrayList<>();
		if (moduleId > -1 || moduleName != null) {
			List<FacilioField> fields = FieldFactory.getViewGroupFields();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
	
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(ModuleFactory.getViewGroupsModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("groupType"), String.valueOf(groupType.getIntVal()),NumberOperators.EQUALS));
			
			if (moduleId > 0) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS));
			}
			
			else if (moduleName != null) {
				selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleName"), moduleName, StringOperators.IS));
			}
	
			ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			}
	
			Criteria appCriteria = new Criteria();
			appCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), String.valueOf(app.getId()), NumberOperators.EQUALS));
			if(app.getLinkName().equals(ApplicationLinkNames.FACILIO_MAIN_APP)) {
				appCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), CommonOperators.IS_EMPTY));
			}
			selectBuilder.andCriteria(appCriteria);
			List<Map<String, Object>> props = selectBuilder.get();
	
			if (props != null && !props.isEmpty()) {
	
				for(Map<String, Object> prop : props) {
					ViewGroups viewGroup = FieldUtil.getAsBeanFromMap(prop, ViewGroups.class);
					viewGroups.add(viewGroup);
				}
			}
		}
	
		return viewGroups;
	
	
	}

		public static List<FacilioView> getAllViews(Long appId, long moduleId, ViewType type, boolean getOnlyBasicValues, String... moduleName) throws Exception {
		//List<FacilioView> views = new ArrayList<>();
		Map<Long, FacilioView> viewMap = new HashMap<>();
		List<Long> viewIds = new ArrayList<>();
		try 
		{
			FacilioModule module = ModuleFactory.getViewsModule();
			List<FacilioField> fields = FieldFactory.getViewFields();
			Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCondition(fieldsMap.get("type"), String.valueOf(type.getIntVal()),NumberOperators.EQUALS))
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.orderBy("SEQUENCE_NUMBER");
			
			if (moduleId != -1) {
				builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS));
			}
			else {
				builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleName"), moduleName[0], StringOperators.IS));
			}
			ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			}

			Criteria appCriteria = new Criteria();
			appCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("appId"), String.valueOf(app.getId()), NumberOperators.EQUALS));
			if(app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
				appCriteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("appId"), CommonOperators.IS_EMPTY));
			}
			builder.andCriteria(appCriteria);

			List<Map<String, Object>> viewProps = builder.get();
			List<FacilioView> views = getAllViewDetails(viewProps, module.getOrgId(), getOnlyBasicValues);

			if(views != null)
			{
				for(FacilioView view : views)
				{
					viewMap.put(view.getId(), view);
				}
			}
			/*
			List<Long> criteriaIds = new ArrayList<>();
			for(Map<String, Object> viewProp : viewProps) 
			{
				//views.add(FieldUtil.getAsBeanFromMap(viewProp, FacilioView.class));
				FacilioView view = FieldUtil.getAsBeanFromMap(viewProp, FacilioView.class);
				viewMap.put(view.getId(), view);
				viewIds.add(view.getId());
				if (StringUtils.isEmpty(view.getModuleName()) && view.getModuleId() > 0) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					view.setModuleName(modBean.getModule(view.getModuleId()).getName());
				}
				if (view.getCriteriaId() > 0) {
					criteriaIds.add(view.getCriteriaId());
				}
			}
			
			if (!criteriaIds.isEmpty() && criteriaIds != null) {
				Map<Long, Criteria> criteriaValueMap = CriteriaAPI.getCriteriaAsMap(criteriaIds);
				for (FacilioView view : viewMap.values())  {
					if (view.getCriteriaId() > 0 && criteriaValueMap.get(view.getCriteriaId()) != null) {
						view.setCriteria(criteriaValueMap.get(view.getCriteriaId()));
					}
				}
			}
			*/

			
		} 
		catch (Exception e) 
		{
			log.info("Exception occurred ", e);
			throw e;
		}
		return new ArrayList<>(viewMap.values());
	}
	
	public static FacilioView getView(String name, String moduleName, long orgId, long appId) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(FieldFactory.getViewFields())
													.table("Views")
													.andCustomWhere("ORGID = ? AND MODULENAME = ? AND NAME = ?", orgId, moduleName, name);
			
			ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			
			if (app != null) {
				builder.andCustomWhere("APP_ID = ?", app.getId());
			}
			
			List<Map<String, Object>> viewProps = builder.get();
			return getViewDetails(viewProps, orgId);
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
	}
	
	public static FacilioView getView(String name, long moduleId, long orgId, long appId) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(FieldFactory.getViewFields())
													.table("Views")
													.andCustomWhere("ORGID = ? AND MODULEID = ? AND NAME = ?", orgId, moduleId, name);
			
			ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			
			if (app != null) {
				builder.andCustomWhere("APP_ID = ?", app.getId());
			}
			
			List<Map<String, Object>> viewProps = builder.get();
			return getViewDetails(viewProps, orgId);
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
	}

	public static FacilioView getViewDetails(List<Map<String, Object>> viewProps, long orgId) throws Exception{
		List<FacilioView> allViewDetails = getAllViewDetails(viewProps, orgId);
		return (allViewDetails != null && allViewDetails.size() > 0) ? allViewDetails.get(0) : null;
	}

	public static List<FacilioView> getAllViewDetails(List<Map<String, Object>> viewPropList, long orgId) throws Exception
	{
		return getAllViewDetails(viewPropList, orgId, false);
	}
	public static List<FacilioView> getAllViewDetails(List<Map<String, Object>> viewPropList, long orgId, boolean getOnlyBasicValues) throws Exception
	{
		if(viewPropList != null && !viewPropList.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//			List<Long> viewIds = new ArrayList<>();

			Map<ViewType, List<Long>> viewTypeMap = new HashMap<>();
			for(Map viewDetail : viewPropList){
				int viewTypeInt = (int) viewDetail.get("type");
				ViewType viewType = getViewType(viewTypeInt);
				Long viewId = (Long) viewDetail.get("id");
				List<Long> viewIds;
				if(viewTypeMap.containsKey(viewType)) {
					viewIds = viewTypeMap.get(viewType);
				} else {
					viewIds = new ArrayList<>();
				}
				viewIds.add(viewId);
				viewTypeMap.put(viewType, viewIds);
			}
			List<FacilioView> views = new ArrayList<>();
			Map<ViewType, Map<Long, Map<String, Object>>> typeBasedValues = (getOnlyBasicValues) ? null : getDependentTableValues(viewTypeMap);
			for(Map viewDetail : viewPropList){
				int viewTypeInt = (int) viewDetail.get("type");
				Long viewId = (Long) viewDetail.get("id");

				FacilioView view;
				if(getOnlyBasicValues)
				{
					view = FieldUtil.getAsBeanFromMap(viewDetail, FacilioView.class);
				}
				else
				{
					if(typeBasedValues.containsKey(getViewType(viewTypeInt)) && typeBasedValues.get(getViewType(viewTypeInt)).containsKey(viewId)){
						viewDetail.putAll(typeBasedValues.get(getViewType(viewTypeInt)).get(viewId));
					}
					switch (getViewType(viewTypeInt)) {
						case TIMELINE:
							view = FieldUtil.getAsBeanFromMap(viewDetail, TimelineViewContext.class);
							setTimelineFieldObjects((TimelineViewContext) view, modBean, orgId);
							break;
						default:
							view = FieldUtil.getAsBeanFromMap(viewDetail, FacilioView.class);
							//sortFields are available only for table list view
							List<ViewField> columns = getViewColumns(view.getId());
							view.setFields(columns);
							view.setSortFields(getSortFields(view.getId(), view.getModuleName()));
							break;
					}
				}
				view.setViewType(view.getType());
				if (view.getCriteriaId() != -1) {
					Criteria criteria = CriteriaAPI.getCriteria(orgId, view.getCriteriaId());
					setCriteriaValue(criteria);
					view.setCriteria(criteria);
				}
				views.add(view);
			}
			return views;
		}
		return null;
	}

	public static void setTimelineFieldObjects(TimelineViewContext record,ModuleBean moduleBean, long orgId) throws Exception{
		record.setStartDateField(moduleBean.getField(record.getStartDateFieldId()));
		record.setEndDateField(moduleBean.getField(record.getEndDateFieldId()));
		record.setGroupByField(moduleBean.getField(record.getGroupByFieldId()));
		if(record.getRecordCustomizationId() > 0) {
			record.setRecordCustomization(RecordCustomizationAPI.getRecordCustomization(record.getRecordCustomizationId()));
		}
		if(record.getWeekendId() > 0) {
			record.setWeekend(WeekendUtil.getWeekend(record.getWeekendId()));
		}
		if (record.getGroupCriteriaId() > 0) {
			Criteria criteria = CriteriaAPI.getCriteria(orgId, record.getGroupCriteriaId());
			setCriteriaValue(criteria);
			record.setGroupCriteria(criteria);
		}
	}

	public static Map<ViewType, Map<Long, Map<String, Object>>> getDependentTableValues(Map<ViewType, List<Long>> extendedIdList) throws Exception {
		Map<ViewType, Map<Long, Map<String, Object>>> typeBasedMap = new HashMap<>();
		for(Map.Entry<ViewType, List<Long>> entry : extendedIdList.entrySet()) {
			switch (entry.getKey()){
				case TIMELINE:
						typeBasedMap.put(entry.getKey(),getExtendedProps(ModuleFactory.getTimelineViewModule(),FieldFactory.getTimelineViewFields(ModuleFactory.getTimelineViewModule()),entry.getValue()));
					break;
				default:
					break;

			}

		}
		return  typeBasedMap;
	}

	private static Map<Long, Map<String, Object>> getExtendedProps(FacilioModule module, List<FacilioField> fields, List<Long> viewIds) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("ID", "id", StringUtils.join(viewIds, ","), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, Map<String, Object>> propsMap = new HashMap<>();
		for (Map<String, Object> prop : props) {
			propsMap.put((Long) prop.get("id"), prop);
		}
		return propsMap;
	}
	
	public static FacilioView getView(long viewId) throws Exception {
		try {
			FacilioModule module = ModuleFactory.getViewsModule();
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(FieldFactory.getViewFields())
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(viewId, module));
			
			List<Map<String, Object>> viewProps = builder.get();
			return getViewDetails(viewProps, AccountUtil.getCurrentOrg().getId());
			
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
	}
	
	public static int getViewTypeById(long viewId) throws Exception {
		try {
			FacilioModule module = ModuleFactory.getViewsModule();
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(FieldFactory.getViewFields())
													.table(module.getTableName())
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(viewId, module));
			
			List<Map<String, Object>> viewProps = builder.get();
			return (int)viewProps.get(0).get("type");
			
		} catch (Exception e) {
			log.info("Error while fetching viewtype from id", e);
			throw e;
		}
	}
	
	public static long addView(FacilioView view, long orgId) throws Exception {
		view.setOrgId(orgId);
		view.setId(-1);
		if (view.getAppId() < 0) {
			ApplicationContext app = view.getAppId() <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(view.getAppId());
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			
			view.setAppId(app.getId());
		}

		//compute linkname
		if (!view.getIsDefault()) {
			computeLinkName(view);
		}

		try {
			Criteria criteria = view.getCriteria();
			if(criteria != null) {
				long criteriaId = CriteriaAPI.addCriteria(criteria, orgId);
				view.setCriteriaId(criteriaId);
			}
			
			Map<String, Object> viewProp = FieldUtil.getAsProperties(view);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("Views")
															.fields(FieldFactory.getViewFields())
															.addRecord(viewProp);
			
			insertBuilder.save();
			long viewId = (long) viewProp.get("id");
			view.setId(viewId);
			addOrUpdateExtendedViewDetails(view, viewProp, true);
			
			addViewSharing(view);
			
			return viewId;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
			 if (e.getMessage().contains("Duplicate entry")) {
				 throw new IllegalArgumentException("Name already taken");
             } else {
                 throw e;
             }
		}
	}

	public static void addOrUpdateExtendedViewDetails(FacilioView view, Map<String,Object> viewProp, Boolean isNew) throws Exception{
		addOrUpdateExtendedViewDetails(view, viewProp, isNew, null);
	}

	public static void addOrUpdateExtendedViewDetails(FacilioView view, Map<String,Object> viewProp, Boolean isNew, FacilioView oldView) throws Exception{
		ViewType type = view.getTypeEnum();
		switch (type){
			case TIMELINE:
				validateTimeLineView((TimelineViewContext)view);
				long oldCustomizationId = TimelineViewUtil.getCustomizationIdFromViewId(view.getId());
				if(oldCustomizationId > 0)
				{
					RecordCustomizationAPI.deleteCustomization(oldCustomizationId);
				}
				if(((TimelineViewContext) view).getRecordCustomization() != null) {
					long recordCustomizationId = RecordCustomizationAPI.addOrUpdateRecordCustomizationValues(((TimelineViewContext) view).getRecordCustomization());
					viewProp.put("recordCustomizationId", recordCustomizationId);
				}
				Criteria criteria = ((TimelineViewContext) view).getGroupCriteria();
				if(criteria != null) {
					long criteriaId = CriteriaAPI.addCriteria(criteria, AccountUtil.getCurrentOrg().getOrgId());
					viewProp.put("groupCriteriaId",criteriaId);
				}
				else {
					viewProp.put("groupCriteriaId", -99);
				}

				if(isNew)
				{
					addDependentTables(ModuleFactory.getTimelineViewModule(), FieldFactory.getTimelineViewFields(ModuleFactory.getTimelineViewModule()), viewProp);
				}
				else
				{
					updateDependentTables(ModuleFactory.getTimelineViewModule(), FieldFactory.getTimelineViewFields(ModuleFactory.getTimelineViewModule()), viewProp);
					if(oldView != null && ((TimelineViewContext)oldView).getGroupCriteriaId() > 0 ) {
						CriteriaAPI.deleteCriteria(((TimelineViewContext)oldView).getGroupCriteriaId());
					}
				}
				break;
			default:
				if(isNew)
				{
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module;
					if (LookupSpecialTypeUtil.isSpecialType(view.getModuleName())) {
						module =  modBean.getModule(view.getModuleName());
					} else {
						module =  modBean.getModule(view.getModuleId());
					}
					
					List<SortField> defaultSortFields = ColumnFactory.getDefaultSortField(module.getName());
				
					if (defaultSortFields != null && !defaultSortFields.isEmpty()) {
						for (int i = 0; i < defaultSortFields.size(); i++) {
							FacilioField sortfield = modBean.getField(defaultSortFields.get(i).getSortField().getName(), module.getName());
							Long fieldID = modBean.getField(sortfield.getName(), module.getName()).getFieldId();
							defaultSortFields.get(i).setFieldId(fieldID);
							defaultSortFields.get(i).setOrgId(view.getOrgId());
							defaultSortFields.get(i).setFieldName(sortfield.getName());
						}
						customizeViewSortColumns((long) viewProp.get("id"), defaultSortFields);
					}
				}
				break;
		}
	}
	
	private static void addDependentTables(FacilioModule module, List<FacilioField> fields, Map<String, Object> viewProperties) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(module.getTableName())
															.fields(fields)
															.addRecord(viewProperties);
		insertBuilder.save();
	}
	
	private static void updateDependentTables(FacilioModule module, List<FacilioField> fields, Map<String, Object> viewProperties) throws Exception {
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(fields)
                .andCondition(CriteriaAPI.getIdCondition((long)viewProperties.get("id"), module));
		updateRecordBuilder.update(viewProperties);
	}
	
	
	private static void validateTimeLineView(TimelineViewContext view) throws Exception
	{
		if(view.getStartDateFieldId() == view.getEndDateFieldId()){
            throw new Exception("startDate field and endDate field cannot be same");
        }

		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
        FacilioField startDateField = moduleBean.getField(view.getStartDateFieldId());
        checkFieldType(startDateField, Arrays.asList(FieldType.DATE,FieldType.DATE_TIME));

        FacilioField endDateField = moduleBean.getField(view.getEndDateFieldId());
        checkFieldType(endDateField,Arrays.asList(FieldType.DATE,FieldType.DATE_TIME));

        FacilioField groupByField = moduleBean.getField(view.getGroupByFieldId());
        checkFieldType(groupByField,Arrays.asList(FieldType.LOOKUP,FieldType.ENUM));
	}
		
	public static void checkFieldType(FacilioField field,List<FieldType> acceptedFieldType) throws Exception{
		if(!(acceptedFieldType.contains(field.getDataTypeEnum()))){
			throw new Exception("Field type doesn't match");
		}
	}
	
	
	
	public static long updateView(long viewId, FacilioView view) throws Exception {
		try {
			FacilioView oldView = getView(viewId);
			Criteria criteria = view.getCriteria();
			if(criteria != null) {
				long criteriaId = CriteriaAPI.addCriteria(criteria, AccountUtil.getCurrentOrg().getId());
				view.setCriteriaId(criteriaId);
			}

			Map<String, Object> viewProp = FieldUtil.getAsProperties(view);
			FacilioModule viewModule = ModuleFactory.getViewsModule();
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(viewModule.getTableName())
															.fields(FieldFactory.getViewFields())
															.andCondition(CriteriaAPI.getIdCondition(viewId, viewModule));
			
			int count = updateBuilder.update(viewProp);
			
			addOrUpdateExtendedViewDetails(view, viewProp, false, oldView);
			
			if(criteria != null) {
				CriteriaAPI.deleteCriteria(oldView.getCriteriaId());
			}
			if (view.getFields() != null) {
			customizeViewColumns(view.getId(), view.getFields());
			}

			// TODO update sort fields and view columns
			
			/*ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module;
			if (LookupSpecialTypeUtil.isSpecialType(view.getModuleName())) {
				module =  modBean.getModule(view.getModuleName());
			} else {
				module =  modBean.getModule(view.getModuleId());
			}
			
			List<SortField> defaultSortFileds = ColumnFactory.getDefaultSortField(module.getName());
		
			if (defaultSortFileds != null && !defaultSortFileds.isEmpty()) {
				for (int i = 0; i < defaultSortFileds.size(); i++) {
					FacilioField sortfield = modBean.getField(defaultSortFileds.get(i).getSortField().getName(), module.getName());
					Long fieldID = modBean.getField(sortfield.getName(), module.getName()).getFieldId();
					defaultSortFileds.get(i).setFieldId(fieldID);
					defaultSortFileds.get(i).setOrgId(AccountUtil.getCurrentOrg().getId());
				}
				
				customizeViewSortColumns((long) viewProp.get("id"), defaultSortFileds);
			}*/
			
			return count;
			
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
	}
	
	public static void customizeViews(List<FacilioView> views) throws Exception {
		
		for(FacilioView view: views)
		{
			int order = view.getSequenceNumber();
			view.setSequenceNumber(order + 1000);
			Map<String, Object> prop = FieldUtil.getAsProperties(view);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getViewsModule().getTableName())
					.fields(FieldFactory.getViewFields())
					.andCustomWhere("ID = ?", view.getId());

			updateBuilder.update(prop);
		}
		
	}
	
	
	public static void customizeViewColumns(long viewId, List<ViewField> columns) throws Exception {
		try {
			deleteViewColumns(viewId);
			
			List<Map<String, Object>> props = new ArrayList<>();
			for(ViewField field: columns)
			{
				FacilioField fieldDetails = field.getField();
				if (field.getFieldId() == -1 && StringUtils.isEmpty(field.getFieldName()) && (fieldDetails.getFieldId() == -1 && StringUtils.isEmpty(fieldDetails.getName()))) {
					throw new IllegalArgumentException("column field is required");
				}
				field.setViewId(viewId);
				Map<String, Object> prop = FieldUtil.getAsProperties(field);
				props.add(prop);
			}
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getViewColumnsModule().getTableName())
															.fields(FieldFactory.getViewColumnFields())
															.addRecords(props);
			insertBuilder.save();
			
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
	}
	
	public static void customizeViewSortColumns(long viewId, List<SortField> sortfields) throws Exception {
		try {
			deleteViewSortColumns(viewId);
			
			List<Map<String, Object>> props = new ArrayList<>();
			
			for(SortField field: sortfields) {
				if (field.getFieldId() == -1 && StringUtils.isEmpty(field.getFieldName())) {
					throw new IllegalArgumentException("Sort field is required");
				}
				field.setViewId(viewId);
				field.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				Map<String, Object> prop = FieldUtil.getAsProperties(field);
				props.add(prop);
			}
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getViewSortColumnsModule().getTableName())
					.fields(FieldFactory.getViewSortColumnFields())
					.addRecords(props);
			
			insertBuilder.save();
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
	}
	
	public static int deleteViewColumns(long viewId) throws Exception {
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getViewColumnsModule().getTableName())
				.andCustomWhere("VIEWID = ?", viewId);
		
		return builder.delete();

	}
	
	public static int deleteView(FacilioView viewDetail) throws Exception {
		//Deleting dependencies
		switch (getViewType(viewDetail.getType())) {
			case TIMELINE:
				if(((TimelineViewContext)viewDetail).getRecordCustomizationId() > 0)
				{
					RecordCustomizationAPI.deleteCustomization(((TimelineViewContext)viewDetail).getRecordCustomizationId());
				}
				break;
			default:
				break;
		}

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getViewsModule().getTableName())
				.andCustomWhere("Views.ID = ?", viewDetail.getId());
		return builder.delete();

	}
	public static int deleteViewSortColumns(long viewId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getViewSortColumnsModule().getTableName())
				.andCustomWhere("ORGID = ? AND VIEWID = ?", AccountUtil.getCurrentOrg().getId(), viewId);
		return builder.delete();
	}
	
	public static List<ViewField> getViewColumns(long viewId) throws Exception {
		List<ViewField> columns = new ArrayList<>();
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(ModuleFactory.getViewColumnsModule().getTableName())
													.select(FieldFactory.getViewColumnFields())
													.andCustomWhere("VIEWID = ?", viewId)
													.orderBy("ID");
			
			List<Map<String, Object>> props = builder.get();
			
			for(Map<String, Object> prop : props) {
				columns.add(FieldUtil.getAsBeanFromMap(prop, ViewField.class));
			}
			setViewFieldsProp(columns, null);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
			throw e;
		}
		return columns;
	}
	
	public static List<SortField> getSortFields(long viewID, String moduleName) throws Exception {
		List<SortField> sortFields = new ArrayList<>();
		
		try {
			List<FacilioField> fields = FieldFactory.getViewSortColumnFields();
			FacilioModule viewSortColumnsModule = ModuleFactory.getViewSortColumnsModule();
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(viewSortColumnsModule.getTableName())
					.select(fields)
					.andCustomWhere("ORGID = ? AND VIEWID = ?", AccountUtil.getCurrentOrg().getOrgId(), viewID)
					.orderBy("ID");
			
			List<Map<String, Object>> props = builder.get();
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			for (Map<String, Object> prop : props) {
				SortField sortField = FieldUtil.getAsBeanFromMap(prop, SortField.class);
				FacilioField field;
				if (sortField.getFieldId() != -1) {
					field = modBean.getField(sortField.getFieldId());					
				}
				else {
					if (FieldFactory.isSystemField(sortField.getFieldName())) {
						field = FieldFactory.getSystemField(sortField.getFieldName(), null);
					}
					else  {
						field = modBean.getField(sortField.getFieldName(), moduleName);
					}
				}
				sortField.setSortField(field);
				sortFields.add(sortField);
			}
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return sortFields;
	}
	
	public static long checkAndAddView(String viewName, String moduleName, List<ViewField> columns, Long groupId, long appId) throws Exception {
		long viewId = -1;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module= modBean.getModule(moduleName);
		long moduleId = module.getModuleId();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		FacilioView view = getView(viewName, moduleId, orgId, appId);
		if(view == null) {
			view = ViewFactory.getView(module, viewName, modBean);
			if(view != null) {
				if(view.getTypeEnum() == null){
					view.setType(ViewType.TABLE_LIST);
				}
				view.setAppId(appId);
				view.setDefault(true);
				view.setViewSharing(null);
				view.setModuleId(moduleId);
				if (groupId != null && groupId > 0) {
					view.setGroupId(groupId);
				}
				viewId = ViewAPI.addView(view, orgId);
				if (columns == null || columns.isEmpty()) {
					columns = view.getFields();
					for(ViewField column: columns) {
						if (StringUtils.isNotEmpty(column.getParentFieldName())) {
							LookupField parentField = (LookupField) modBean.getField(column.getParentFieldName(), moduleName);
							if (parentField != null && parentField.getLookupModule() != null) {
								FacilioField childField = modBean.getField(column.getName(), parentField.getLookupModule().getName());
								column.setFieldId(childField.getFieldId());
								column.setParentField(parentField);
							}
						}
						else {
							Long fieldId = modBean.getField(column.getName(), moduleName).getFieldId();
							column.setFieldId(fieldId);
						}
					}
				}
				List<SortField> sortFields = view.getSortFields();
				if (sortFields != null && !sortFields.isEmpty()) {
					for (SortField field : sortFields) {
						String sortFieldName = field.getSortField().getName();
						FacilioField sortfield = modBean.getField(sortFieldName, moduleName);
						if (sortfield.getFieldId() != -1) {
							field.setFieldId(sortfield.getFieldId());							
						}
						else {
							field.setFieldName(sortFieldName);
						}
						field.setOrgId(orgId);
					}
					customizeViewSortColumns(viewId, sortFields);
				}
			}
			else {
				// For report-like view,  which wont be there in view db or view factory initially
				view = new FacilioView();
				view.setAppId(appId);
				view.setName(viewName);
				view.setModuleId(moduleId);
				view.setType(ViewType.TABLE_LIST);
				view.setDefault(LoadViewCommand.HIDDEN_VIEW_NAMES.contains(viewName));
				view.setHidden(true);
				if (groupId != null && groupId > 0) {
					view.setGroupId(groupId);
				}
				viewId = ViewAPI.addView(view, orgId);
			}
			
		} else {
			viewId = view.getId();
		}
		if (columns != null && !columns.isEmpty()) {
			customizeViewColumns(viewId, columns);
			List<SortField> sortFields = view.getSortFields();
			if (sortFields == null || sortFields.isEmpty()) {
				sortFields = ColumnFactory.getDefaultSortField(moduleName);
			}
			if (sortFields != null && !sortFields.isEmpty()) {
				for (SortField field : sortFields) {
					FacilioField sortfield = modBean.getField(field.getSortField().getName(), moduleName);
					Long fieldID = modBean.getField(sortfield.getName(), moduleName).getFieldId();
					field.setFieldId(fieldID);
					field.setOrgId(orgId);
				}
				customizeViewSortColumns(viewId, sortFields);
			}
		}
		
		return viewId;
	}
	
	public static void addViewSharing(FacilioView view) throws Exception {
		SharingContext<SingleSharingContext> viewSharing = view.getViewSharing();
		SharingAPI.deleteSharingForParent(Collections.singletonList(view.getId()), ModuleFactory.getViewSharingModule());
		
		if(viewSharing == null) {
			viewSharing = new SharingContext<>();
		}
		
			List<Long> orgUsersId = viewSharing.stream().filter(value -> value.getTypeEnum() == SharingType.USER)
					.map(val -> val.getUserId()).collect(Collectors.toList());
			
			if (CollectionUtils.isNotEmpty(orgUsersId) && !orgUsersId.contains(AccountUtil.getCurrentUser().getId())) {
				SingleSharingContext newViewSharing = new SingleSharingContext(); 
				newViewSharing.setUserId(AccountUtil.getCurrentUser().getId());
				newViewSharing.setType(SharingType.USER);
				viewSharing.add(newViewSharing);	
			}
			SharingAPI.addSharing(viewSharing, view.getId(), ModuleFactory.getViewSharingModule());
		
	}
	
	private static void setCriteriaValue(Criteria criteria) throws Exception {
		Map<String, Condition> conditions = criteria.getConditions();
		for(Map.Entry<String, Condition> entry : conditions.entrySet()) {
			Condition condition = entry.getValue();
			if(condition.getOperatorId() == LookupOperator.LOOKUP.getOperatorId() && condition.getCriteriaValueId() != -1) {
				Criteria criteriaValue = CriteriaAPI.getCriteria(criteria.getOrgId(),condition.getCriteriaValueId());
				condition.setCriteriaValue(criteriaValue);
				setCriteriaValue(criteriaValue);
			}
		}
	}
	
	public static void setViewFieldsProp(List<ViewField> fields, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(ViewField viewField: fields) {
			FacilioField field;
			if(viewField.getName() != null && moduleName != null) {
				field = modBean.getField(viewField.getName(), moduleName);
			}
			else {
				field = modBean.getField(viewField.getFieldId());
			}
			viewField.setField(field);
			
			if(viewField.getParentFieldId() != -1) {
				viewField.setParentField(modBean.getField(viewField.getParentFieldId()));
			} else if (viewField.getParentFieldName() != null && StringUtils.isNotEmpty(viewField.getParentFieldName())) {
				LookupField parentField = (LookupField) modBean.getField(viewField.getParentFieldName(), moduleName);
				if (parentField != null && parentField.getLookupModule() != null) {
					FacilioField childField = modBean.getField(viewField.getName(), parentField.getLookupModule().getName());
					viewField.setField(childField);
					viewField.setParentField(parentField);
				}
			}
		}
	}

	public static ViewGroups getViewGroup(Long groupId) throws Exception{
		ViewGroups viewGroup = null;
		GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getViewGroupsModule().getTableName())
				.select(FieldFactory.getViewGroupFields())
				.andCondition(CriteriaAPI.getIdCondition(groupId, ModuleFactory.getViewGroupsModule()));

		List<Map<String, Object>> groupRecord = genericSelectRecordBuilder.get();

		if (groupRecord != null && groupRecord.size() > 0) {
			viewGroup = FieldUtil.getAsBeanFromMap(groupRecord.get(0), ViewGroups.class);
		}

		return viewGroup;
	}

	public static ViewGroups getViewGroup(String name, long moduleId, long appId) throws Exception{
		ViewGroups viewGroup = null;
		List<FacilioField> allFields = FieldFactory.getViewGroupFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(allFields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(allFields)
				.table(ModuleFactory.getViewGroupsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), name, StringOperators.IS));

		if (appId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("appId"), String.valueOf(appId), NumberOperators.EQUALS));
		}

		if (moduleId > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS));
		}

		List<Map<String, Object>> groupRecord = builder.get();

		if (groupRecord != null && groupRecord.size() > 0) {
			viewGroup = FieldUtil.getAsBeanFromMap(groupRecord.get(0), ViewGroups.class);
		}

		return viewGroup;
	}

	public static FacilioView getView(String name, long moduleId, String moduleName, long orgId, long appId) throws Exception{
		List<FacilioField> allFields = FieldFactory.getViewFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(allFields);

		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getViewFields())
					.table("Views")
					.andCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), name, StringOperators.IS));

			if (moduleId > 0) {
				builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS));
			}
			else if (moduleName != null) {
				builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleName"), moduleName, StringOperators.IS));
			}

			ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			}
			Criteria appCriteria = new Criteria();
			appCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("appId"), String.valueOf(app.getId()), NumberOperators.EQUALS));
			if(app.getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
				appCriteria.addOrCondition(CriteriaAPI.getCondition(fieldsMap.get("appId"), CommonOperators.IS_EMPTY));
			}
			builder.andCriteria(appCriteria);

			List<Map<String, Object>> viewProps = builder.get();
			FacilioView view = getViewDetails(viewProps, orgId);

			if ((view == null) && LoadViewCommand.HIDDEN_VIEW_NAMES.contains(name)) {
				ApplicationContext mainApp = ApplicationApi.getApplicationForLinkName(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
				if (app.getId() != mainApp.getId()) {
					view = getView(name, moduleId, moduleName, orgId, mainApp.getId());
					if (view != null) {
						view.setId(-1);
						view.setAppId(app.getId());
					}
				}
			}

			return view;
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
	}

	public static FacilioView getAllView(ModuleBean modBean, FacilioModule module, String viewName) throws Exception {
		FacilioView view = null;

		view = new FacilioView();
		view.setName(viewName);
		view.setDisplayName("All");
		view.setModuleName(module.getName());

		if (modBean == null) {
			modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		}
		List<ViewField> columns = new ArrayList<>();
		List<FacilioField> allFields = modBean.getAllFields(module.getName());
		for (FacilioField field : allFields) {
			ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
			viewField.setFieldName(viewField.getName());
			viewField.setFieldId(field.getFieldId());
			viewField.setField(field);
			columns.add(viewField);
		}

		if (columns != null && module.isCustom()) {
			List<ViewField> fieldsToRemove = new ArrayList<>();
			for(ViewField column : columns) {
				if (column.getName().equals("stateFlowId")) {
					fieldsToRemove.add(column);
				}
				if (module.getStateFlowEnabled() != null && !module.getStateFlowEnabled() && column.getName().equals("moduleState")) {
					fieldsToRemove.add(column);
				}
			}
			columns.removeAll(fieldsToRemove);
		}

		view.setFields(columns);
		view.setDefault(true);

		return view;
	}

	public static void computeLinkName(FacilioView view) throws Exception {
		FacilioModule module = ModuleFactory.getViewsModule();
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getIdField(module));
		fields.add(FieldFactory.getStringField("name", "NAME", module));

		String viewLinkName = view.getName();
		if (StringUtils.isEmpty(viewLinkName)) {
			viewLinkName = view.getDisplayName().toLowerCase().replaceAll("[^a-zA-Z0-9]+", "");
		}

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(view.getModuleId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("APP_ID", "appId", String.valueOf(view.getAppId()), NumberOperators.EQUALS))
				;

		Criteria nameCriteria = new Criteria();
		nameCriteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", viewLinkName, StringOperators.IS));
		nameCriteria.addOrCondition(CriteriaAPI.getCondition("NAME", "name", viewLinkName, StringOperators.STARTS_WITH));
		builder.andCriteria(nameCriteria);

		List<Map<String, Object>> props = builder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			List<String> viewNames = props.stream().map(prop -> (String) prop.get("name")).collect(Collectors.toList());
			int count = 1;
			while (viewNames.contains(viewLinkName)) {
				if (viewLinkName.contains("_")) {
					viewLinkName = viewLinkName.split("_")[0];
				}
				viewLinkName = viewLinkName + "_" + (count++);
			}
		}
		view.setName(viewLinkName);
	}
}
