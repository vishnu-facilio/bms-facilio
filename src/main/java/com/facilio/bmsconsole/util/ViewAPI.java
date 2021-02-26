package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext.SharingType;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
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
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class ViewAPI {

	private static Logger log = LogManager.getLogger(ViewAPI.class.getName());
	
	public static List<FacilioView> getAllViews(String moduleName) throws Exception {
		return getAllViews(-1, moduleName);
	}
	
	public static long addViewGroup(ViewGroups viewGroup, long orgId, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module= modBean.getModule(moduleName);
		long moduleId = module.getModuleId();
		
		viewGroup.setModuleId(moduleId);
		viewGroup.setOrgId(orgId);
		viewGroup.setId(-1);
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
	
	public static List<ViewGroups> getAllGroups(long moduleId,long appId) throws Exception {
	
		List<ViewGroups> viewGroups = new ArrayList<>();
		if (moduleId > -1) {
			List<FacilioField> fields = FieldFactory.getViewGroupFields();
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
	
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(ModuleFactory.getViewGroupsModule().getTableName())
					.andCondition(CriteriaAPI.getCondition(fieldMap.get("moduleId"),String.valueOf(moduleId), NumberOperators.EQUALS));
	
			ApplicationContext app = appId <= 0 ? AccountUtil.getCurrentApp() : ApplicationApi.getApplicationForId(appId);
			if (app == null) {
				app = ApplicationApi.getApplicationForLinkName(ApplicationLinkNames.FACILIO_MAIN_APP);
			}
	
			Criteria appCriteria = new Criteria();
			appCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), String.valueOf(app.getId()), NumberOperators.EQUALS));
			if(app.getLinkName().equals(ApplicationLinkNames.FACILIO_MAIN_APP)) {
				appCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("appId"), CommonOperators.IS_EMPTY));
			}
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

	public static List<FacilioView> getAllViews(long moduleId, String... moduleName) throws Exception {
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
//													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.orderBy("SEQUENCE_NUMBER");
			
			if (moduleId != -1) {
				builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS));
			}
			else {
				builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleName"), moduleName[0], StringOperators.IS));
			}
			
			List<Map<String, Object>> viewProps = builder.get();
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
			
		} 
		catch (Exception e) 
		{
			log.info("Exception occurred ", e);
			throw e;
		}
		return new ArrayList<>(viewMap.values());
	}
	
	public static FacilioView getView(String name, String moduleName, long orgId) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(FieldFactory.getViewFields())
													.table("Views")
													.andCustomWhere("ORGID = ? AND MODULENAME = ? AND NAME = ?", orgId, moduleName, name);
			
			List<Map<String, Object>> viewProps = builder.get();
			if(viewProps != null && !viewProps.isEmpty()) {
				Map<String, Object> viewProp = viewProps.get(0);
				FacilioView view = FieldUtil.getAsBeanFromMap(viewProp, FacilioView.class);
				if(view.getCriteriaId() != -1) {
					Criteria criteria = CriteriaAPI.getCriteria(orgId, view.getCriteriaId());
					setCriteriaValue(criteria);
					view.setCriteria(criteria);
				}
				List<ViewField> columns = getViewColumns(view.getId());
				view.setFields(columns);
				view.setSortFields(getSortFields(view.getId(), moduleName));
				return view;
			}
			
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return null;
	}
	
	public static FacilioView getView(String name, long moduleId, long orgId) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(FieldFactory.getViewFields())
													.table("Views")
													.andCustomWhere("ORGID = ? AND MODULEID = ? AND NAME = ?", orgId, moduleId, name);
			
			List<Map<String, Object>> viewProps = builder.get();
			if(viewProps != null && !viewProps.isEmpty()) {
				Map<String, Object> viewProp = viewProps.get(0);
				FacilioView view = FieldUtil.getAsBeanFromMap(viewProp, FacilioView.class);
				if(view.getCriteriaId() != -1) {
					Criteria criteria = CriteriaAPI.getCriteria(orgId, view.getCriteriaId());
					setCriteriaValue(criteria);
					view.setCriteria(criteria);
				}
				List<ViewField> columns = getViewColumns(view.getId());
				view.setFields(columns);
				if (StringUtils.isEmpty(view.getModuleName()) && view.getModuleId() > 0) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					view.setModuleName(modBean.getModule(view.getModuleId()).getName());
				}
				view.setSortFields(getSortFields(view.getId(), view.getModuleName()));
				return view;
			}
			
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return null;
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
			if(viewProps != null && !viewProps.isEmpty()) {
				Map<String, Object> viewProp = viewProps.get(0);
				FacilioView view = FieldUtil.getAsBeanFromMap(viewProp, FacilioView.class);
				if(view.getCriteriaId() != -1) {
					Criteria criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), view.getCriteriaId());
					setCriteriaValue(criteria);
					view.setCriteria(criteria);
				}
				List<ViewField> columns = getViewColumns(view.getId());
				view.setFields(columns);
				if (StringUtils.isEmpty(view.getModuleName()) && view.getModuleId() > 0) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					view.setModuleName(modBean.getModule(view.getModuleId()).getName());
				}
				view.setSortFields(getSortFields(view.getId(), view.getModuleName()));
				return view;
			}
			
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return null;
	}
	
	public static long addView(FacilioView view, long orgId) throws Exception {
		view.setOrgId(orgId);
		view.setId(-1);
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
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
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
					defaultSortFileds.get(i).setOrgId(orgId);
				}
				
				customizeViewSortColumns((long) viewProp.get("id"), defaultSortFileds);
			}
			
			view.setId((long) viewProp.get("id"));
			addViewSharing(view);
			
			return (long) viewProp.get("id");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
			throw e;
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
	
	public static int deleteView(long id) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getViewsModule().getTableName())
				.andCustomWhere("Views.ID = ?", id);
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
	
	public static long checkAndAddView(String viewName, String moduleName, List<ViewField> columns, Long groupId) throws Exception {
		long viewId = -1;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module= modBean.getModule(moduleName);
		long moduleId = module.getModuleId();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		FacilioView view = getView(viewName, moduleId, orgId);
		if(view == null) {
			view = ViewFactory.getView(module, viewName, modBean);
			if(view != null) {
				if(view.getTypeEnum() == null){
					view.setType(ViewType.TABLE_LIST);
				}
				view.setDefault(true);
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
				view.setName(viewName);
				view.setModuleId(moduleId);
				view.setType(ViewType.TABLE_LIST);
				view.setDefault(false);
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

		//temp handling for apptype sharing for custom views..can be removed after supporting apptype sharing in ui
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long modId = -1;
		if(view.getId() > 0) {
			FacilioView existingView = ViewAPI.getView(view.getId());
			modId = existingView.getModuleId();
		}
		if(viewSharing == null) {
			viewSharing = new SharingContext<>();
		}
		

			FacilioView defaultView = ViewFactory.getView(modBean.getModule(modId > 0 ? modId : view.getModuleId()), view.getName(), modBean);
			if(defaultView == null) {
				SingleSharingContext defaultAppSharing = SharingAPI.getCurrentAppTypeSharingForCustomViews();
				if (defaultAppSharing != null) {
					viewSharing.add(defaultAppSharing);
				}
			}
			else {
				SharingContext<SingleSharingContext> appSharing = SharingAPI.getDefaultAppTypeSharing(defaultView);
				if(CollectionUtils.isNotEmpty(appSharing)) {
					viewSharing.addAll(appSharing);
				}
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
}
