package com.facilio.bmsconsoleV3.commands.floorplan;


import com.amazonaws.services.dynamodbv2.xspec.B;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.AmenitiesContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3MarkerdZonesContext;
import com.facilio.bmsconsoleV3.context.spacebooking.SpaceAmenityContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.microsoft.schemas.office.visio.x2012.main.ShapesType;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SetDefaultFloorplanBookingVariableCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long floorplanId = (long) context.get(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN_ID);
		String viewMode = (String) context.get(FacilioConstants.ContextNames.Floorplan.VIEW_MODE);

		List<Long> amentiyIds = (List<Long>) context.get(FacilioConstants.ContextNames.FacilityBooking.AMENITY);
		Boolean isNewBooking = (Boolean) context.get("IS_NEW_BOOKING");
		if (viewMode == null) {
			viewMode = FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW;
		}
		if(viewMode.equals(FacilioConstants.ContextNames.Floorplan.BOOKING_VIEW) && isNewBooking && !amentiyIds.isEmpty()) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SPACE_AMENITY);
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

			SelectRecordsBuilder<SpaceAmenityContext> builder = new SelectRecordsBuilder<SpaceAmenityContext>()
					.module(module)
					.beanClass(SpaceAmenityContext.class)
					.select(modBean.getAllFields(module.getName()))
					.andCondition(CriteriaAPI.getCondition("AMENITY_ID", "right",
							StringUtils.join(amentiyIds, ","), NumberOperators.EQUALS));
			List<SpaceAmenityContext> spaceAmenities = builder.get();
			List<Long> spaceIds = new ArrayList<>();
			if(spaceAmenities != null) {
				for (SpaceAmenityContext spaceAmenity : spaceAmenities) {
					if (spaceAmenity.getLeft() != null && !spaceIds.contains(spaceAmenity.getLeft().getId())) {
						spaceIds.add(spaceAmenity.getLeft().getId());
					}
				}
			}
			context.put("filteredSpaceIds",spaceIds);
		}

		return false;
	}

    }
