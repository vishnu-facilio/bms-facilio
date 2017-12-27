package com.facilio.bmsconsole.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PMRemainder;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.ActionAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.ActionContext;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.endpoints.SessionManager;

public class PMRemainderJob extends FacilioJob {
	private static final Logger logger = Logger.getLogger(SessionManager.class.getName());
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			long remainderId = jc.getJobId();
			List<FacilioField> fields = FieldFactory.getPMRemainderFields();
			fields.addAll(FieldFactory.getPreventiveMaintenanceFields());
			FacilioModule module = ModuleFactory.getPMRemainderModule();
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(fields)
															.table(module.getTableName())
															.innerJoin("Preventive_Maintenance")
															.on("PM_Remainders.PM_ID = Preventive_Maintenance.ID")
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getIdCondition(remainderId, module));
			
			List<Map<String, Object>> remainderProps = selectBuilder.get();
			if(remainderProps != null && !remainderProps.isEmpty()) {
				//Do not use ID field of both PM and remainder. It'll be wrong id
				PMRemainder remainder = FieldUtil.getAsBeanFromMap(remainderProps.get(0), PMRemainder.class);
				PreventiveMaintenance pm = FieldUtil.getAsBeanFromMap(remainderProps.get(0), PreventiveMaintenance.class);
				
				WorkOrderContext wo = null;
				switch(remainder.getTypeEnum()) {
					case BEFORE:
								long templateId = pm.getTemplateId();
								JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(AccountUtil.getCurrentOrg().getOrgId(), templateId);
								JSONObject content = template.getTemplate(null);
								wo = FieldUtil.getAsBeanFromJson((JSONObject) content.get(FacilioConstants.ContextNames.WORK_ORDER), WorkOrderContext.class);
								wo.setSourceType(TicketContext.SourceType.PREVENTIVE_MAINTENANCE);
								wo.setCreatedTime((jc.getExecutionTime()+remainder.getDuration())*1000);
								break;
					case AFTER:
								wo = getLatestWO(remainder.getPmId());
								break;
				}
				
				if(remainder.getTypeEnum() == PMRemainder.RemainderType.BEFORE || !isClosed(wo)) {
					ActionContext action = ActionAPI.getAction(remainder.getActionId());
					if(action != null) {
						Map<String, Object> placeHolders = new HashMap<>();
						CommonCommandUtil.appendModuleNameInKey(FacilioConstants.ContextNames.WORK_ORDER, FacilioConstants.ContextNames.WORK_ORDER, FieldUtil.getAsProperties(wo), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "pm", FieldUtil.getAsProperties(pm), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
						CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
						action.executeAction(placeHolders, null);
					}
				}
			}
		}
		catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private WorkOrderContext getLatestWO(long pmId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		SelectRecordsBuilder<WorkOrderContext> woBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
																.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
																.beanClass(WorkOrderContext.class)
																.innerJoin("PM_To_WO")
																.on("WorkOrders.ID = PM_To_WO.WO_ID")
																.andCustomWhere("PM_To_WO.PM_ID = ?", pmId)
																.orderBy("CREATED_TIME desc")
																;
		
		List<WorkOrderContext> woList = woBuilder.get();
		if(woList != null && !woList.isEmpty()) {
			WorkOrderContext wo = woList.get(0);
			return wo;
		}
		return null;
	}
	
	private boolean isClosed(WorkOrderContext wo) throws Exception {
		TicketStatusContext status = TicketAPI.getStatus("Closed");
		if(wo.getStatus().getId() == status.getId()) {
			return true;
		}
		return false;
	}
}
