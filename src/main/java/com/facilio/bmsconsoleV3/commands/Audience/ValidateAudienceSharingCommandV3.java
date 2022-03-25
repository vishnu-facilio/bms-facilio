package com.facilio.bmsconsoleV3.commands.Audience;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateAudienceSharingCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<AudienceContext> audienceList = (List<AudienceContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("audience"));
        for(AudienceContext audience:audienceList){
            if(CollectionUtils.isEmpty(audience.getAudienceSharing())){
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Sharing information cannot be empty");
            }
            if(audience.getFilterSharingType() != null && audience.getFilterSharingType() == Long.valueOf(CommunitySharingInfoContext.SharingType.ROLE.getIndex())){
                boolean isRoleFilterEmpty = true;
                if(CollectionUtils.isNotEmpty(audience.getAudienceSharing()))
                for(CommunitySharingInfoContext sharingInfo : audience.getAudienceSharing()){
                    if(sharingInfo.getSharedToRoleId() != null){
                        isRoleFilterEmpty = false;
                    }
                }
                if(isRoleFilterEmpty){
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Role filter cannot be empty");
                }
            }
        }
        return false;
    }
}