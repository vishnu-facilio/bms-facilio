package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.PhotosContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsoleV3.context.V3PhotosContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.FacilityContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class GetFacilityPhotosCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds  = Constants.getRecordIds(context);

        if(CollectionUtils.isNotEmpty(recordIds)) {
            for(Long id: recordIds) {
                FacilityContext facility = (FacilityContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.FacilityBooking.FACILITY, id);
                if (facility != null) {
                    List<V3PhotosContext> assetPhotos = FacilityAPI.getFacilityPhotoId(id);
                    if(CollectionUtils.isNotEmpty(assetPhotos)) {
                        facility.setPhotos(assetPhotos);
                    }
                }

            }

        }

        return false;
    }
}
