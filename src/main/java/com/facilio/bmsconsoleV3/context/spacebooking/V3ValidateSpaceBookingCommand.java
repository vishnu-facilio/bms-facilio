package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class V3ValidateSpaceBookingCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule spaceBookingModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        List<FacilioField> fields = modBean.getAllFields(spaceBookingModule.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> selectFields = Arrays.asList(fieldMap.get("bookingStartTime"), fieldMap.get("bookingEndTime"),fieldMap.get("space"));

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3SpaceBookingContext> spaceBooking = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(spaceBooking)) {

            Map<String, Object> bodyParams = Constants.getBodyParams(context);

            Long startTime = null;
            Long endTime = null;
            long spaceId = -1;
            for (V3SpaceBookingContext booking : spaceBooking) {

                 startTime = booking.getBookingStartTime();
                 endTime = booking.getBookingEndTime();
                spaceId = booking.getSpace().getId();
            }



            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .table(modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING).getTableName())
                    .select(selectFields)
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("space"), String.valueOf(spaceId), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", "false", BooleanOperators.IS));


            List<Map<String, Object>> props = selectBuilder.get();

            for(Map<String, Object> prop : props) { 
                Long bookingEndTime = (Long)prop.get("bookingEndTime");
                Long bookingStartTime = (Long)prop.get("bookingStartTime");
                // Check selected slot not overlaps the existing booked slots
                if(((startTime <bookingStartTime && endTime <= bookingStartTime) ||(startTime>=bookingEndTime && endTime> bookingEndTime) || ( (startTime > bookingStartTime && startTime >bookingEndTime ) && (endTime > bookingStartTime && endTime >bookingEndTime ))|| ((startTime <bookingStartTime && startTime <bookingEndTime) &&(endTime <bookingStartTime && endTime <bookingEndTime))) && (endTime >startTime)) {
                // Selected Booking Range
                 }


            else{
                   throw new RESTException(ErrorCode.VALIDATION_ERROR, "Parallel booking is not allowed for this space  ");
                }
            }

        }

        return false;
    } }
