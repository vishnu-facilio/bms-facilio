package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;

public class GetUpcomingPreventiveMaintenanceCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = FieldFactory.getPreventiveMaintenanceFields();
		fields.addAll(FieldFactory.getPMJobFields());
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table("Preventive_Maintenance")
															.innerJoin("Jobs")
															.on("Preventive_Maintenance.ID = Jobs.JOBID")
															.andCustomWhere("Preventive_Maintenance.ORGID = ? AND Jobs.JOBNAME = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), "PreventiveMaintenance");
		
		selectRecordBuilder.andCustomWhere("(Preventive_Maintenance.STATUS = ? OR Preventive_Maintenance.STATUS IS NULL)", true);
		

		String orderBy = (String) context.get(FacilioConstants.ContextNames.SORTING_QUERY);
		if (orderBy != null && !orderBy.isEmpty()) {
			selectRecordBuilder.orderBy(orderBy);
		}

		Integer limit = (Integer) context.get(FacilioConstants.Reports.LIMIT);
		if(limit!=null) {
			selectRecordBuilder.limit(limit);
		}
		
		List<Map<String, Object>> pmProps = selectRecordBuilder.get();
		
		if(pmProps != null && !pmProps.isEmpty()) 
		{
			long startTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME);
			long endTime = (Long) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME);
			
			List<Pair<Long, PreventiveMaintenance>> pms = new ArrayList<>();
			for(Map<String, Object> prop : pmProps) 
			{
				PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(prop, PreventiveMaintenance.class);
				List<Long> nextExecutionTimes = pm.getSchedule().nextExecutionTimes(startTime, endTime);
				for(Long nextExecutionTime : nextExecutionTimes)
				{
					Pair<Long, PreventiveMaintenance> pair = new ImmutablePair<Long, PreventiveMaintenance>(nextExecutionTime, pm);
					pms.add(pair);
				}
			}
			pms.sort(new Comparator<Pair<Long, PreventiveMaintenance>>() {

				@Override
				public int compare(Pair<Long, PreventiveMaintenance> o1, Pair<Long, PreventiveMaintenance> o2) {
					if (o1.getLeft() < o2.getLeft()) {
		                return -1;
		            } else if (o2.getLeft().equals(o1.getLeft())) {
		                return 0; 
		            } else {
		                return 1;
		            }
				}
			});
			
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
		}
		
		return false;
	}

}
