package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ValidatePolicyCommand extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule policyModule = modBean.getModule(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY);

            List<Long> policyIds = (List<Long>) context.get("POLICIES");
            V3SpaceBookingContext bookingContext = (V3SpaceBookingContext) context.get(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING);
            V3SpaceBookingPolicyJSONContext policyJSON = new V3SpaceBookingPolicyJSONContext();
            JSONObject validationObject = new JSONObject();
            JSONObject policyObject = new JSONObject();

            if (!policyIds.isEmpty()) {

                Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(policyModule);
                Collection<SupplementRecord> policysupplements = new ArrayList<>();
                List<FacilioField> policyFields = modBean.getAllFields(policyModule.getName());
                Map<String, FacilioField> policyFieldMap = FieldFactory.getAsMap(policyFields);
                policysupplements.add((LargeTextField) policyFieldMap.get(FacilioConstants.ContextNames.SpaceBooking.POLICY_JSON));

                List<V3SpaceBookingPolicyContext> policyList = V3RecordAPI.getRecordsListWithSupplements(policyModule.getName(), policyIds, beanClassName, policysupplements);
                policyObject.put("Booking_policy", policyList);
                if (policyList != null)
                {
                    for (V3SpaceBookingPolicyContext policy : policyList) {
                        if (policy != null) {
                            policyJSON = policy.getPolicy();
                            context.put("POLICY_JSON", policyJSON);
                            validationObject = validateJSON(policyJSON, bookingContext);

                        }

                    }
            }
            }
            constructErrorMessage(validationObject,policyJSON);

            
        return false;
    }


    public static JSONObject validateJSON(V3SpaceBookingPolicyJSONContext policyContext,V3SpaceBookingContext bookingContext) throws Exception{
        JSONObject validateObject = new JSONObject();
        validateObject.put("MULTIDAY_BOOKING", ValidateSpaceBookingPolicy.policyENUM.IS_MULTIDAY_BOOKING.validate(policyContext,bookingContext));
        validateObject.put("BOOKING_ADVANCE_DAYS", ValidateSpaceBookingPolicy.policyENUM.BOOKING_ADVANCE_DAYS.validate(policyContext,bookingContext));
        validateObject.put("BOOKING_ADVANCE_HOURS", ValidateSpaceBookingPolicy.policyENUM.BOOKING_ADVANCE_HOURS.validate(policyContext,bookingContext));
        validateObject.put("MAXIMUM_ATTENDEES", ValidateSpaceBookingPolicy.policyENUM.MAXIMUM_ATTENDEES.validate(policyContext,bookingContext));

        return validateObject;
    }

    public boolean constructErrorMessage(JSONObject validateObject,V3SpaceBookingPolicyJSONContext policyContext) throws Exception
    {
        boolean finalVal = true;


        StringBuilder error = new StringBuilder();
        for (String keyStr : validateObject.keySet()) {
            Object keyvalue = validateObject.get(keyStr);
            if (keyvalue.equals(false)) {
                finalVal = false;
                if (keyStr.equals("MULTIDAY_BOOKING")) {
                    error.append("Multiday Booking is not allowed \n");
                } else if (keyStr.equals("BOOKING_ADVANCE_DAYS")) {
                    error.append("Advance Booking can be made up to " + policyContext.getBookingAdvanceDays().getValue() + " days \n");
                } else if (keyStr.equals("BOOKING_ADVANCE_HOURS")) {
                    error.append("Advance Booking can be made up to " + policyContext.getBookingAdvanceHours().getValue() + " hours \n");

                } else if (keyStr.equals("MAXIMUM_ATTENDEES")) {
                    error.append("Maximum attendees cannot be more than " + policyContext.getMaximumAttendees().getValue() + " \n");
                }
            }
        }

        if(finalVal == false) {
            String errorMsg = error.toString();
            throw new RESTException(ErrorCode.VALIDATION_ERROR, errorMsg);
        }


        return finalVal;
    }





}


