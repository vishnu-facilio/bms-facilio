package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;

public class UpdateInviteVisitorStateInChangeSetCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<InviteVisitorContextV3> visitorLoggingRecords = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(visitorLoggingRecords)) {
            Map<String, Map<Long, List<UpdateChangeSet>>> moduleChangeSetMap = new HashMap<String, Map<Long,List<UpdateChangeSet>>>();
            Map<Long, List<UpdateChangeSet>> changeSet = new HashMap<Long, List<UpdateChangeSet>>();

            for(InviteVisitorContextV3 vl : visitorLoggingRecords) {
                List<UpdateChangeSet> changes = new ArrayList<UpdateChangeSet>();
                UpdateChangeSet updateChangeState = new UpdateChangeSet();

                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
                long fieldId = modBean.getField("moduleState", module.getName()).getFieldId();

                InviteVisitorContextV3 inviteVisitorContext = V3VisitorManagementAPI.getInviteVisit(vl.getId(), null, false);

                if(inviteVisitorContext != null) {
                    updateChangeState.setFieldId(fieldId);
                    updateChangeState.setNewValue(inviteVisitorContext.getModuleState().getId());
                    changes.add(updateChangeState);
                    changeSet.put(inviteVisitorContext.getId(), changes);

                }
            }
            moduleChangeSetMap.put(FacilioConstants.ContextNames.INVITE_VISITOR, changeSet);
            CommonCommandUtil.appendChangeSetMapToContext(context, changeSet, moduleName);
        }
        return false;
    }
}

