package com.facilio.bmsconsoleV3.signup.scoping;

import java.util.Collections;
import java.util.List;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.floorplan.V3FloorplanMarkersContext;
import com.facilio.bmsconsoleV3.signup.SignUpData;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;


public class FloorPlanScopingConfig extends SignUpData {
    @Override
    public void addData() {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule desk = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
            FacilioModule markerTypeModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKER_TYPE);
            List<FacilioField> fields = modBean.getAllFields(markerTypeModule.getName());

            long orgId = AccountUtil.getCurrentOrg().getOrgId();
            
            V3FloorplanMarkersContext markerType  = new V3FloorplanMarkersContext();

            markerType.setName("desk");
            markerType.setModuleId(markerTypeModule.getModuleId());
            markerType.setOrgId(orgId);
            markerType.setRecordModuleId(desk.getModuleId());
            markerType.setType(1);
            markerType.setIsAutoCreate(true);
            markerType.setEnableNumbering(true);

            if(orgId < 1){
                throw new IllegalArgumentException("Invalid orgId");
            }
            

            V3RecordAPI.addRecord(false, Collections.singletonList(markerType), markerTypeModule, fields);
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
