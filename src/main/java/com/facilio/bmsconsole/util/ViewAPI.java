package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.context.ViewSharingContext;
import com.facilio.bmsconsole.context.ViewSharingContext.SharingType;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.view.ColumnFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ViewAPI {

	private static Logger log = LogManager.getLogger(ViewAPI.class.getName());
	
	public static List<FacilioView> getAllViews(String moduleName) throws Exception {
		return getAllViews(-1, moduleName);
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
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.orderBy("SEQUENCE_NUMBER");
			
			if (moduleId != -1) {
				builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(moduleId), NumberOperators.EQUALS));
			}
			else {
				builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleName"), moduleName[0], StringOperators.IS));
			}
			
			List<Map<String, Object>> viewProps = builder.get();
			for(Map<String, Object> viewProp : viewProps) 
			{
				//views.add(FieldUtil.getAsBeanFromMap(viewProp, FacilioView.class));
				FacilioView view = FieldUtil.getAsBeanFromMap(viewProp, FacilioView.class);
				viewMap.put(view.getId(), view);
				viewIds.add(view.getId());
			}
		} 
		catch (Exception e) 
		{
			log.info("Exception occurred ", e);
			throw e;
		}
		return getFilteredViews(viewMap, viewIds);
	}
	
	public static List<FacilioView> getFilteredViews(Map<Long, FacilioView> viewMap, List<Long> viewIds) throws Exception {
		
		List<FacilioView> viewList = new ArrayList<FacilioView>();
		if (!viewMap.isEmpty()) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getViewSharingFields())
					.table(ModuleFactory.getViewSharingModule().getTableName())
					.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
					.andCondition(CriteriaAPI.getCondition("View_Sharing.VIEWID", "viewId", StringUtils.join(viewIds, ","), NumberOperators.EQUALS));
			
			List<Map<String, Object>> props = selectBuilder.get();
			
			if (props != null && !props.isEmpty()) {
				for (Map<String, Object> prop : props) {
					ViewSharingContext viewSharing = FieldUtil.getAsBeanFromMap(prop, ViewSharingContext.class);
					if (viewIds.contains(viewSharing.getViewId())) {
						viewIds.remove(viewSharing.getViewId());
					}
					if(!viewList.contains(viewMap.get(viewSharing.getViewId()))) {
						if (viewSharing.getSharingTypeEnum().equals(SharingType.USER) && viewSharing.getOrgUserId() == AccountUtil.getCurrentAccount().getUser().getOuid()) {
							viewList.add(viewMap.get(viewSharing.getViewId()));
						}
						else if (viewSharing.getSharingTypeEnum().equals(SharingType.ROLE) && viewSharing.getRoleId() == AccountUtil.getCurrentAccount().getUser().getRoleId()) {
							viewList.add(viewMap.get(viewSharing.getViewId()));
						}
						else if (viewSharing.getSharingTypeEnum().equals(SharingType.GROUP)) {
							List<Group> mygroups = AccountUtil.getGroupBean().getMyGroups(AccountUtil.getCurrentAccount().getUser().getOuid());
							for (Group group : mygroups) {
								if (viewSharing.getGroupId() == group.getGroupId() && !viewList.contains(viewMap.get(viewSharing.getViewId()))) {
									viewList.add(viewMap.get(viewSharing.getViewId()));
								}
							}
						}
					}
				}
				for (Long viewId : viewIds) {
					viewList.add(viewMap.get(viewId));
				}
			}
			else {
				viewList.addAll(viewMap.values());
			}
		}
		return viewList;
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
				view.setSortFields(getSortFields(view.getId()));
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
				view.setSortFields(getSortFields(view.getId()));
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
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
				view.setSortFields(getSortFields(view.getId()));
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
	
	public static void applyViewSharing(Long viewId, List<ViewSharingContext> viewSharingList) throws Exception {
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getViewSharingModule().getTableName())
				.andCustomWhere("VIEWID = ?", viewId);
		deleteBuilder.delete();
		
		if (viewSharingList == null) {
			return;
		}
		
		List<Map<String, Object>> viewSharingProps = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getId();
		for(ViewSharingContext viewSharing : viewSharingList) {
			viewSharing.setOrgId(orgId);
			viewSharing.setViewId(viewId);
			viewSharingProps.add(FieldUtil.getAsProperties(viewSharing));
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getViewSharingModule().getTableName())
					.fields(FieldFactory.getViewSharingFields())
					.addRecords(viewSharingProps);
		insertBuilder.save();
	}
	
	public static void customizeViewColumns(long viewId, List<ViewField> columns) throws Exception {
		try {
			deleteViewColumns(viewId);
			
			List<Map<String, Object>> props = new ArrayList<>();
			for(ViewField field: columns)
			{
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
	
	public static List<SortField> getSortFields(long viewID) throws Exception {
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
				FacilioField field = modBean.getField(sortField.getFieldId());
				sortField.setSortField(field);
				sortFields.add(sortField);
			}
		} catch (Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return sortFields;
	}
	
	public static long checkAndAddView(String viewName, String moduleName, List<ViewField> columns) throws Exception {
		long viewId = -1;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long moduleId = modBean.getModule(moduleName).getModuleId();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		FacilioView view = getView(viewName, moduleId, orgId);
		if(view == null) {
			view = ViewFactory.getView(moduleName, viewName);
			if(view != null) {
				if(view.getTypeEnum() == null){
					view.setType(ViewType.TABLE_LIST);
				}
				view.setDefault(true);
				view.setModuleId(moduleId);
				viewId = ViewAPI.addView(view, orgId);
				if (columns == null || columns.isEmpty()) {
					columns = view.getFields();
					for(ViewField column: columns) {
						Long fieldId = modBean.getField(column.getName(), moduleName).getFieldId();
						column.setFieldId(fieldId);
					}
				}
				List<SortField> sortFields = view.getSortFields();
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
			else {
				// For report-like view,  which wont be there in view db or view factory initially
				view = new FacilioView();
				view.setName(viewName);
				view.setModuleId(moduleId);
				view.setType(ViewType.TABLE_LIST);
				view.setDefault(false);
				view.setHidden(true);
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
	
	private static void setCriteriaValue(Criteria criteria) throws Exception {
		Map<Integer, Condition> conditions = criteria.getConditions();
		for(Map.Entry<Integer, Condition> entry : conditions.entrySet()) {
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
			}
		}
	}
}
