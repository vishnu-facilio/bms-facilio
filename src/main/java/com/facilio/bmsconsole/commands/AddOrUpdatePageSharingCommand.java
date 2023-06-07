package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PagesContext;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class AddOrUpdatePageSharingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        PagesContext customPage = (PagesContext) context.get(FacilioConstants.CustomPage.CUSTOM_PAGE);
        Objects.requireNonNull(customPage, "Custom page can't be null to add roles sharing");
        long pageId = customPage.getId();
        FacilioUtil.throwIllegalArgumentException(pageId <= 0, "Invalid page id to add sharing");
        FacilioModule pageSharingModule = ModuleFactory.getPageSharingModule();

        if(CollectionUtils.isNotEmpty(customPage.getPageSharing())) {
            List<FacilioField> pageSharingFields = FieldFactory.getPageSharingFields();

            SharingContext<SingleSharingContext> existingPageSharing = SharingAPI.getSharing(pageId, pageSharingModule, SingleSharingContext.class, pageSharingFields);

            List<Long> sharedRoleIds = CollectionUtils.isNotEmpty(existingPageSharing)?existingPageSharing.stream()
                    .map(SingleSharingContext::getRoleId)
                    .collect(Collectors.toList()) : null;

            SharingContext<SingleSharingContext> newRoleIdsToAdd = new SharingContext<>();
            long currentUserId = AccountUtil.getCurrentUser().getId();
            List<Long> shareToRoleIds = new ArrayList<>();

            customPage.getPageSharing().forEach((sharingContext) -> {
                shareToRoleIds.add(sharingContext.getRoleId());
                if ((CollectionUtils.isEmpty(sharedRoleIds)) || (!sharedRoleIds.contains(sharingContext.getRoleId()))) {
                    sharingContext.setType(SingleSharingContext.SharingType.ROLE.getValue());
                    sharingContext.setSharedBy(currentUserId);
                    newRoleIdsToAdd.add(sharingContext);
                }
            });
            List<Long> sharedRoleIdsToRemove = (CollectionUtils.isNotEmpty(sharedRoleIds)) ? existingPageSharing.stream()
                    .filter(sharingContext -> !shareToRoleIds.contains(sharingContext.getRoleId()))
                    .map(SingleSharingContext::getId).collect(Collectors.toList()) : null;

            //removing the existing sharings which are not selected
            if (CollectionUtils.isNotEmpty(sharedRoleIdsToRemove)) {
                SharingAPI.deleteSharing(sharedRoleIdsToRemove, pageSharingModule);
            }
            //Adding newly shared users
            if (CollectionUtils.isNotEmpty(newRoleIdsToAdd)) {
                SharingAPI.addSharing(newRoleIdsToAdd, pageSharingFields, pageId, pageSharingModule);
            }
        }
        //deleting pageSharing if none is configure in input
        else {
            SharingAPI.deleteSharingForParent(Collections.singletonList(customPage.getId()), pageSharingModule);
        }

        return false;
    }
}
