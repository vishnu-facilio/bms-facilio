package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;

public class UpdateChildInvitesAfterSaveCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
    	
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<InviteVisitorContextV3> records = recordMap.get(FacilioConstants.ContextNames.INVITE_VISITOR);
        Map<String, List<InviteVisitorContextV3>> childInvitesMap = new HashMap<String, List<InviteVisitorContextV3>>();
        childInvitesMap.put(FacilioConstants.ContextNames.INVITE_VISITOR, records);

        FacilioChain addInviteVisitorChain = TransactionChainFactoryV3.getInviteVisitorAfterSaveOnCreateChain();
        FacilioContext addInviteVisitorChainContext = addInviteVisitorChain.getContext();
        Constants.setModuleName(addInviteVisitorChainContext, FacilioConstants.ContextNames.INVITE_VISITOR);
        Constants.setRawInput(addInviteVisitorChainContext, FieldUtil.getAsJSON(childInvitesMap));
        addInviteVisitorChainContext.put(Constants.RECORD_MAP, childInvitesMap);
        addInviteVisitorChainContext.put(Constants.BEAN_CLASS, InviteVisitorContextV3.class);
        addInviteVisitorChainContext.put(FacilioConstants.ContextNames.EVENT_TYPE, com.facilio.bmsconsole.workflow.rule.EventType.CREATE);
        addInviteVisitorChainContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
        addInviteVisitorChain.execute();   
    	
		return false;
    	
    }
}
