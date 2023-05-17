package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;

public class UpdateChildInvitesAfterSaveCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.GROUP_INVITES)) {

            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
            List<InviteVisitorContextV3> records = recordMap.get(FacilioConstants.ContextNames.INVITE_VISITOR);
            Map<String, List<InviteVisitorContextV3>> childInvitesMap = new HashMap<String, List<InviteVisitorContextV3>>();
            childInvitesMap.put(FacilioConstants.ContextNames.INVITE_VISITOR, records);
            FacilioChain addInviteVisitorBeforeSaveChain = TransactionChainFactoryV3.getInviteVisitorBeforeSaveOnCreateChain();
            FacilioContext addInviteVisitorBeforeSaveChainContext = addInviteVisitorBeforeSaveChain.getContext();
            Constants.setModuleName(addInviteVisitorBeforeSaveChainContext, FacilioConstants.ContextNames.INVITE_VISITOR);
            Constants.setRawInput(addInviteVisitorBeforeSaveChainContext, FieldUtil.getAsJSON(childInvitesMap));
            addInviteVisitorBeforeSaveChainContext.put(Constants.RECORD_MAP, childInvitesMap);
            addInviteVisitorBeforeSaveChainContext.put(Constants.BEAN_CLASS, InviteVisitorContextV3.class);
            addInviteVisitorBeforeSaveChainContext.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
            addInviteVisitorBeforeSaveChainContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
            addInviteVisitorBeforeSaveChain.execute();
            List<InviteVisitorContextV3> childInvites = childInvitesMap.get(FacilioConstants.ContextNames.INVITE_VISITOR);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
            List<FacilioField> fields = modBean.getAllFields(module.getName());
            for (InviteVisitorContextV3 childInvite : childInvites) {
                V3RecordAPI.updateRecord(childInvite, module, fields, false, true);
            }


            FacilioChain addInviteVisitorAfterSaveChain = TransactionChainFactoryV3.getInviteVisitorAfterSaveOnCreateBeforeTransactionChain();
            FacilioContext addInviteVisitorAfterSaveChainContext = addInviteVisitorAfterSaveChain.getContext();

            Constants.setModuleName(addInviteVisitorAfterSaveChainContext, FacilioConstants.ContextNames.INVITE_VISITOR);
            Constants.setRawInput(addInviteVisitorAfterSaveChainContext, FieldUtil.getAsJSON(childInvitesMap));
            addInviteVisitorAfterSaveChainContext.put(Constants.RECORD_MAP, childInvitesMap);
            addInviteVisitorAfterSaveChainContext.put(Constants.BEAN_CLASS, InviteVisitorContextV3.class);
            addInviteVisitorAfterSaveChainContext.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
            addInviteVisitorAfterSaveChainContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
            addInviteVisitorAfterSaveChain.execute();

            FacilioChain addInviteVisitorChain = TransactionChainFactoryV3.getInviteVisitorAfterSaveOnCreateChain();
            FacilioContext addInviteVisitorChainContext = addInviteVisitorChain.getContext();
            addInviteVisitorChainContext.put(FacilioConstants.ContextNames.OLD_INVITES, (List<InviteVisitorContextV3>) addInviteVisitorBeforeSaveChainContext.get(FacilioConstants.ContextNames.OLD_INVITES));
            addInviteVisitorChainContext.put(Constants.QUERY_PARAMS, (Map<String, List<Object>>) addInviteVisitorBeforeSaveChainContext.get(Constants.QUERY_PARAMS));
            addInviteVisitorChainContext.put(FacilioConstants.ContextNames.INVITE_VISITOR_RECORDS, (List<InviteVisitorContextV3>) addInviteVisitorBeforeSaveChainContext.get(FacilioConstants.ContextNames.INVITE_VISITOR_RECORDS));
            Constants.setModuleName(addInviteVisitorChainContext, FacilioConstants.ContextNames.INVITE_VISITOR);
            Constants.setRawInput(addInviteVisitorChainContext, FieldUtil.getAsJSON(childInvitesMap));
            addInviteVisitorChainContext.put(Constants.RECORD_MAP, childInvitesMap);
            addInviteVisitorChainContext.put(Constants.BEAN_CLASS, InviteVisitorContextV3.class);
            addInviteVisitorChainContext.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
            addInviteVisitorChainContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
            addInviteVisitorChain.execute();
        }
		return false;

    }
}
