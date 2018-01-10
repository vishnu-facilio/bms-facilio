package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class ViewAPI {
	
	public static List<FacilioView> getAllViews(long moduleId, long orgId) throws Exception {
		
		List<FacilioView> views = new ArrayList<>();
		try 
		{
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.select(FieldFactory.getViewFields())
													.table("Views")
													.andCustomWhere("ORGID = ? AND MODULEID = ?", orgId, moduleId);
			
			List<Map<String, Object>> viewProps = builder.get();
			for(Map<String, Object> viewProp : viewProps) 
			{
				views.add(FieldUtil.getAsBeanFromMap(viewProp, FacilioView.class));
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw e;
		}
		return views;
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
	
	public static void customizeViewColumns(long viewId, List<ViewField> columns) throws Exception {
		try {
			deleteViewColumns(viewId);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getViewColumnsModule().getTableName())
															.fields(FieldFactory.getViewColumnFields());
			
			for(ViewField field: columns)
			{
				field.setViewId(viewId);
				Map<String, Object> prop = FieldUtil.getAsProperties(field);
				insertBuilder.addRecord(prop);
			}
			
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
			String columnTableName = ModuleFactory.getViewColumnsModule().getTableName();
			String fieldsTableName = ModuleFactory.getFieldsModule().getTableName();
			List<FacilioField> fields = new ArrayList<>(FieldFactory.getSelectFieldFields());
			fields.addAll(FieldFactory.getViewColumnFields());
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.table(columnTableName)
													.select(fields)
													.innerJoin("Fields")
													.on(columnTableName+".FIELDID="+fieldsTableName+".FIELDID")
													.andCustomWhere("VIEWID = ?", viewId)
													.orderBy(columnTableName+".ID");
			
			List<Map<String, Object>> props = builder.get();
			
			for(Map<String, Object> prop : props) {
				columns.add(FieldUtil.getAsBeanFromMap(prop, ViewField.class));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return columns;
	}
	
	public static long checkAndAddView(String viewName, String moduleName) throws Exception {
		long viewId = -1;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		long moduleId = modBean.getModule(moduleName).getModuleId();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		FacilioView view = getView(viewName, moduleId, orgId);
		if(view == null) {
			view = ViewFactory.getView(moduleName + "-" +viewName);
			if(view != null) {
				if(view.getTypeEnum() == null){
					view.setType(ViewType.TABLE_LIST);
				}
				view.setDefault(true);
				view.setModuleId(moduleId);
				viewId = ViewAPI.addView(view, orgId);
			}
		} else {
			viewId = view.getId();
		}
		return viewId;
	}
}
