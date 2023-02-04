package com.facilio.sandbox.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.sandbox.context.SandboxConfigContext;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddOrUpdateSandboxSharingCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        SandboxConfigContext sandboxConfig = (SandboxConfigContext) context.get(SandboxConstants.SANDBOX);
        long sandboxId = sandboxConfig.getId();
        V3Util.throwRestException((sandboxId <= 0), ErrorCode.VALIDATION_ERROR, "Invalid request");

        FacilioModule sandboxSharingModule = ModuleFactory.getSandboxSharingModule();
        List<FacilioField> sandboxSharingFields = FieldFactory.getSandboxSharingFields();

        if(CollectionUtils.isEmpty(sandboxConfig.getSandboxSharing())) {
            SharingAPI.deleteSharingForParent(Collections.singletonList(sandboxId), sandboxSharingModule);
            return false;
        }

        SharingContext<SingleSharingContext> existingSharing = SharingAPI.getSharing(sandboxId, sandboxSharingModule, SingleSharingContext.class, sandboxSharingFields);
        Map<Long, Long> existingUserIds = (CollectionUtils.isNotEmpty(existingSharing)) ? existingSharing.stream().collect(Collectors.toMap(SingleSharingContext::getUserId, SingleSharingContext::getId)) : null;

        SharingContext<SingleSharingContext> sharingToAdd = new SharingContext<>();
        long currentUserId = AccountUtil.getCurrentUser().getOuid();
        List<Long> requestedUserIds = new ArrayList();

        sandboxConfig.getSandboxSharing().stream().forEach((sharingContext) -> {
            requestedUserIds.add(sharingContext.getUserId());
            if((MapUtils.isEmpty(existingUserIds)) || (existingUserIds != null && !existingUserIds.containsKey(sharingContext.getUserId()))) {
                sharingContext.setSharedBy(currentUserId);
                sharingToAdd.add(sharingContext);
            }
        });

        List<Long> sharingIdsToRemove = (CollectionUtils.isNotEmpty(existingSharing)) ? existingSharing.stream().filter(sharingContext -> !requestedUserIds.contains(sharingContext.getUserId())).map(SingleSharingContext::getId).collect(Collectors.toList()) : null;

        //removing the existing sharings which are not selected
        if(CollectionUtils.isNotEmpty(sharingIdsToRemove)) {
            SharingAPI.deleteSharing(sharingIdsToRemove, sandboxSharingModule);
        }

        //Adding newly shared users
        if(CollectionUtils.isNotEmpty(sharingToAdd)) {
            SharingAPI.addSharing(sharingToAdd, sandboxSharingFields, sandboxId, sandboxSharingModule);
        }

        return false;
    }
}
