package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;

public class ExecutePMsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> pmIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		List<PreventiveMaintenance> pms = getPMs(pmIds);
		if(pms != null && !pms.isEmpty()) {
			Map<Long, Long> pmToWo = new HashMap<>();
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			List<Long> woIds = new ArrayList<>();
			for(PreventiveMaintenance pm : pms) {
				WorkOrderContext wo = bean.addWorkOrderFromPM(pm);
				if(wo != null) {
					woIds.add(wo.getId());
					pmToWo.put(pm.getId(), wo.getId());
				}
			}
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
			context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, woIds);
			context.put(FacilioConstants.ContextNames.PM_TO_WO, pmToWo);
		}
		return false;
	}
	
	private List<PreventiveMaintenance> getPMs (List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getPreventiveMaintenancetModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getPreventiveMaintenanceFields())
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(ids, module));

		List<Map<String, Object>> props = selectBuilder.get();
		
		if(props != null && !props.isEmpty()) {
			List<PreventiveMaintenance> pms = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				pms.add(FieldUtil.getAsBeanFromMap(prop, PreventiveMaintenance.class));
			}
		}
		return null;
	}

}
