package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.LookupOperator;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.events.constants.EventConstants;
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
	
	public static void addViewFields(long viewId, JSONArray fields) throws Exception {
		try {
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(ModuleFactory.getViewColumnsModule().getTableName())
															.fields(FieldFactory.getViewColumnFields());
			
			Iterator ids = fields.iterator();
			while(ids.hasNext())
			{
				Long id = (Long)ids.next();
				Map<String, Object> prop = new HashMap<>();
				prop.put("viewId", viewId);
				prop.put("fieldId", id);
				insertBuilder.addRecord(prop);
			}
			
			insertBuilder.save();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}
}
