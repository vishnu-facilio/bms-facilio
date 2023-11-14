package com.facilio.utility.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.utility.context.UtilityIntegrationTariffContext;
import com.facilio.utility.context.UtilityIntegrationTariffSlabContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ValidateUtilityIntegrationTariffSlabCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<UtilityIntegrationTariffSlabContext> tariffs = recordMap.get(moduleName);

        String errorMessage = getValidationErrorMessage(tariffs);

        if (errorMessage != null) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, errorMessage);
        }
//                if (!isValidSlabs(tariffs)) {
//                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid slab range.");
//                }
            return false;
        }
      public static String getValidationErrorMessage(List<UtilityIntegrationTariffSlabContext> slabs) {

        slabs.sort(Comparator.comparingDouble(UtilityIntegrationTariffSlabContext::getFrom));

        for (int i = 0; i < slabs.size() - 1; i++) {
            UtilityIntegrationTariffSlabContext currentSlab = slabs.get(i);
            UtilityIntegrationTariffSlabContext nextSlab = slabs.get(i + 1);
            if (currentSlab.getFrom() < 0 || currentSlab.getTo() <= currentSlab.getFrom()) {
                return "Invalid range for slab " + currentSlab.getName() + ": 'From' should be less than 'To'";
            }
            if (nextSlab.getFrom() <= currentSlab.getTo()) {
                return "Overlapping or invalid range between slabs " + currentSlab.getName() + " and " + nextSlab.getName() ;
                 }
        }
        UtilityIntegrationTariffSlabContext lastSlab = slabs.get(slabs.size() - 1);
          if (lastSlab.getFrom() < 0 || lastSlab.getTo() <= lastSlab.getFrom()) {
              return "Invalid range for the last slab " + lastSlab.getName() + ": 'From' should be less than 'To'";
          }
          return null;
      }
}
