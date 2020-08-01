package com.facilio.bmsconsole.util;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.DashboardFilterContext;
import com.facilio.bmsconsole.context.DashboardUserFilterContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;

public class DashboardFilterUtil {


	public static DashboardFilterContext getDashboardFilter(Long dashboardId,Long dashboardTabId) throws Exception
	{
		FacilioModule module=ModuleFactory.getDashboardFilterModule();
		Condition idCondition;
		
		if(dashboardId!=null&&dashboardId>0)
		{
			idCondition=CriteriaAPI.getCondition("DASHBOARD_ID","dashboardId", ""+dashboardId, NumberOperators.EQUALS);
		}
		else if(dashboardTabId!=null&&dashboardTabId>0)
		{
			idCondition=CriteriaAPI.getCondition("DASHBOARD_TAB_ID","dashboardTabId", ""+dashboardTabId, NumberOperators.EQUALS);
			
		}
		else {
			throw new IllegalArgumentException("No Dashboard or Tab Specified");
		}
		
		
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getDashboardFilterFields())
				.andCondition(idCondition);
		
				
		List<Map<String,Object>> records= builder.get();
				if(records!=null&& !records.isEmpty())
				{
					return FieldUtil.getAsBeanFromMap(records.get(0), DashboardFilterContext.class);
				}
				return null;
		
	}
	public static List<DashboardUserFilterContext> getDashboardUserFilters(Long dashboardFilterId) throws Exception
	{
		FacilioModule module=ModuleFactory.getDashboardUserFilterModule();
		GenericSelectRecordBuilder builder=new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(FieldFactory.getDashboardUserFilterFields())
				.andCondition(CriteriaAPI.getCondition("DASHBOARD_FILTER_ID", "dashboardFilterId", ""+dashboardFilterId,NumberOperators.EQUALS));
		
				
		List<Map<String,Object>> records= builder.get();
				if(records!=null&& !records.isEmpty())
				{
					List<DashboardUserFilterContext> dashboardUserFilters= FieldUtil.getAsBeanListFromMapList(records, DashboardUserFilterContext.class);
					for (DashboardUserFilterContext filter : dashboardUserFilters) {
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						filter.setField(modBean.getField(filter.getFieldId()));
						
					}
					 return dashboardUserFilters;
				}
				return null;
	}
	
	public static Long insertDashboardUserFilterRel(DashboardUserFilterContext dashboardUserFilterRel) throws Exception
	{
		GenericInsertRecordBuilder  builder=new  GenericInsertRecordBuilder()
				.table(ModuleFactory.getDashboardUserFilterModule().getTableName())
				.fields(FieldFactory.getDashboardUserFilterFields());
		
		
		return builder.insert(FieldUtil.getAsProperties(dashboardUserFilterRel));
	}
	
	public static void updateDashboardUserFilerRel(DashboardUserFilterContext dashboardUserFilterRel) throws Exception
	{
		FacilioModule module=ModuleFactory.getDashboardUserFilterModule();
		GenericUpdateRecordBuilder  builder=new  GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getDashboardUserFilterFields())
				.andCondition(CriteriaAPI.getIdCondition(dashboardUserFilterRel.getId(), module));
		
		
		builder.update(FieldUtil.getAsProperties(dashboardUserFilterRel));
	}

	public static void deleteDashboardUserFilterRel(List<Long> toRemove) throws Exception {
		FacilioModule module=ModuleFactory.getDashboardUserFilterModule();
		
		GenericDeleteRecordBuilder builder=new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(toRemove, module));
		builder.delete();
		
		
	}
	
}
