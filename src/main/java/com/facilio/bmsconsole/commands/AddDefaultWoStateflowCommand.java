package com.facilio.bmsconsole.commands;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext;
import com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.util.DBConf;
import com.facilio.db.util.SQLScriptRunner;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;

public class AddDefaultWoStateflowCommand extends FacilioCommand {

	private static org.apache.log4j.Logger log = LogManager.getLogger(AddDefaultWoStateflowCommand.class.getName());

	private static final File INSERT_STATEFLOW_SQL = new File(SQLScriptRunner.class.getClassLoader().getResource(FacilioUtil.normalizePath("conf/db/" + DBConf.getInstance().getDBName() + "/defaultWOStateflow.sql")).getFile());
	@Override
	public boolean executeCommand(Context context) throws Exception {

		long orgId = (long) context.get("orgId");
		long assignedStateId = -1;

		Map<String, String> paramValues = new HashMap<>(); 
		paramValues.put("orgId", String.valueOf(orgId));

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", orgId);
		FacilioModule module = modBean.getModule(ContextNames.WORK_ORDER);
		
		StateFlowRuleContext stateflowContext = StateFlowRulesAPI.getDefaultStateFlow(module);

		paramValues.put("stateflowId", String.valueOf(stateflowContext.getId()));

		List<WorkflowRuleContext> transitions = StateFlowRulesAPI.getAllStateTransitionList(stateflowContext);
		List<FacilioStatus> states = TicketAPI.getAllStatus(module, true);

		for (FacilioStatus state :states) {
			if(state.getStatus().equals("Assigned")) {
				assignedStateId = state.getId();
			}
			paramValues.put(state.getStatus(), String.valueOf(state.getId()));
		}
		
		for(WorkflowRuleContext t :transitions) {
			StateflowTransitionContext transition = (StateflowTransitionContext) t;

			if(transition.getName().equals("Close") && transition.getFromStateId() == assignedStateId) {
				paramValues.put("Close-1", String.valueOf(t.getId()));
			} else {
				paramValues.put(t.getName(), String.valueOf(t.getId()));	
			}
		}

		System.out.println(paramValues);
		SQLScriptRunner scriptRunner = new SQLScriptRunner(INSERT_STATEFLOW_SQL, true, paramValues, DBUtil.getDBSQLScriptRunnerMode());
		scriptRunner.runScript();
		
		return false;
	}

}
