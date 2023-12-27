package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.calendarview.CalendarViewContext;
import com.facilio.bmsconsole.calendarview.CalendarViewUtil;
import com.facilio.bmsconsole.calendarview.CommonCalendarViewContext;
import com.facilio.bmsconsole.commands.LoadViewCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.SingleSharingContext.SharingType;
import com.facilio.bmsconsole.context.ViewGroups.ViewGroupType;
import com.facilio.bmsconsole.timelineview.context.TimelineViewContext;
import com.facilio.bmsconsole.timelineview.context.TimelineScheduledViewContext;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ApplicationLinkNames;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.delegate.context.DelegationType;
import com.facilio.fields.context.FieldListType;
import com.facilio.fields.context.ModuleViewField;
import com.facilio.fields.util.FieldsConfigChainUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.recordcustomization.RecordCustomizationAPI;
import com.facilio.recordcustomization.RecordCustomizationContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.TimelineViewUtil;
import com.facilio.weekends.WeekendUtil;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
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

		User currentUser = AccountUtil.getCurrentUser();
		viewGroup.setSysCreatedTime(System.currentTimeMillis());
		viewGroup.setSysModifiedTime(viewGroup.getSysCreatedTime());
		viewGroup.setSysCreatedBy(currentUser.getId());
		viewGroup.setSysModifiedBy(currentUser.getId());

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

	public static void updateViewGroup(ViewGroups viewGroup) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getViewGroupsModule().getTableName())
				.fields(FieldFactory.getViewGroupFields())
				.andCondition(CriteriaAPI.getIdCondition(viewGroup.getId(), ModuleFactory.getViewGroupsModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(viewGroup);
		updateBuilder.update(props);
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
		List<FacilioField> fields = FieldFactory.getViewGroupFields();
		return getAllGroups(moduleId, appId, moduleName, groupType, fields, false, false);
	}
	public static List<ViewGroups> getAllGroups(long moduleId, long appId, String moduleName, ViewGroups.ViewGroupType groupType, boolean getViewsCount, boolean fromBuilder) throws Exception{
		List<FacilioField> fields = FieldFactory.getViewGroupFields();
		return getAllGroups(moduleId, appId, moduleName, groupType, fields, getViewsCount, fromBuilder);
	}
	public static List<ViewGroups> getAllGroups(long moduleId, long appId, String moduleName, ViewGroups.ViewGroupType groupType, List<FacilioField> fields, boolean getViewsCount, boolean fromBuilder) throws Exception {
		List<ViewGroups> viewGroups = new ArrayList<>();
		if (moduleId > -1 || moduleName != null) {
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
			if(getViewsCount){
				List<FacilioField> viewFields = FieldFactory.getViewFields();
				Map<String, FacilioField> viewFieldsAsMap = FieldFactory.getAsMap(viewFields);
				FacilioField viewIdField = viewFieldsAsMap.get("id");
				viewIdField.setName("viewCount");
				selectBuilder.leftJoin(ModuleFactory.getViewsModule().getTableName())
						.on("View_Groups.ID = Views.GROUPID")
						.aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, viewFieldsAsMap.get("id"))
						.groupBy("View_Groups.ID");
				if (!fromBuilder) {
					Criteria statusFilterCriteria = new Criteria();
					statusFilterCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(true), BooleanOperators.IS));
					statusFilterCriteria.addOrCondition(CriteriaAPI.getCondition(viewFieldsAsMap.get("status"), CommonOperators.IS_EMPTY));
					selectBuilder.andCriteria(statusFilterCriteria);
				}
			}
			selectBuilder.andCriteria(appCriteria);
			selectBuilder.orderBy("View_Groups.SEQUENCE_NUMBER");
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
		Map<Long, FacilioView> viewMap = new HashMap<>();
		try
		{
			FacilioModule module = ModuleFactory.getViewsModule();
			List<FacilioField> fields = FieldFactory.getViewFields();
			Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCondition(fieldsMap.get("type"), String.valueOf(type.getIntVal()),NumberOperators.EQUALS))
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
		List<FacilioView> allViewDetails = getAllViewDetails(viewProps, orgId, false);
		return (allViewDetails != null && allViewDetails.size() > 0) ? allViewDetails.get(0) : null;
	}

	public static List<FacilioView> getAllViewDetails(List<Map<String, Object>> viewPropList, long orgId, boolean getOnlyBasicValues) throws Exception
	{
		if(viewPropList != null && !viewPropList.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<Long> calendarViewIds = new ArrayList<>();
			List<Long> timelineViewIds = new ArrayList<>();

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
				// calendarView enabled
				boolean isCalendarView = viewDetail.containsKey("isCalendarView") && (boolean) viewDetail.get("isCalendarView");
				if (isCalendarView) {
					calendarViewIds.add(viewId);
				}
				boolean isTimelineView = viewDetail.containsKey("isTimelineView") && (boolean) viewDetail.get("isTimelineView");
				if (isTimelineView) {
					timelineViewIds.add(viewId);
				}
			}
			List<FacilioView> views = new ArrayList<>();

			Map<Long, CalendarViewContext> calendarViewContextMap = new HashMap<>();
			Map<Long, TimelineScheduledViewContext> timelineScheduledViewContextMap = new HashMap<>();

			if (CollectionUtils.isNotEmpty(calendarViewIds)) {
				calendarViewContextMap = CalendarViewUtil.getAllCalendarViews(calendarViewIds);
			}
			if (CollectionUtils.isNotEmpty(timelineViewIds)) {
				timelineScheduledViewContextMap = TimelineViewUtil.getAllTimelineViews(timelineViewIds);
			}

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

							// add view fields & sort fields
							if (view.isListView()) {
								view.setSortFields(getSortFields(view.getId(), view.getModuleName()));
								view.setFields(getViewColumns(view.getId()));
							}

							// add calendar view context
							if (view.isCalendarView()) {
								if (MapUtils.isNotEmpty(calendarViewContextMap) && calendarViewContextMap.containsKey(viewId)) {
									CalendarViewContext calendarViewContext = calendarViewContextMap.get(viewId);
									setCommonCalendarViewObjects(calendarViewContext);
									view.setCalendarViewContext(calendarViewContext);
								}
							} else if (view.isTimelineView()) {
								if (MapUtils.isNotEmpty(timelineScheduledViewContextMap) && timelineScheduledViewContextMap.containsKey(viewId)) {
									TimelineScheduledViewContext timelineScheduledViewContext = timelineScheduledViewContextMap.get(viewId);
									setTimelineScheduledView(timelineScheduledViewContext, modBean, orgId);
									view.setTimelineScheduledViewContext(timelineScheduledViewContext);
								}
							}

							break;
					}
				}
				view.setViewType(view.getType());

				if ((!getOnlyBasicValues) && (view.getCriteriaId() != -1)) {
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

	public static void setTimelineScheduledView(TimelineScheduledViewContext timelineScheduledViewContext, ModuleBean moduleBean, long orgId) throws Exception{
		setCommonCalendarViewObjects(timelineScheduledViewContext);
		timelineScheduledViewContext.setGroupByField(moduleBean.getField(timelineScheduledViewContext.getGroupByFieldId()));
		if (timelineScheduledViewContext.getGroupCriteriaId() > 0) {
			Criteria criteria = CriteriaAPI.getCriteria(orgId, timelineScheduledViewContext.getGroupCriteriaId());
			setCriteriaValue(criteria);
			timelineScheduledViewContext.setGroupCriteria(criteria);
		}
	}

	public static void setCommonCalendarViewObjects(CommonCalendarViewContext commonCalendarViewContext) throws Exception {
		ModuleBean moduleBean = Constants.getModBean();
		if (commonCalendarViewContext.getEndDateFieldId() > 0) {
			commonCalendarViewContext.setEndDateField(moduleBean.getField(commonCalendarViewContext.getEndDateFieldId()));
		}
		if (commonCalendarViewContext.getStartDateFieldId() > 0) {
			commonCalendarViewContext.setStartDateField(moduleBean.getField(commonCalendarViewContext.getStartDateFieldId()));
		}
		if(commonCalendarViewContext.getRecordCustomizationId() > 0) {
			commonCalendarViewContext.setRecordCustomization(RecordCustomizationAPI.getRecordCustomization(commonCalendarViewContext.getRecordCustomizationId()));
		}
		if(commonCalendarViewContext.getWeekendId() > 0) {
			commonCalendarViewContext.setWeekend(WeekendUtil.getWeekend(commonCalendarViewContext.getWeekendId()));
		}
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

	public static List<FacilioView> getViewsFromIds(List<Long> viewIds, boolean getOnlyBasicValues) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		FacilioModule module = ModuleFactory.getViewsModule();
		List<FacilioField> fields = FieldFactory.getViewFields();

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(viewIds, module));

		List<Map<String, Object>> viewProps = builder.get();
		if (CollectionUtils.isNotEmpty(viewProps)) {
			return getAllViewDetails(viewProps, orgId, getOnlyBasicValues);

		}
		return null;
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
		if(view.getStatus() == null){
			view.setStatus(true);
		}
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
			User currentUser = AccountUtil.getCurrentUser();
			view.setSysCreatedTime(System.currentTimeMillis());
			view.setSysCreatedBy(currentUser.getId());
			view.setSysModifiedTime(System.currentTimeMillis());
			view.setSysModifiedBy(currentUser.getId());
			
			Map<String, Object> viewProp = FieldUtil.getAsProperties(view);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("Views")
															.fields(FieldFactory.getViewFields())
															.addRecord(viewProp);
			
			insertBuilder.save();
			long viewId = (long) viewProp.get("id");
			view.setId(viewId);

			addOrUpdateExtendedViewDetails(view, viewProp, true);

			if (CollectionUtils.isNotEmpty(view.getFields())) {
				customizeViewColumns(viewId, view.getFields());
			}

			addViewSharing(view);
			addViewGroupSharing(view.getGroupId());
			
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

				// calendar view is inserted only when calendarView is enabled
				if (view.isCalendarView()) {
					validateCalendarView(view.getCalendarViewContext());
					Map<String, Object> calendarViewProps = getCalendarViewProps(viewProp);
					FacilioUtil.throwIllegalArgumentException(MapUtils.isEmpty(calendarViewProps), "CalendarView props is not defined");

					FacilioModule calendarModule = ModuleFactory.getCalendarViewModule();
					List<FacilioField> calendarModuleFields = FieldFactory.getCalendarViewFields(calendarModule);

					if (!isNew) {
						CalendarViewContext oldCalendarView = CalendarViewUtil.getCalendarView(view.getId());
						isNew = oldCalendarView == null;
					}

					if(isNew) {
						addDependentTables(calendarModule, calendarModuleFields, calendarViewProps);
					} else {
						updateDependentTables(calendarModule, calendarModuleFields, calendarViewProps);
					}
				}
//				timeline view props
				else if (view.isTimelineView()) {
					TimelineScheduledViewContext timelineScheduledViewContext = view.getTimelineScheduledViewContext();

					Map<String, Object> timeLineViewProps = getTimeLineViewProps(viewProp);
					FacilioUtil.throwIllegalArgumentException(MapUtils.isEmpty(timeLineViewProps), "Timeline View props is not defined");
					validateTimelineScheduledView(timelineScheduledViewContext);

					oldCustomizationId = TimelineViewUtil.getCustomizationIdFromViewId(view.getId());
					if (oldCustomizationId > 0) {
						RecordCustomizationAPI.deleteCustomization(oldCustomizationId);
					}

					if ((timelineScheduledViewContext.getRecordCustomization() != null)) {
						long recordCustomizationId = RecordCustomizationAPI.addOrUpdateRecordCustomizationValues((timelineScheduledViewContext.getRecordCustomization()));
						timeLineViewProps.put("recordCustomizationId", recordCustomizationId);
					}

					criteria = (timelineScheduledViewContext.getGroupCriteria());
					if (criteria != null) {
						long criteriaId = CriteriaAPI.addCriteria(criteria, AccountUtil.getCurrentOrg().getOrgId());
						timeLineViewProps.put("groupCriteriaId",criteriaId);
					}
					else {
						timeLineViewProps.put("groupCriteriaId", -99);
					}

					FacilioModule timelineViewModule = ModuleFactory.getTimelineViewModule();
					List<FacilioField> timelineViewFields = FieldFactory.getTimelineViewFields(timelineViewModule);

					TimelineScheduledViewContext timelineView = TimelineViewUtil.getTimelineView(view.getId());
					isNew = (timelineView == null);

					if (isNew) {
						addDependentTables(timelineViewModule, timelineViewFields, timeLineViewProps);
					} else {
						updateDependentTables(timelineViewModule, timelineViewFields, timeLineViewProps);
						if (oldView != null && (timelineScheduledViewContext.getGroupCriteriaId() > 0)) {
							CriteriaAPI.deleteCriteria(timelineScheduledViewContext.getGroupCriteriaId());
						}
					}

				}

//				if (view.isListView()){
//					List<ViewField> columns = view.getFields();
//					FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(columns), "View Columns cannot be empty");
//				}

				break;
		}
	}

	private static void addGroupByChildModuleIdInViewProp(FacilioView view, Map<String, Object> viewProp) {
		long groupByChildModuleId = ((TimelineViewContext) view).getGroupByChildModuleId();
		viewProp.put("groupByChildModuleId", groupByChildModuleId > 0? groupByChildModuleId : -99);
	}

	private static Map<String, Object> getTimeLineViewProps(Map<String, Object> viewProp) {
		Map<String, Object> timelineViewContext = (Map<String, Object>) viewProp.get("timelineScheduledViewContext");
		if (MapUtils.isNotEmpty(timelineViewContext)) {
			Map<String, Object> timelineViewProps = new HashMap<>(timelineViewContext);
			timelineViewProps.put("orgId", viewProp.get("orgId"));
			timelineViewProps.put("id", viewProp.get("id"));
			return timelineViewProps;
		}
		return null;
	}

	public static Map<String, Object> getCalendarViewProps(Map<String, Object> viewProps) throws Exception {
		Map<String,Object> calendarViewContext = (Map<String, Object>) viewProps.get("calendarViewContext");
		if (MapUtils.isNotEmpty(calendarViewContext)) {
			Map<String, Object> calendarViewProps = new HashMap<>(calendarViewContext);
			calendarViewProps.put("orgId", viewProps.get("orgId"));
			calendarViewProps.put("id", viewProps.get("id"));
			if (calendarViewContext.containsKey("recordCustomization") && (calendarViewContext.get("recordCustomization") != null)) {
				RecordCustomizationContext recordCustomizationContext = FieldUtil.getAsBeanFromMap((Map<String, Object>) calendarViewContext.get("recordCustomization"), RecordCustomizationContext.class);
				long recordCustomizationId = RecordCustomizationAPI.addOrUpdateRecordCustomizationValues(recordCustomizationContext);
				calendarViewProps.put("recordCustomizationId", recordCustomizationId);
			}
			return calendarViewProps;
		}
		return null;
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
	private static void validateTimelineScheduledView(TimelineScheduledViewContext view) throws Exception
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

	public static void validateCalendarView(CalendarViewContext view) throws Exception {
		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		FacilioField startDateField = moduleBean.getField(view.getStartDateFieldId());
		checkFieldType(startDateField, Arrays.asList(FieldType.DATE,FieldType.DATE_TIME));
		FacilioUtil.throwIllegalArgumentException(startDateField == null, "Start Date Field cannot be null");

		if (view.getEndDateFieldId() > 0) {
			if(view.getStartDateFieldId() == view.getEndDateFieldId()){
				throw new Exception("Start Date field and End Date field cannot be same");
			}
			FacilioField endDateField = moduleBean.getField(view.getEndDateFieldId());
			checkFieldType(endDateField,Arrays.asList(FieldType.DATE,FieldType.DATE_TIME));
		}
	}
	
	public static long updateView(long viewId, FacilioView view) throws Exception {
		try {
			FacilioView oldView = getView(viewId);

			Criteria criteria = view.getCriteria();
			long criteriaId = -99;
			if(criteria != null) {
				criteriaId = CriteriaAPI.addCriteria(criteria, AccountUtil.getCurrentOrg().getId());
			}
			// update criteria only for customViews and timeline views
			if(!oldView.isDefault() || (view.getTypeEnum() != null && view.getTypeEnum().equals(ViewType.TIMELINE))) {
				view.setCriteriaId(criteriaId);
			}
			User currentUser = AccountUtil.getCurrentUser();
			view.setSysModifiedTime(System.currentTimeMillis());
			view.setSysModifiedBy(currentUser.getId());

			if(view.getStatus() == null){
				view.setStatus(true);
			}

			Map<String, Object> viewProp = FieldUtil.getAsProperties(view);
			FacilioModule viewModule = ModuleFactory.getViewsModule();
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(viewModule.getTableName())
															.fields(FieldFactory.getViewFields())
															.andCondition(CriteriaAPI.getIdCondition(viewId, viewModule));
			
			int count = updateBuilder.update(viewProp);
			
			addOrUpdateExtendedViewDetails(view, viewProp, false, oldView);
			// delete old criteria only for customViews or timeline views
			if (oldView.getCriteriaId() != -1 && (!oldView.isDefault() || (view.getTypeEnum() != null && view.getTypeEnum().equals(ViewType.TIMELINE)))) {
				CriteriaAPI.deleteCriteria(oldView.getCriteriaId());
			}

			if (view.getFields() != null) {
			customizeViewColumns(view.getId(), view.getFields());
			}

			addViewSharing(view);
			addViewGroupSharing(view.getGroupId());
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
			List<ViewField> oldViewColumns = getViewColumns(viewId, false);
			Map<Long, String> fieldIdVsCustomization = new HashMap<>();
			Map<String, String> displayNameVsCustomization = new HashMap<>();
			if (CollectionUtils.isNotEmpty(oldViewColumns)) {
				for (ViewField viewField : oldViewColumns) {
					if (StringUtils.isNotEmpty(viewField.getCustomization())) {
						if (viewField.getFieldId() > 0) {
							fieldIdVsCustomization.put(viewField.getFieldId(), viewField.getCustomization());
						} else if (StringUtils.isNotEmpty(viewField.getColumnDisplayName())) {
							displayNameVsCustomization.put(viewField.getColumnDisplayName(), viewField.getCustomization());
						}
					}
				}
			}

			deleteViewColumns(viewId);
			
			List<Map<String, Object>> props = new ArrayList<>();
			for(ViewField field: columns)
			{
				FacilioField fieldDetails = field.getField();
				if (field.getFieldId() == -1 && StringUtils.isEmpty(field.getFieldName()) && (fieldDetails.getFieldId() == -1 && StringUtils.isEmpty(fieldDetails.getName()))) {
					throw new IllegalArgumentException("column field is required");
				}
				// add viewField customizations obtained from oldViewField
				if (StringUtils.isEmpty(field.getCustomization())) {
					if (field.getFieldId() > 0 && fieldIdVsCustomization.containsKey(field.getFieldId())) {
						field.setCustomization(fieldIdVsCustomization.get(field.getFieldId()));
					} else if (StringUtils.isNotEmpty(field.getColumnDisplayName()) && displayNameVsCustomization.containsKey(field.getColumnDisplayName())) {
						field.setCustomization(displayNameVsCustomization.get(field.getColumnDisplayName()));
					}
				}
				field.setVersion(fieldDetails != null ? fieldDetails.getVersion() : null);
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
		int count = builder.delete();
		addViewGroupSharing(viewDetail.getGroupId());
		return count;
	}

	public static void deleteFacilioViews(Collection<Long> viewIds) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getViewsModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(viewIds, ModuleFactory.getViewsModule()));
		builder.delete();
	}

	public static int deleteViewSortColumns(long viewId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getViewSortColumnsModule().getTableName())
				.andCustomWhere("ORGID = ? AND VIEWID = ?", AccountUtil.getCurrentOrg().getId(), viewId);
		return builder.delete();
	}

	public static List<ViewField> getViewColumns(long viewId) throws Exception {
		return getViewColumns(viewId, true);
	}
	
	public static List<ViewField> getViewColumns(long viewId, boolean setFieldProps) throws Exception {
		List<ViewField> columns = new ArrayList<>();
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(ModuleFactory.getViewColumnsModule().getTableName())
													.select(FieldFactory.getViewColumnFields())
													.andCustomWhere(CriteriaAPI.getCurrentBuildVersionCriteria()+" AND VIEWID = ?", viewId)
													.orderBy("ID");
			
			List<Map<String, Object>> props = builder.get();
			
			for(Map<String, Object> prop : props) {
				columns.add(FieldUtil.getAsBeanFromMap(prop, ViewField.class));
			}

			if (setFieldProps) {
				setViewFieldsProp(columns, null);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
			throw e;
		}
		return columns;
	}

	public static void appendFilterCriteriaWithViewCriteria(FacilioView view, Criteria filterCriteria) throws Exception {
		if(view != null && filterCriteria != null) {
			FacilioUtil.throwIllegalArgumentException(view.isDefault(), "SystemView's criteria can't be updated");

			Criteria viewCriteria = view.getCriteria();
			if (viewCriteria != null) {
				filterCriteria.andCriteria(viewCriteria);
			}

			long criteriaId = CriteriaAPI.addCriteria(filterCriteria);
			if(criteriaId > -1) {
				Map<String, Object> props = new HashMap<>();
				props.put("id", view.getId());
				props.put("criteriaId", criteriaId);

				FacilioModule viewModule = ModuleFactory.getViewsModule();
				FacilioField crieriaField = FieldFactory.getNumberField("criteriaId", "CRITERIAID", viewModule);

				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(viewModule.getTableName())
						.fields(Arrays.asList(crieriaField))
						.andCondition(CriteriaAPI.getIdCondition(view.getId(), viewModule));
				updateBuilder.update(props);
			}
		}
	}

	public static void updateViewColumnCustomization(@NonNull List<ViewField> viewFields) throws Exception {
		if(CollectionUtils.isNotEmpty(viewFields)) {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getViewColumnFields());

			List<GenericUpdateRecordBuilder.BatchUpdateContext> updateBatch = new ArrayList<>();
			for (ViewField viewField : viewFields) {
				if(viewField != null && viewField.getId() > 0) {
					GenericUpdateRecordBuilder.BatchUpdateContext updateContext = new GenericUpdateRecordBuilder.BatchUpdateContext();
					updateContext.addUpdateValue("customization", viewField.getCustomization());

					updateContext.addWhereValue("id", viewField.getId());
					updateBatch.add(updateContext);
				}
			}

			List<FacilioField> whereField = new ArrayList<>();
			whereField.add(fieldMap.get("id"));

			List<FacilioField> updateFields = new ArrayList<>();
			updateFields.add(fieldMap.get("customization"));

			GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
					.fields(updateFields)
					.table(ModuleFactory.getViewColumnsModule().getTableName());

			builder.batchUpdate(whereField, updateBatch);
		}
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
		if(!FacilioUtil.isEmptyOrNull(viewSharing)) {
			List<FacilioField> viewSharingFields = FieldFactory.getSharingFields(ModuleFactory.getViewSharingModule());
			SharingContext<SingleSharingContext> existingSharing = SharingAPI.getSharing(view.getId(), ModuleFactory.getViewSharingModule(), SingleSharingContext.class, viewSharingFields);

			viewSharing.forEach((sharingContext) -> {
				if (sharingContext.getId() <= 0) {
					sharingContext.setSharedBy(AccountUtil.getCurrentUser().getOuid());
				} else {
					existingSharing.stream().filter(share -> share.getId() == sharingContext.getId()).findFirst()
							.ifPresent(existingShare -> {
								long sharedBy = existingShare.getSharedBy() > 0 ? existingShare.getSharedBy() : AccountUtil.getCurrentUser().getOuid();
								sharingContext.setSharedBy(sharedBy);
							});
				}
			});
		}

		SharingAPI.deleteSharingForParent(Collections.singletonList(view.getId()), ModuleFactory.getViewSharingModule());

		if(viewSharing == null) {
			viewSharing = new SharingContext<>();
		}

		viewSharing.forEach(singleSharingContext -> {
			if (Objects.equals(singleSharingContext.getTypeEnum(), SharingType.USER)) {
				long userId = singleSharingContext.getUserId();
				if (userId <= 0) {
					singleSharingContext.setUserId(AccountUtil.getCurrentUser().getId());
				}
			}
		});

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


	public static List<FacilioView> getViewsForGroupId(long viewGroupId, boolean fromBuilder) throws Exception {
		Criteria criteria = new Criteria();
		List<FacilioField> viewFields = FieldFactory.getViewFields();
		Map<String, FacilioField> viewFieldsAsMap = FieldFactory.getAsMap(viewFields);
		FacilioField statusField = viewFieldsAsMap.get("status");
		if(!fromBuilder){
			Criteria statusFilterCriteria = new Criteria();
			statusFilterCriteria.addAndCondition(CriteriaAPI.getCondition(statusField, String.valueOf(true), BooleanOperators.IS));
			statusFilterCriteria.addOrCondition(CriteriaAPI.getCondition(statusField, CommonOperators.IS_EMPTY));
			criteria.andCriteria(statusFilterCriteria);
		}
		criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(ModuleFactory.getViewsModule()), StringUtils.join(LoadViewCommand.HIDDEN_VIEW_NAMES, ','), StringOperators.ISN_T));
		List<FacilioField> fields = FieldFactory.getViewFields();
		return getViewsForGroupIds(Collections.singletonList(viewGroupId), fields, criteria);
	}
	public static List<FacilioView> getViewsForGroupIds(List<Long> viewGroupIds, List<FacilioField> viewFields, Criteria criteria) throws Exception{
		FacilioModule module = ModuleFactory.getViewsModule();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(viewFields);
		FacilioField viewGroupIdField = fieldsMap.get("groupId");
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(viewFields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(viewGroupIdField, StringUtils.join(viewGroupIds, ','), NumberOperators.EQUALS))
				.orderBy("SEQUENCE_NUMBER");
		if(criteria != null && !criteria.isEmpty()){
			builder.andCriteria(criteria);
		}
		List<Map<String, Object>> viewProps = builder.get();
		return getAllViewDetails(viewProps, module.getOrgId(), true);
	}

	public static void addViewGroupSharing(long viewGroupId) throws Exception{
		if(viewGroupId <= 0) {
			return;
		}

		Criteria viewIdsFilterCriteria = new Criteria();
		viewIdsFilterCriteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(ModuleFactory.getViewsModule()), StringUtils.join(LoadViewCommand.HIDDEN_VIEW_NAMES, ','), StringOperators.ISN_T));

		Criteria statusFilterCriteria = new Criteria();
		statusFilterCriteria.addAndCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(true), BooleanOperators.IS));
		statusFilterCriteria.addOrCondition(CriteriaAPI.getCondition("STATUS", "status", CommonOperators.IS_EMPTY));

		viewIdsFilterCriteria.andCriteria(statusFilterCriteria);

		List<Long> viewIds = getViewIdsVsGroup(viewGroupId, viewIdsFilterCriteria);
		SharingContext<SingleSharingContext> allViewSharing = new SharingContext<>();
		Map<Long, SharingContext<SingleSharingContext>> sharing = null;
		boolean deleteAll = viewIds.isEmpty();
		if(!viewIds.isEmpty()){
			sharing = SharingAPI.getSharing(viewIds, ModuleFactory.getViewSharingModule(), SingleSharingContext.class, FieldFactory.getSharingFields(ModuleFactory.getViewSharingModule()));
			if(sharing.keySet().size() <= 0 || sharing.keySet().size() != viewIds.size()) {
				deleteAll = true;
			}
		}
		if(deleteAll) {
			SharingAPI.deleteSharingForParent(Collections.singletonList(viewGroupId), ModuleFactory.getViewGroupSharingModule());
			return;
		}
		for(SharingContext<SingleSharingContext> sharingContexts : sharing.values()) {
			allViewSharing.addAll(sharingContexts);
		}
		SharingContext<SingleSharingContext> viewGroupSharing = SharingAPI.getSharing(viewGroupId, ModuleFactory.getViewGroupSharingModule(),
				SingleSharingContext.class, FieldFactory.getViewGroupSharingFields(ModuleFactory.getViewGroupSharingModule()));
		if(viewGroupSharing == null) {
			viewGroupSharing = new SharingContext<>();
		}
		Map<String, Set<Long>> allViewSharingTypeMap = getSharingTypeMap(allViewSharing);
		Map<String, Set<Long>> viewGroupSharingTypeMap = getSharingTypeMap(viewGroupSharing);
		Map<String, Map<String, Set<Long>>> sharingDifferenceValues = getSharingDifferenceValues(allViewSharingTypeMap, viewGroupSharingTypeMap, "userIds", "roleIds", "groupIds");
		Map<String, Set<Long>> addSharingValues = sharingDifferenceValues.get("addSharingValues");
		Map<String, Set<Long>> deleteSharingValues = sharingDifferenceValues.get("deleteSharingValues");
		SharingContext<SingleSharingContext> addViewGroupSharing = getSharingContextFromSharingValues(addSharingValues);
		if(!addViewGroupSharing.isEmpty()) {
			SharingAPI.addSharing(addViewGroupSharing, FieldFactory.getViewGroupSharingFields(ModuleFactory.getViewGroupSharingModule()), viewGroupId, ModuleFactory.getViewGroupSharingModule());
		}
		boolean hasValues = deleteSharingValues.values()
				.stream()
				.anyMatch(set -> !set.isEmpty());
		if(hasValues) {
			deleteGroupSharing(deleteSharingValues, viewGroupId);
		}
	}
	private static Map<String, Map<String, Set<Long>>> getSharingDifferenceValues(Map<String, Set<Long>> allViewSharingTypeMap, Map<String, Set<Long>> viewGroupSharingTypeMap, String... keys) {
		Map<String, Map<String, Set<Long>>> sharingValues = new HashMap<>();
		Map<String, Set<Long>> addSharingValues = new HashMap<>();
		Map<String, Set<Long>> deleteSharingValues = new HashMap<>();
		for (String key : keys) {
			Set<Long> leftSet = allViewSharingTypeMap.get(key);
			Set<Long> rightSet = viewGroupSharingTypeMap.get(key);
			Set<Long> difference = new HashSet<>(leftSet);
			difference.removeAll(rightSet);
			addSharingValues.put(key, difference);
			difference = new HashSet<>(rightSet);
			difference.removeAll(leftSet);
			deleteSharingValues.put(key, difference);
		}
		sharingValues.put("addSharingValues", addSharingValues);
		sharingValues.put("deleteSharingValues", deleteSharingValues);
		return sharingValues;
	}
	private static void deleteGroupSharing(Map<String, Set<Long>> groupSharingValues, long viewGroupId) throws Exception{
		Set<Long> userIds = groupSharingValues.get("userIds");
		Set<Long> roleIds = groupSharingValues.get("roleIds");
		Set<Long> groupIds = groupSharingValues.get("groupIds");
		Criteria criteria = new Criteria();
		criteria.addOrCondition(CriteriaAPI.getCondition("ORG_USERID", "userId", StringUtils.join(userIds, ','), NumberOperators.EQUALS));
		criteria.addOrCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", StringUtils.join(roleIds, ','), NumberOperators.EQUALS));
		criteria.addOrCondition(CriteriaAPI.getCondition("GROUP_ID", "groupId", StringUtils.join(groupIds, ','), NumberOperators.EQUALS));
		if(!CollectionUtils.isEmpty(userIds) || !CollectionUtils.isEmpty(roleIds) || !CollectionUtils.isEmpty(groupIds)) {
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getViewGroupSharingModule().getTableName())
					.andCriteria(criteria)
					.andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId",String.valueOf(viewGroupId), NumberOperators.EQUALS));
			deleteBuilder.delete();
		}
	}
	private static SharingContext<SingleSharingContext> getSharingContextFromSharingValues(Map<String, Set<Long>> sharingValues) {
		SharingContext<SingleSharingContext> sharingContext = new SharingContext<>();
		Set<Long> userIds = sharingValues.get("userIds");
		Set<Long> roleIds = sharingValues.get("roleIds");
		Set<Long> groupIds = sharingValues.get("groupIds");
		if (CollectionUtils.isNotEmpty(userIds)) {
			sharingContext.addAll(generateSharingContextObjects(userIds, SharingType.USER));
		}
		if (CollectionUtils.isNotEmpty(roleIds)) {
			sharingContext.addAll(generateSharingContextObjects(roleIds, SharingType.ROLE));
		}
		if (CollectionUtils.isNotEmpty(groupIds)) {
			sharingContext.addAll(generateSharingContextObjects(groupIds, SharingType.GROUP));
		}
		return sharingContext;
	}

	public static List<Long> getViewIdsVsGroup(Long viewGroupId) throws Exception {
		return getViewIdsVsGroup(viewGroupId, null);
	}
	public static List<Long> getViewIdsVsGroup(Long viewGroupId, Criteria andCriteria) throws Exception {
		List<Long> viewIds;
		GenericSelectRecordBuilder viewsVsGroupProps= new GenericSelectRecordBuilder()
				.select(Collections.singleton(FieldFactory.getIdField()))
				.table(ModuleFactory.getViewsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("GROUPID","groupId",String.valueOf(viewGroupId),NumberOperators.EQUALS));
		if(andCriteria != null && !andCriteria.isEmpty()){
			viewsVsGroupProps.andCriteria(andCriteria);
		}
		List<Map<String, Object>> props = viewsVsGroupProps.get();
		viewIds = props.stream().map(prop -> (Long) prop.get("id")).collect(Collectors.toList());
		return viewIds;
	}
	public static Map<String, Set<Long>> getSharingTypeMap(SharingContext<SingleSharingContext> sharingContexts) {
		Set<Long> userIds = new HashSet<>();
		Set<Long> roleIds = new HashSet<>();
		Set<Long> groupIds = new HashSet<>();
		for (SingleSharingContext sharingContext : sharingContexts) {
			switch (sharingContext.getTypeEnum()) {
				case USER:
					if (sharingContext.getUserId() > 0) {
						userIds.add(sharingContext.getUserId());
					}
					break;
				case ROLE:
					if (sharingContext.getRoleId() > 0) {
						roleIds.add(sharingContext.getRoleId());
					}
					break;
				case GROUP:
					if (sharingContext.getGroupId() > 0) {
						groupIds.add(sharingContext.getGroupId());
					}
					break;
				default:
					break;
			}
		}
		Map<String, Set<Long>> sharingTypeMap = new HashMap<>();
		sharingTypeMap.put("userIds", userIds);
		sharingTypeMap.put("roleIds", roleIds);
		sharingTypeMap.put("groupIds", groupIds);
		return sharingTypeMap;
	}
	public static SharingContext<SingleSharingContext> generateSharingContextObjects(Collection<Long> ids, SharingType sharingType) {
		SharingContext<SingleSharingContext> sharingContexts = new SharingContext<>();
		for (Long id : ids) {
			SingleSharingContext sharingContext = new SingleSharingContext();
			sharingContext.setType(sharingType);
			switch (sharingType) {
				case USER:
					sharingContext.setUserId(id);
					break;
				case ROLE:
					sharingContext.setRoleId(id);
					break;
				case GROUP:
					sharingContext.setGroupId(id);
					break;
				default:
					break;
			}
			sharingContexts.add(sharingContext);
		}
		return sharingContexts;
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

	public static ViewGroups getViewGroup(String name, String moduleName, long moduleId, long appId) throws Exception{
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

		if (StringUtils.isNotEmpty(moduleName)) {
			builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleName"), moduleName, StringOperators.IS));
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

		FieldUtil.setSiteIdFieldForModuleFields(allFields, module.getName());

		FacilioContext fieldsContext = FieldsConfigChainUtil.fetchFieldList(module.getName(), AccountUtil.getCurrentApp().getId(), FieldListType.VIEW_FIELDS, null);
		List<ModuleViewField>  moduleViewFields = (List<ModuleViewField>) fieldsContext.get(FacilioConstants.ContextNames.FIELDS);
		Map<String, FacilioField> allFieldsAsMap = FieldFactory.getAsMap(allFields);

		for (ModuleViewField moduleViewField : moduleViewFields) {
			FacilioField field = allFieldsAsMap.get(moduleViewField.getName());
			if (field != null) {
				ViewField viewField = new ViewField(field.getName(), field.getDisplayName());
				viewField.setFieldName(viewField.getName());
				viewField.setFieldId(field.getFieldId());
				viewField.setField(field);
				columns.add(viewField);
			}
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
	public static List<ViewGroups> filterAccessibleViewGroups(List<ViewGroups> viewGroups, boolean fromBuilder, Boolean isPrivilegedAccess) throws Exception {
		if(fromBuilder || isPrivilegedAccess){
			return viewGroups;
		}
		List<Long> viewGroupIds = viewGroups.stream().map(ViewGroups::getId).collect(Collectors.toList());
		List<Long> accessibleIds = getSharedViewsOrViewGroupIds(viewGroupIds, ModuleFactory.getViewGroupSharingModule(), FieldFactory.getViewGroupSharingFields(ModuleFactory.getViewGroupSharingModule()));
		viewGroups = viewGroups.stream().filter(viewGroup -> accessibleIds.contains(viewGroup.getId())).collect(Collectors.toList());

		return viewGroups;
	}

	public static List<FacilioView> filterAccessibleViews(List<FacilioView> views, User currentUser, Boolean isPrivilegedAccess) throws Exception {
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		Long currentUserId = currentUser.getId();
		Long superAdminUserId = AccountUtil.getOrgBean().getSuperAdmin(orgId).getOuid();

		views = views.stream().filter(facilioView -> !facilioView.isHidden()).collect(Collectors.toList());
		if(!isPrivilegedAccess) {
			List<Long> viewIds = views.stream().map(FacilioView::getId).collect(Collectors.toList());
			List<Long> accessibleIds = getSharedViewsOrViewGroupIds(viewIds, ModuleFactory.getViewSharingModule(), FieldFactory.getSharingFields(ModuleFactory.getViewSharingModule()));
			checkViewOwnerAccess(accessibleIds, views, superAdminUserId, currentUserId);
			views = views.stream().filter(view -> accessibleIds.contains(view.getId())).collect(Collectors.toList());
		}
		addEditableAccessToViews(views, isPrivilegedAccess, currentUserId, superAdminUserId);
		return views;
	}

	private static void checkViewOwnerAccess(List<Long> accessibleIds, List<FacilioView> views, Long superAdminUserId, Long currentUserId) {
		for(FacilioView view: views){
			Long ownerId = view.getOwnerId() != -1 ? view.getOwnerId() : superAdminUserId;
			if(ownerId.equals(currentUserId)){
				accessibleIds.add(view.getId());
			}
		}
	}

	public static List<Long> getSharedViewsOrViewGroupIds(List<Long> viewsOrViewGroupsIds, FacilioModule module, List<FacilioField> viewsOrViewGroupFields) throws Exception {
		List<Long> resultIds = new ArrayList<>();
		Map<Long, SharingContext<SingleSharingContext>> sharingMap = SharingAPI.getSharing(viewsOrViewGroupsIds, module, SingleSharingContext.class, viewsOrViewGroupFields);
		List<Long> viewGroupIdsWithPermission = SharingAPI.getAllowedParentIds(sharingMap, DelegationType.LIST_VIEWS);
		for(Long id: viewsOrViewGroupsIds) {
			if(!sharingMap.containsKey(id) || viewGroupIdsWithPermission.contains(id)){
				resultIds.add(id);
			}
		}
		return resultIds;
	}
	public static boolean isPrivilegedAccess(long currAppId, User currentUser, long currUserAppId) throws Exception {
		Long currentUserRoleId = currentUser.getRoleId();
		Boolean isSuperAdmin = currentUser.isSuperAdmin();
		// Admin/CAFMAdmin Role has equal privileges as SuperAdmin Role
		Criteria roleNameCriteria = new Criteria();
		String[] roleNames = { FacilioConstants.DefaultRoleNames.ADMIN, FacilioConstants.DefaultRoleNames.MAINTENANCE_ADMIN, FacilioConstants.DefaultRoleNames.CAFM_ADMIN };
		roleNameCriteria.addAndCondition(CriteriaAPI.getCondition("NAME", "name", org.apache.commons.lang.StringUtils.join(roleNames, ","), StringOperators.IS));

		List<Role> adminRoles = AccountUtil.getRoleBean().getRoles(roleNameCriteria);

		List<Long> adminRoleIds = CollectionUtils.isNotEmpty(adminRoles) ? adminRoles.stream().map(Role::getId).collect(Collectors.toList()) : new ArrayList<>();

		boolean isPrivilegedRole = currentUser.getRole().isPrevileged() && (currAppId == currUserAppId);
		boolean isAdmin = adminRoleIds.contains(currentUserRoleId);
		return isSuperAdmin || isAdmin || isPrivilegedRole;
	}

	public static void addEditableAccessToViews(List<FacilioView> views, Boolean isPrivilegedAccess, Long currentUserId, Long superAdminUserId) {
		for (FacilioView view : views) {
			boolean isLocked = view.getIsLocked() != null ? view.getIsLocked() : false;
			Long ownerId = view.getOwnerId() != -1 ? view.getOwnerId() : superAdminUserId;
			view.setEditable(isPrivilegedAccess || !isLocked || ownerId.equals(currentUserId));
		}
	}
	public static void setViewsForViewGroup(List<FacilioView> views, List<ViewGroups> viewGroups, long groupId) {
		ViewGroups matchingViewGroup = viewGroups.stream()
				.filter(viewGroup -> viewGroup.getId() == groupId)
				.findFirst()
				.orElse(null);
		if (matchingViewGroup != null) {
			long matchingViewGroupId = matchingViewGroup.getId();
			List<FacilioView> groupBasedViews = views.stream()
					.filter(view -> matchingViewGroupId > 0 && view.getGroupId() == matchingViewGroupId)
					.sorted(Comparator.comparing(FacilioView::getSequenceNumber))
					.collect(Collectors.toList());
			matchingViewGroup.setViews(groupBasedViews);
		}
	}
}
