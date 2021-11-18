package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class ValidateAudienceSharingCommandV3   extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<AudienceContext> audienceList = (List<AudienceContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("audience"));

        if(CollectionUtils.isNotEmpty(audienceList)) {
            for (AudienceContext audience : audienceList) {
                if(CollectionUtils.isNotEmpty(audience.getAudienceSharing())) {
                    for(CommunitySharingInfoContext sharing : audience.getAudienceSharing()){

                        if(sharing.getSharingType().intValue() == (CommunitySharingInfoContext.SharingType.SITE).getIndex() && sharing.getSharedToSpace() == null){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Select Atleast One Site");
                        }else if(sharing.getSharingType().intValue() == (CommunitySharingInfoContext.SharingType.BUILDING).getIndex() && sharing.getSharedToSpace() == null){
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Select Atleast One Building");
                        }else if(sharing.getSharingType().intValue() == (CommunitySharingInfoContext.SharingType.ROLE).getIndex() && sharing.getSharedToRole() == null) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Select Atleast One Role");
                        }else if(sharing.getSharingType().intValue() == (CommunitySharingInfoContext.SharingType.PEOPLE).getIndex() && sharing.getSharedToPeople() == null) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Select Atleast One People");
                        }
                    }
                }
            }
        }

        return false;
    }

}
