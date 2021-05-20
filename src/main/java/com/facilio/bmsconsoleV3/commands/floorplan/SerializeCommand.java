package com.facilio.bmsconsoleV3.commands.floorplan;

import org.apache.commons.collections4.CollectionUtils;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerdZonesContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.FieldFactory;


import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SerializeCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

		List<V3IndoorFloorPlanContext> floorplans = recordMap
				.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN);

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule floorplanmarkers = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKER);
		List<FacilioField> fields = modBean.getAllFields(floorplanmarkers.getName());

		FacilioModule floorplanzones = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES);
		List<FacilioField> zonesFields = modBean.getAllFields(floorplanzones.getName());
		Map<String, FacilioField> zonesFieldMap = FieldFactory.getAsMap(fields);

		if (CollectionUtils.isNotEmpty(floorplans)) {

			for (V3IndoorFloorPlanContext floorplan : floorplans) {

				SelectRecordsBuilder builder = new SelectRecordsBuilder()
						.module(modBean.getModule(floorplanmarkers.getName())).select(fields)
						.beanClass(V3MarkerContext.class);

				builder.andCondition(CriteriaAPI.getCondition("FLOORPLAN_ID", "floorplanId",
						String.valueOf(floorplan.getId()), NumberOperators.EQUALS));

				List<V3MarkerContext> markers = builder.get();

				floorplan.setMarkers(markers);
				
				List<LookupField> supplements = new ArrayList<>();
				supplements.add((LookupField) zonesFieldMap.get("space"));

				SelectRecordsBuilder zonesBuilder = new SelectRecordsBuilder()
						.module(modBean.getModule(floorplanzones.getName())).select(zonesFields)
						.beanClass(V3MarkerdZonesContext.class)
						.fetchSupplements(supplements);

				zonesBuilder.andCondition(CriteriaAPI.getCondition("FLOORPLAN_ID", "floorplanId",
						String.valueOf(floorplan.getId()), NumberOperators.EQUALS));

				List<V3MarkerdZonesContext> zones = zonesBuilder.get();

				floorplan.setMarkedZones(zones);

				setDeskRecords(floorplan.getMarkers());

			}

		}

		return false;
	}

	public static void setDeskRecords(List<V3MarkerContext> markers) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule deskModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.DESKS);
		List<FacilioField> fields = modBean.getAllFields(deskModule.getName());
		Map<String, FacilioField> deskFieldMap = FieldFactory.getAsMap(fields);

		// need to handle for all the modules Like Desk, locker, etc...
		if (CollectionUtils.isNotEmpty(markers)) {
			for (V3MarkerContext marker : markers) {
				if (marker.getRecordId() != null) {

					FacilioModule recordModule = modBean.getModule(marker.getMarkerModuleId());
           		 
           		 if(recordModule.getName().equals("desks")) {

					Long id = marker.getRecordId();
					List<LookupField> supplements = new ArrayList<>();
					supplements.add((LookupField) deskFieldMap.get("employee"));
					supplements.add((LookupField) deskFieldMap.get("department"));
					
					if (id > 0) {
						SelectRecordsBuilder deskbuilder = new SelectRecordsBuilder()
						.module(deskModule).select(fields)
						.beanClass(V3DeskContext.class)
						.andCondition(CriteriaAPI.getIdCondition(id, deskModule))
						.fetchSupplements(supplements);

						V3DeskContext desk = (V3DeskContext) deskbuilder.fetchFirst();
						if(desk != null) {
							marker.setDesk(desk);
						}
					}
           		 }
           		 else {
           			ModuleBaseWithCustomFields record =  V3RecordAPI.getRecord(recordModule.getName(), marker.getRecordId());
                    
                    marker.setModuleData(record);
           		 }

				}

			}
		}

	}
}