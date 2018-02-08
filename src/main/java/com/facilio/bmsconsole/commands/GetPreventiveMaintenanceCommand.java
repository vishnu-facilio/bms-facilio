package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.leed.context.PMTriggerContext;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetPreventiveMaintenanceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Boolean status = null;
		if(context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STATUS) != null)
		{
			status = (Boolean) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STATUS);
		}
		
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(module.getTableName())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.orderBy("Preventive_Maintenance.CREATION_TIME DESC")
															;
		
		if (status != null) {
			if (status) {
				selectRecordBuilder.andCustomWhere("(Preventive_Maintenance.STATUS = ? OR Preventive_Maintenance.STATUS IS NULL)", status);
			}
			else {
				selectRecordBuilder.andCustomWhere("Preventive_Maintenance.STATUS = ?", status);
			}
		}
		if(context.get(FacilioConstants.ContextNames.CV_NAME) != null)
		{
			String viewName = (String) context.get(FacilioConstants.ContextNames.CV_NAME);
			Map<Long,Object> woTypes = CommonCommandUtil.getPickList(FacilioConstants.ContextNames.TICKET_TYPE);
			Iterator<Long> woTypeIterator = woTypes.keySet().iterator();
			while(woTypeIterator.hasNext())
			{
				Long key = woTypeIterator.next();
				Object val = woTypes.get(key);
				if("preventive".equals(viewName) && "Preventive".equals(val))
				{
					selectRecordBuilder.andCustomWhere("Preventive_Maintenance.TYPE_ID = ?", key);
				}
				else if("corrective".equals(viewName) && "Corrective".equals(val))
				{
					selectRecordBuilder.andCustomWhere("Preventive_Maintenance.TYPE_ID = ?", key);
				}
				else if("rounds".equals(viewName) && "Rounds".equals(val))
				{
					selectRecordBuilder.andCustomWhere("Preventive_Maintenance.TYPE_ID = ?", key);
				}
				else if("breakdown".equals(viewName) && "Breakdown".equals(val))
				{
					selectRecordBuilder.andCustomWhere("Preventive_Maintenance.TYPE_ID = ?", key);
				}
				else if("compliance".equals(viewName) && "Compliance".equals(val))
				{
					selectRecordBuilder.andCustomWhere("Preventive_Maintenance.TYPE_ID = ?", key);
				}
			}
		}
		if(context.get(FacilioConstants.ContextNames.ASSET_ID) != null && (long) context.get(FacilioConstants.ContextNames.ASSET_ID) != -1)
		{
			selectRecordBuilder.andCustomWhere("Preventive_Maintenance.RESOURCE_ID = ?", (long) context.get(FacilioConstants.ContextNames.ASSET_ID));
		}
		if(context.get(FacilioConstants.ContextNames.SPACE_ID) != null && (long) context.get(FacilioConstants.ContextNames.SPACE_ID) != -1)
		{
			selectRecordBuilder.andCustomWhere("Preventive_Maintenance.RESOURCE_ID = ?", (long) context.get(FacilioConstants.ContextNames.SPACE_ID));
		}
		
		Criteria filterCriteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
		if (filterCriteria != null) {
			selectRecordBuilder.andCriteria(filterCriteria);
		}
		
		List<Long> resourceIds = new ArrayList<>();
		List<Map<String, Object>> pmProps = selectRecordBuilder.get();
		
		if(pmProps != null && !pmProps.isEmpty()) {
			List<PreventiveMaintenance> pms = new ArrayList<>();
			
			for(Map<String, Object> prop : pmProps) {
				pms.add(FieldUtil.getAsBeanFromMap(prop, PreventiveMaintenance.class));
			}
			
			Map<Long, List<PMTriggerContext>> pmTriggers = PreventiveMaintenanceAPI.getPMTriggers(pms);
			
			for(PreventiveMaintenance pm : pms) {
				pm.setTriggers(pmTriggers.get(pm.getId()));
				resourceIds.add(pm.getResourceId());
			}
			Map<Long, ResourceContext> resourceMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
			for(PreventiveMaintenance pm : pms) {
				pm.setResource(resourceMap.get(pm.getResourceId()));
			}
			
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
		}
		
		return false;
	}

}
