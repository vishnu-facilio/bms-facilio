package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.StateFlowRulesAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ChangeVisitorInviteStateCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorLoggingContext> records = recordMap.get(moduleName);
        Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);

        List<V3VisitorLoggingContext> oldRecords = (List<V3VisitorLoggingContext>) context.get(FacilioConstants.ContextNames.VISITOR_LOGGING_RECORDS);

        if(MapUtils.isNotEmpty(changeSet)){
            if(CollectionUtils.isNotEmpty(records)) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                    long time = System.currentTimeMillis();
                    for (V3VisitorLoggingContext record : records) {
                        List<UpdateChangeSet> updatedSet = changeSet.get(record.getId());
                        if (updatedSet != null && !updatedSet.isEmpty()) {
                            for (UpdateChangeSet changes : updatedSet) {
                                FacilioField field = modBean.getField(changes.getFieldId());
                                if (field != null) {
                                    if (field.getName().equals("moduleState") && changes.getNewValue()  != null) {
                                        FacilioStatus status = StateFlowRulesAPI.getStateContext((Long)changes.getNewValue());
                                        if (status.getStatus().toString().trim().equals("Invited") || status.getStatus().toString().trim().equals("Upcoming")) {
                                            V3VisitorManagementAPI.updateVisitorLogInvitationStatus(record, true);
                                        } else if (status.getStatus().toString().trim().equals("CheckedIn")) {
                                            FacilioChain c = TransactionChainFactory.updateVisitorLoggingRecordsChain();
                                            if (record.getVisitor() != null && record.getVisitor().getId() > 0) {
                                                V3VisitorManagementAPI.getActiveLogExcludingCurrentLog(record, c.getContext());
                                            }
                                            if (c.getContext().get("visitorLogging") != null) {
                                                c.getContext().put(FacilioConstants.ContextNames.TRANSITION_ID, c.getContext().get("nextTransitionId"));
                                                V3VisitorLoggingContext visitorLoggingContext = (V3VisitorLoggingContext) c.getContext().get("visitorLogging");
                                                c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(visitorLoggingContext));
                                                c.execute();
                                            }
                                            if (record.getCheckInTime() == null ||  record.getCheckInTime() <= 0) {
                                                V3VisitorManagementAPI.updateVisitorLogCheckInCheckoutTime(record, true, time);
                                                V3VisitorManagementAPI.updateVisitorRollUps(record, oldRecords.get(0));
                                                V3VisitorManagementAPI.updateVisitorLastVisitRollUps(record);
                                            }
                                        } else if (status.getStatus().toString().trim().equals("CheckedOut")) {
                                            V3VisitorManagementAPI.updateVisitorLogCheckInCheckoutTime(record, false, time);
                                            V3VisitorManagementAPI.updateVisitorLastVisitDurationRollUp(record);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        return false;
    }
}
