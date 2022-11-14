package com.facilio.bmsconsoleV3.commands.floorplan;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanGeoJsonContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanPropertiesContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerdZonesContext;
import com.facilio.bmsconsoleV3.util.V3FloorPlanAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;



public class getIndoorFloorPlanPropertiesCommand extends FacilioCommand {

    @SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
    	
	    long objectId = (long) context.get(FacilioConstants.ContextNames.Floorplan.OBJECTID);
	    Long startTime = (Long) context.get(FacilioConstants.ContextNames.START_TIME);
	    Long endTime = (Long) context.get(FacilioConstants.ContextNames.ENDTIME);
	    

		
	    
	    JSONObject propertiesResult = new JSONObject();

        List<Long> bookingSpaceIds = new ArrayList<Long>(); 
        

        V3MarkerContext marker = V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.MARKER, objectId);
        V3MarkerdZonesContext zone = V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES, objectId);
        
        List<V3MarkerContext> markers = new ArrayList<>();
        List<V3MarkerdZonesContext> zones = new ArrayList<>();

        if (marker != null) {
        	markers.add(marker);
        	
        	marker.setObjectType(1);
        	
        	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule markerModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKER);
      		List<FacilioField> markerFields = modBean.getAllFields(markerModule.getName());
        	
            
            if (marker.getRecordId() != null && marker.getMarkerModuleId() != null) {
            	
            	
                FacilioModule module = modBean.getModule(marker.getMarkerModuleId());
          		List<FacilioField> fields = modBean.getAllFields(module.getName());
          		
                if(module.getName().equals(FacilioConstants.ContextNames.Floorplan.DESKS)) {
                	
                	Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
                      
                    Collection<SupplementRecord>lookUpfields = new ArrayList<>();
              		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
               		lookUpfields.add((LookupField) fieldMap.get("employee"));
               		lookUpfields.add((LookupField) fieldMap.get("department"));
					lookUpfields.add((LookupField) fieldMap.get("building"));
					lookUpfields.add((LookupField) fieldMap.get("floor"));


					lookUpfields.add((MultiLookupField) fieldMap.get("amenities"));


					List<V3DeskContext> desklist = V3RecordAPI.getRecordsListWithSupplements(module.getName(), Collections.singletonList(marker.getRecordId()), beanClassName, lookUpfields);

               		if (desklist != null) {
               			V3DeskContext deskData = (V3DeskContext)desklist.get(0);
               			propertiesResult.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
               			propertiesResult.put(FacilioConstants.ContextNames.Floorplan.RECORD_FIELDS, getFields(module.getName()));
               			propertiesResult.put(FacilioConstants.ContextNames.Floorplan.RECORD, deskData);
               			
               			if (deskData.getDeskType() == 3) {
               				bookingSpaceIds.add(deskData.getId());
               			}
               			
               			
               			
               		}
       

                }
                else {
           			propertiesResult.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
                	propertiesResult.put(FacilioConstants.ContextNames.Floorplan.RECORD, V3RecordAPI.getRecord(module.getName(), marker.getRecordId()));
                	propertiesResult.put(FacilioConstants.ContextNames.Floorplan.RECORD_FIELDS, fields);

                }

            }
            
            propertiesResult.put(FacilioConstants.ContextNames.Floorplan.OBJECT_FIEDS, getFields(markerModule.getName()));
            propertiesResult.put(FacilioConstants.ContextNames.Floorplan.OBJECT, FieldUtil.getAsProperties(marker));

        	
        }
        else if (zone != null) {
        	
        	zones.add(zone);
        	zone.setObjectType(2);

        	
        	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule objectModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES);
      		List<FacilioField> objectFields = modBean.getAllFields(objectModule.getName());

			if (zone.isIsReservable()) {
          		bookingSpaceIds.add(zone.getSpace().getId());
      		}
            
            if (zone.getRecordId() != null && zone.getZoneModuleId() != null) {
            	
                FacilioModule module = modBean.getModule(zone.getZoneModuleId());
          		List<FacilioField> fields = modBean.getAllFields(module.getName());
       			propertiesResult.put(FacilioConstants.ContextNames.MODULE_NAME, module.getName());
          	  	propertiesResult.put(FacilioConstants.ContextNames.Floorplan.RECORD, V3RecordAPI.getRecord(module.getName(), zone.getRecordId()));
            	propertiesResult.put(FacilioConstants.ContextNames.Floorplan.RECORD_FIELDS, fields);


            }
            
            propertiesResult.put(FacilioConstants.ContextNames.Floorplan.OBJECT_FIEDS, getFields(objectModule.getName()));
            propertiesResult.put(FacilioConstants.ContextNames.Floorplan.OBJECT, FieldUtil.getAsProperties(zone));

        	
        }

    	
        
        context.put(FacilioConstants.ContextNames.SPACE_LIST, bookingSpaceIds);
        context.put(FacilioConstants.ContextNames.START_TIME, startTime);
        context.put(FacilioConstants.ContextNames.END_TIME, endTime);
        

        context.put(FacilioConstants.ContextNames.Floorplan.MARKER_LIST, markers);
        context.put(FacilioConstants.ContextNames.Floorplan.ZONE_LIST, zones);
        
        context.put(FacilioConstants.ContextNames.Floorplan.PROPERTIES, propertiesResult);
        
        
     	
                
        
    	return false;
    }
    
	 public static List<FacilioField> getFields(String moduleName) throws Exception {
		 
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			context.put("handleStateField", true);
			
			FacilioChain getFieldsChain = FacilioChainFactory.getGetFieldsChain();
			getFieldsChain.execute(context);
		
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
		 
		 return fields;
	 }
    
    

}