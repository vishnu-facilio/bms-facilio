package com.facilio.bmsconsoleV3.signup.scoping;

import java.util.ArrayList;
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
            
            List<V3FloorplanMarkersContext> markerTypes = new ArrayList<>();


            V3FloorplanMarkersContext desktype  = new V3FloorplanMarkersContext();

            desktype.setName("desk");
            desktype.setModuleId(markerTypeModule.getModuleId());
            desktype.setOrgId(orgId);
            desktype.setRecordModuleId(desk.getModuleId());
            desktype.setType(1);
            desktype.setFileId((long) -1);
            desktype.setIsAutoCreate(true);
            desktype.setEnableNumbering(true);
            markerTypes.add(desktype);
            
            
            String markerNames[] = { "camera", "cctv", "elevator", "escalator", "femalerestroom", "fireextingus", "kitchen1", "kitchen2", "locker", "malerestroom", "parking", "restroom" };  

     
            
            for (String markerName : markerNames) {
            	
            	 V3FloorplanMarkersContext markerType  = new V3FloorplanMarkersContext();

            	 markerType.setName(markerName);
            	 markerType.setModuleId(markerTypeModule.getModuleId());
            	 markerType.setOrgId(orgId);
            	 markerType.setType(1);
            	 markerType.setFileId((long) -1);
            	 markerType.setIsAutoCreate(false);
            	 markerType.setEnableNumbering(false);
                 markerTypes.add(markerType);
            }
            

            if(orgId < 1){
                throw new IllegalArgumentException("Invalid orgId");
            }
            

            V3RecordAPI.addRecord(false, markerTypes, markerTypeModule, fields);
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
