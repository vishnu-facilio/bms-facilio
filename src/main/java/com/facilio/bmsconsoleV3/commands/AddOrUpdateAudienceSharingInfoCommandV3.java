package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.CommunitySharingInfoContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.AudienceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class AddOrUpdateAudienceSharingInfoCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<AudienceContext> audienceList = (List<AudienceContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("audience"));

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule sharingInfoModule = modBean.getModule(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING);
        if(CollectionUtils.isNotEmpty(audienceList)) {
            for (AudienceContext audience : audienceList) {
                if(audience.getId() > 0){
                    deleteAudienceSharing(audience.getId());
                }

                if(CollectionUtils.isNotEmpty(audience.getAudiencesharing())) {
                    for(CommunitySharingInfoContext sharing : audience.getAudiencesharing()){
                        sharing.setAudienceId(audience.getId());
                    }
                    V3RecordAPI.addRecord(false, audience.getAudiencesharing(), sharingInfoModule, modBean.getAllFields(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING));
                }
            }
        }

        return false;
    }

    private void deleteAudienceSharing(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.Tenant.AUDIENCE_SHARING);
        DeleteRecordBuilder<CommunitySharingInfoContext> deleteBuilder = new DeleteRecordBuilder<CommunitySharingInfoContext>()
                .module(module)
                .andCondition(CriteriaAPI.getCondition("AUDIENCE_ID", "audienceId", String.valueOf(id), NumberOperators.EQUALS));
        deleteBuilder.delete();
    }
}
