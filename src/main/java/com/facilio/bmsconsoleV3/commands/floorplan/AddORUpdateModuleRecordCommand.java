package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.floorplan.V3FloorplanMarkersContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.V3Util;

public class AddORUpdateModuleRecordCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        List<V3IndoorFloorPlanContext> floorplans = recordMap.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
	

        if (CollectionUtils.isNotEmpty(floorplans)) {

            for (V3IndoorFloorPlanContext floorplan : floorplans) {

                List<V3MarkerContext> markers = floorplan.getMarkers();

                if (CollectionUtils.isNotEmpty(markers)) {
                	
                	
                	
                	for (V3MarkerContext marker : markers) {
                		
                		marker.getMarkerType();
                		
                		V3FloorplanMarkersContext markerType  =  V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.MARKER_TYPE, marker.getMarkerType().getId());

        				if (marker.getRecordId() != null && marker.getMarkerModuleId() != null && markerType.getIsAutoCreate()) {
        					//update 
                        	
                        	FacilioModule module = modBean.getModule(marker.getMarkerModuleId());
                        	List<FacilioField> fields = modBean.getAllFields(module.getName());
                   	
                        	
                        	 if(!module.getName().equals("desks")) {
                        		 //update desk
                     		 }

        				}
        				
        				else if (marker.getRecordId() == null && marker.getMarkerModuleId() != null && markerType.getIsAutoCreate()) {
        					// add marker
                        	
                    		FacilioModule module = modBean.getModule(marker.getMarkerModuleId());
                    		 List<FacilioField> fields = modBean.getAllFields(module.getName());
                    		 
                    		 if(!module.getName().equals("desks")) {
								JSONObject obj = new JSONObject();
								obj.put(modBean.getPrimaryField(module.getName()).getName(),  marker.getLabel());
								 FacilioContext contextNew = V3Util.createRecord(module, FieldUtil.getAsJSON(obj));
								 
								 Map<String, List> dataMap = (Map<String, List>) contextNew.get(Constants.RECORD_MAP);

							      List list = dataMap.get(module.getName());
							      
								  ModuleBaseWithCustomFields data = (ModuleBaseWithCustomFields) list.get(0);

								  marker.setRecordId(data.getId());
                             	System.out.println("add =====>" + marker + "   =>" + module + "===>" +  data); 

                    		 }


        	
        				}
                	}
                }

            }

        }

        return false;

    }


}
