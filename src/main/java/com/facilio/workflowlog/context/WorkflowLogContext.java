package com.facilio.workflowlog.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.bmsconsoleV3.context.inspection.InspectionCategoryContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionPriorityContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext.SourceType;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.v3.context.V3Context;
import com.facilio.workflowlog.context.WorkflowLogContext.WorkflowLogStatus;
import com.facilio.workflowlog.context.WorkflowLogContext.WorkflowLogType;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;
@Getter
@Setter
@Log4j
public class WorkflowLogContext extends V3Context {

	long recordId;
	long parentId;
	long workflowId;
	public WorkflowLogType logType;
	public WorkflowLogStatus status;
	String exception;
	long createdTime;
	String logValue;
	private Long recordModuleId;

	// for client purpose
	private String recordModuleName;
	private String workflowRuleName;

	public int getStatus() {
	    return status == null ? -1 :status.getStatusId();
	}
	public void setStatus(int status) {
		this.status = (status <=0 ? null : WorkflowLogStatus.typeMap.get(status));
	}
	
    public int getLogType() {
    	return logType == null ? -1 : logType.getTypeId();
	}
	public void setLogType(int type) {
		this.logType = (type <= 0 ? null: WorkflowLogType.typeMap.get(type));
	}
	public void setLogType(WorkflowLogType type) {
		this.logType = type;
	}
	
	public enum WorkflowLogType implements FacilioIntEnum{
		FORMULA(1),
		SCHEDULER(2),
		MODULE_RULE(3,RuleType.MODULE_RULE),
		STATE_RULE(4,RuleType.STATE_RULE),
		WORKFLOW_RULE_EVALUATION(5),
		Q_AND_A_RULE(6)
		;
		
		WorkflowLogType(int i) {
			this.typeId = i;
		}
		

		WorkflowLogType(int i, RuleType ruleType) {
			this.typeId=i;
			this.ruleType = ruleType;
		}


		public RuleType getRuleType() {
			return ruleType;
		}


		public void setRuleType(RuleType ruleType) {
			this.ruleType = ruleType;
		}


		private int typeId;
		private RuleType ruleType;



		public void setTypeId(int typeId) {
			this.typeId = typeId;
		}

		public int getTypeId() {
			return typeId;
		}
		public static final Map<Integer, WorkflowLogType> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, WorkflowLogType> initTypeMap() {
			Map<Integer, WorkflowLogType> typeMap = new HashMap<>();
			
			for(WorkflowLogType type : values()) {
				typeMap.put(type.getTypeId(), type);
			}
			return typeMap;
		}
		public static final Map<RuleType, WorkflowLogType> ruleTypeMap = Collections.unmodifiableMap(initRuleTypeMap());
		private static Map<RuleType, WorkflowLogType> initRuleTypeMap() {
			Map<RuleType, WorkflowLogType> RuleTypeMap = new HashMap<>();
			
			for(WorkflowLogType type : values()) {
				RuleTypeMap.put(type.getRuleType(), type);
			}
			return RuleTypeMap;
		}
	}	

	public enum WorkflowLogStatus implements FacilioIntEnum{
		SUCCESS(1),
		FAILURE(2),
		SYNTAX_ERROR(3)
		;
		
		WorkflowLogStatus(int i) {
			this.statusId = i;
		}
		
		private int statusId;

		public void setStatusId(int typeId) {
			this.statusId = typeId;
		}

		public int getStatusId() {
			return statusId;
		}
		public static final Map<Integer, WorkflowLogStatus> typeMap = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, WorkflowLogStatus> initTypeMap() {
			Map<Integer, WorkflowLogStatus> typeMap = new HashMap<>();
			
			for(WorkflowLogStatus status : values()) {
				typeMap.put(status.getStatusId(), status);
			}
			return typeMap;
		}
	}
}
