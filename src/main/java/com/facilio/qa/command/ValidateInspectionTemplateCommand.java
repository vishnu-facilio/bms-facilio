package com.facilio.qa.command;

import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.bmsconsoleV3.util.V3AssetAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class ValidateInspectionTemplateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
        if(CollectionUtils.isNotEmpty(inspections)) {
            for (InspectionTemplateContext inspection : inspections) {
                if(inspection!=null && inspection.getCreationType()!=null && InspectionTemplateContext.CreationType.valueOf(inspection.getCreationType()).equals(InspectionTemplateContext.CreationType.SINGLE) && inspection.getResource()!=null && inspection.getResource().getId()>0){
                    if(V3AssetAPI.isAssetInStoreRoom(inspection.getResource())){
                        throw new RESTException(ErrorCode.VALIDATION_ERROR ,"Cannot create inspection for a rotating asset present in storeroom");
                    }
                }
            }
        }
    return false;
    }
}
