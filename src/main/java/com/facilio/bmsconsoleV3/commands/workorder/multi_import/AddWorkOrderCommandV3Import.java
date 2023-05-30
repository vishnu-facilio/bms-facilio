package com.facilio.bmsconsoleV3.commands.workorder.multi_import;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.TicketPriorityContext;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3TicketAPI;
import com.facilio.bmsconsoleV3.util.V3WorkOderAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;

import java.util.List;
import java.util.Map;

//TODO check bulk handling
public class AddWorkOrderCommandV3Import extends FacilioCommand {
    private static org.apache.log4j.Logger LOGGER = LogManager.getLogger(AddWorkOrderCommandV3Import.class.getName());
    public static final int DESCRIPTION_LENGTH = 2000;
    Map<Long,V3WorkOrderContext> parentWoIdVsContext = null;
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> insertRecordMap = ImportConstants.getInsertRecordMap(context);
        List<Pair<Long,ModuleBaseWithCustomFields>> wos = insertRecordMap.get(moduleName);
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);

        if (CollectionUtils.isEmpty(wos)) {
            return false;
        }

        TicketPriorityContext priority = V3TicketAPI.getPriority(AccountUtil.getCurrentOrg().getId(), "Low");

        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsWo : wos) {

            Long logId = logIdVsWo.getKey();

            try {
                V3WorkOrderContext workOrder = (V3WorkOrderContext) logIdVsWo.getValue();

                workOrder.setSourceType(V3TicketContext.SourceType.IMPORT.getIntVal());

                if(StringUtils.isNotEmpty(workOrder.getDescription())) {
                    workOrder.setDescription(workOrder.getDescription().trim());
                    if(workOrder.getDescription().length() > DESCRIPTION_LENGTH){
                        workOrder.setDescription(workOrder.getDescription().substring(0,DESCRIPTION_LENGTH));
                    }
                }

                workOrder.setStatus(workOrder.getModuleState());

                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS) && AccountUtil.getCurrentUser() != null) {
                    long currentUserId = AccountUtil.getCurrentUser().getOuid();
                    try {
                        V3TenantContext tenant = V3PeopleAPI.getTenantForUser(currentUserId);
                        if (tenant != null) {
                            if(workOrder.getSiteId() < 0) {
                                workOrder.setSiteId(tenant.getSiteId());
                            }
                            if(workOrder.getTenant() == null || workOrder.getTenant().getId() <= 0) {
                                workOrder.setTenant(tenant);
                            }
                        }
                    }
                    catch(Exception e) {
                        // Till people is migrated for all orgs
                        LOGGER.error("Error occurred in setting tenant", e);
                    }
                }

                V3WorkOderAPI.handleSiteRelations(workOrder);

                V3TicketAPI.validateSiteSpecificData(workOrder);

                if(workOrder.getCreatedBy()==null || workOrder.getCreatedBy().getId()==-1){
                    workOrder.setCreatedBy(AccountUtil.getCurrentUser());
                }
                if(workOrder.getCreatedTime() == null){
                    workOrder.setCreatedTime(workOrder.getCurrentTime());
                }
                if(workOrder.getScheduledStart() == null){
                    workOrder.setScheduledStart(workOrder.getCurrentTime());
                }
                if (workOrder.getParentWO() != null && workOrder.getParentWO().getStatus()!=null) {
                    validateStatusOfParentAndChild(workOrder);
                }
                if(workOrder.getModifiedTime() == null){
                    workOrder.setModifiedTime(workOrder.getCreatedTime());
                }
                if(workOrder.getApprovalState() == null){
                    workOrder.setApprovalState(ApprovalState.YET_TO_BE_REQUESTED.getValue());
                }
                if (workOrder.getPriority() == null || workOrder.getPriority().getId() == -1) {
                    workOrder.setPriority(priority);
                }
                if (workOrder.getDuration() != null) {
                    workOrder.setDueDate(workOrder.getCreatedTime() + (workOrder.getDuration() * 1000));
                }
                workOrder.setEstimatedEnd(workOrder.getDueDate());

                updateTicketAssignedBy(workOrder);
            }
            catch (Exception e){
                ImportRowContext importRowContext = logIdVsImportRows.get(logId);
                importRowContext.setErrorOccurredRow(true);
                importRowContext.setErrorMessage(e.getMessage());
            }

        }
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.WORKORDER_ACTIVITY);
        return false;
    }
    public static void updateTicketAssignedBy(V3TicketContext ticket) {
        if ((ticket.getAssignedTo() != null && ticket.getAssignedTo().getId() != -1) || (ticket.getAssignmentGroup() != null && ticket.getAssignmentGroup().getId() != -1)) {
           if(ticket.getAssignedBy()==null || ticket.getAssignedBy().getId() ==-1L){
               ticket.setAssignedBy(AccountUtil.getCurrentUser());
           }
        }
    }
    private void validateStatusOfParentAndChild(V3WorkOrderContext workOrder) throws Exception {
        FacilioStatus status = V3TicketAPI.getStatus(workOrder.getParentWO().getStatus().getId());
        if (status.getType() == FacilioStatus.StatusType.CLOSED) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot add open WO as a child to closed parent");
        }
    }
}
