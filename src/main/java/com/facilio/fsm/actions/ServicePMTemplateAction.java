package com.facilio.fsm.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;


@Getter @Setter
public class ServicePMTemplateAction  extends V3Action {
    private static final long serialVersionUID = 1L;

    private Long recordId;
    private Integer type;
    private List<Long> siteIds;
    private List<Long> assetIds;
    private List<Long> spaceIds;
    private ServicePMTriggerContext trigger;

    public String createServicePM() throws Exception{
        try{
            if(recordId==null){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Master PM is required");
            }
            if(type==null){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Type is required");
            }
            if(type.equals(ServicePlannedMaintenanceContext.PMType.SITE.getIndex()) && CollectionUtils.isEmpty(siteIds)){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Sites are required");
            }
            if(type.equals(ServicePlannedMaintenanceContext.PMType.SPACE.getIndex()) && CollectionUtils.isEmpty(spaceIds)){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Spaces are required");
            }
            if(type.equals(ServicePlannedMaintenanceContext.PMType.ASSET.getIndex()) && CollectionUtils.isEmpty(assetIds)){
                throw new RESTException(ErrorCode.VALIDATION_ERROR,"Assets are required");
            }
            long orgId = AccountUtil.getCurrentOrg().getOrgId();
            JSONObject message = new JSONObject();
            message.put("orgId", orgId);
            message.put("servicePMTemplateId", recordId);
            message.put("type", type);
            message.put("siteIds", siteIds);
            message.put("assetIds", assetIds);
            message.put("spaceIds", spaceIds);
            message.put("trigger", trigger);

            Messenger.getMessenger().sendMessage(new Message()
                    .setKey("service_pm_template/" + recordId + "/execute")
                    .setOrgId(orgId)
                    .setContent(message)
            );
            HashMap<String, String> successMsg = new HashMap<>();
            successMsg.put("message","Successfully created PMs");
            setData(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_STATUS_ACTIONS,successMsg);
        }catch (Exception e){
            throw new RESTException(ErrorCode.VALIDATION_ERROR,e.getMessage());
        }
        return V3Action.SUCCESS;

    }
}
