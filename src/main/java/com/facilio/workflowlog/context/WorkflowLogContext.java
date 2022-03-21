package com.facilio.workflowlog.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.tenant.TenantContext;
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
	
	public enum WorkflowLogType implements FacilioIntEnum{
		FORMULA(1);
		
		WorkflowLogType(int i) {
			this.typeId = i;
		}
		
		private int typeId;

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
	}	

	public enum WorkflowLogStatus implements FacilioIntEnum{
		SUCCESS(1),
		FAILURE(2);
		
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
