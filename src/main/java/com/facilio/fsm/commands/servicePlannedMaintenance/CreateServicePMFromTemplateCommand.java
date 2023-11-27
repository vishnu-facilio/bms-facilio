package com.facilio.fsm.commands.servicePlannedMaintenance;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fsm.context.ServicePMTemplateContext;
import com.facilio.fsm.context.ServicePMTriggerContext;
import com.facilio.fsm.context.ServicePlannedMaintenanceContext;
import com.facilio.fsm.context.ServicePMTemplateContext.MasterPMType;
import com.facilio.fsm.context.ServiceTaskContext;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import com.facilio.v3.context.SubFormContext;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Log4j
public class CreateServicePMFromTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        Long servicePMTemplateId = (Long) context.get("servicePMTemplateId");
        Integer type = (Integer) context.get("type");
        List<Integer> sites = (List<Integer>)  context.get("siteIds");
        List<Integer> spaces = (List<Integer>) context.get("spaceIds");
        List<Integer> assets = (List<Integer>) context.get("assetIds");
        ServicePMTriggerContext trigger = (ServicePMTriggerContext) context.get("trigger");
        Boolean publish = (Boolean) context.get("publish");
        List<ServicePMTemplateContext> servicePMTemplates = new ArrayList<>();
        ServicePMTemplateContext servicePMTemplate = V3RecordAPI.getRecord(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TEMPLATE,servicePMTemplateId,ServicePMTemplateContext.class);
        if(type.equals(MasterPMType.SITE.getIndex()) && CollectionUtils.isNotEmpty(sites)){
            List<Long> siteIds = sites.stream().map(Integer::longValue).collect(Collectors.toList());
            for(Long siteId : siteIds){
                ServicePMTemplateContext servicePMTemplateClone = FieldUtil.cloneBean(servicePMTemplate,ServicePMTemplateContext.class);
                V3SiteContext site = new V3SiteContext();
                site.setId(siteId);
                servicePMTemplateClone.setSite(site);
                servicePMTemplateClone.setPmType(ServicePlannedMaintenanceContext.PMType.SITE.getIndex());
                servicePMTemplateClone.setServicePMTemplate(servicePMTemplate);
                SubFormContext<ServicePMTriggerContext> subFormContext = new SubFormContext<>();
                subFormContext.setData(Collections.singletonList(trigger));
                servicePMTemplateClone.setRelations(Collections.singletonMap(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER, Collections.singletonList(subFormContext)));
                servicePMTemplates.add(servicePMTemplateClone);
            }
        }else if((type.equals(MasterPMType.SPACE.getIndex()) || type.equals(MasterPMType.BUILDING.getIndex()) || type.equals(MasterPMType.FLOOR.getIndex())) && CollectionUtils.isNotEmpty(spaces)){
            List<Long> spaceIds = spaces.stream().map(Integer::longValue).collect(Collectors.toList());
            for(Long spaceId : spaceIds){
                ServicePMTemplateContext servicePMTemplateClone = FieldUtil.cloneBean(servicePMTemplate,ServicePMTemplateContext.class);
                V3BaseSpaceContext space = V3RecordAPI.getRecord(FacilioConstants.ContextNames.BASE_SPACE,spaceId, V3BaseSpaceContext.class);
                servicePMTemplateClone.setPmType(ServicePlannedMaintenanceContext.PMType.SPACE.getIndex());
                servicePMTemplateClone.setSpace(space);
                servicePMTemplateClone.setSite(space.getSite());
                servicePMTemplateClone.setServicePMTemplate(servicePMTemplate);
                SubFormContext<ServicePMTriggerContext> subFormContext = new SubFormContext<>();
                subFormContext.setData(Collections.singletonList(trigger));
                servicePMTemplateClone.setRelations(Collections.singletonMap(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER, Collections.singletonList(subFormContext)));
                servicePMTemplates.add(servicePMTemplateClone);
            }
        }else if(type.equals(MasterPMType.ASSET.getIndex()) && CollectionUtils.isNotEmpty(assets)){
            List<Long> assetIds = assets.stream().map(Integer::longValue).collect(Collectors.toList());
            for(Long assetId : assetIds){
                ServicePMTemplateContext servicePMTemplateClone = FieldUtil.cloneBean(servicePMTemplate,ServicePMTemplateContext.class);
                V3AssetContext asset = V3RecordAPI.getRecord(FacilioConstants.ContextNames.ASSET,assetId,V3AssetContext.class);
                servicePMTemplateClone.setAsset(asset);
                V3SiteContext site = new V3SiteContext();
                site.setId(asset.getSiteId());
                servicePMTemplateClone.setSite(site);
                servicePMTemplateClone.setPmType(ServicePlannedMaintenanceContext.PMType.ASSET.getIndex());
                servicePMTemplateClone.setServicePMTemplate(servicePMTemplate);
                SubFormContext<ServicePMTriggerContext> subFormContext = new SubFormContext<>();
                subFormContext.setData(Collections.singletonList(trigger));
                servicePMTemplateClone.setRelations(Collections.singletonMap(FacilioConstants.ServicePlannedMaintenance.SERVICE_PM_TRIGGER, Collections.singletonList(subFormContext)));
                servicePMTemplates.add(servicePMTemplateClone);
            }
        }
        FacilioContext createdServicePMs = V3Util.createRecordList(modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE), FieldUtil.getAsMapList(servicePMTemplates, ServicePlannedMaintenanceContext.class), null, null);
        if (createdServicePMs != null && publish!=null && publish) {
            List<ModuleBaseWithCustomFields> servicePMList = getRecordsList(createdServicePMs);
            List<Long> servicePMIds = servicePMList.stream().map(ModuleBaseWithCustomFields ::getId).collect(Collectors.toList());
            FacilioContext servicePMSummaryList =V3Util.getSummary(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE,servicePMIds);
            servicePMList = getRecordsList(servicePMSummaryList);
            JSONObject bodyParams = new JSONObject();
            bodyParams.put("publishServicePM",true);
            V3Util.processAndUpdateBulkRecords(modBean.getModule(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE),servicePMList,  FieldUtil.getAsMapList(servicePMList, ServicePlannedMaintenanceContext.class)  ,bodyParams,null,null,null,null,null,null,null,false,false,null);
        }
        return false;
    }
    private List<ModuleBaseWithCustomFields> getRecordsList(FacilioContext servicePMs){
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(servicePMs);
        List<ModuleBaseWithCustomFields> servicePMList = recordMap.get(FacilioConstants.ServicePlannedMaintenance.SERVICE_PLANNED_MAINTENANCE);
        return servicePMList;
    }
}
