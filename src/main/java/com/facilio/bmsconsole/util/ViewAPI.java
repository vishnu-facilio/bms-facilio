package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.context.ViewSharingContext;
import com.facilio.bmsconsole.context.ViewSharingContext.SharingType;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class ViewAPI {
	
	public static List<FacilioView> getAllViews(long moduleId, long orgId) throws Exception {
		
		//List<FacilioView> views = new ArrayList<>();
		Map<Long, FacilioView> viewMap = new HashMap<>();
		List<Long> viewIds = new ArrayList<>();
		try 
		{
			FacilioModule module = ModuleFactory.getViewsModule();
			List<FacilioField> fields = FieldFactory.getViewFields();
			Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
			FacilioField moduleField = fieldsMap.get("moduleId");
			FacilioField isHiddenField = fieldsMap.get("isHidden");
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(fields)
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getCondition(moduleField, moduleId+"", NumberOperators.EQUALS))
													.andCondition(CriteriaAPI.getCondition(isHiddenField, String.valueOf(false) , BooleanOperators.IS))
													.orderBy("SEQUENCE_NUMBER");
			
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
			e.printStackTrace();
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
				return view;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			
			return (long) viewProp.get("id");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		
		List<Map<String, Object>> viewSharingProps = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getId();
		for(ViewSharingContext viewSharing : viewSharingList) {
			viewSharing.setOrgId(orgId);
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
			e.printStackTrace();
			throw e;
		}
	}
	
	public static int deleteViewColumns(long viewId) throws Exception {
		
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getViewColumnsModule().getTableName())
				.andCustomWhere("VIEWID = ?", viewId);
		
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
			e.printStackTrace();
			throw e;
		}
		return columns;
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
			if(viewField.getFieldId() == -1) {
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
