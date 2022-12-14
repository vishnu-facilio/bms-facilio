package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.TicketAPI;
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
import com.facilio.modules.FacilioStatus;
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

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule spaceBookingModule = modBean.getModule(FacilioConstants.ContextNames.SPACE_BOOKING);
        List<FacilioField> fields = modBean.getAllFields(spaceBookingModule.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> selectFields = Arrays.asList(fieldMap.get(FacilioConstants.ContextNames.SpaceBooking.BOOKING_START_TIME), fieldMap.get(FacilioConstants.ContextNames.SpaceBooking.BOOKING_END_TIME),fieldMap.get(FacilioConstants.ContextNames.SpaceBooking.SPACE_ID),FieldFactory.getIdField(spaceBookingModule));

        Long startTime = (Long) context.get(FacilioConstants.ContextNames.SpaceBooking.BOOKING_START_TIME);
        Long endTime = (Long) context.get(FacilioConstants.ContextNames.SpaceBooking.BOOKING_END_TIME);
        Long currentTime = (Long) context.get(FacilioConstants.ContextNames.SpaceBooking.CURRENT_TIME);
        long spaceId = (Long) context.get(FacilioConstants.ContextNames.SpaceBooking.SPACE_ID);
        FacilioStatus status = TicketAPI.getStatus(spaceBookingModule, "cancelled");
        long cancelledStateId = status.getId();

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(spaceBookingModule.getTableName())
                .select(selectFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.SpaceBooking.SPACE_ID), String.valueOf(spaceId), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("SYS_DELETED", "sysDeleted", "false", BooleanOperators.IS))
                .andCondition(CriteriaAPI.getCondition(spaceBookingModule.getTableName() + ".MODULE_STATE", "moduleState", String.valueOf(cancelledStateId), NumberOperators.NOT_EQUALS));
        if(context.get("recordId")!=null){
            long recordId = (Long) context.get("recordId");
            selectBuilder.andCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(recordId),NumberOperators.NOT_EQUALS));
        }

        List<Map<String, Object>> props = selectBuilder.get();
                if (props != null) {
                    for (Map<String, Object> prop : props) {
                        Long bookingEndTime = (Long) prop.get(FacilioConstants.ContextNames.SpaceBooking.BOOKING_END_TIME);
                        Long bookingStartTime = (Long) prop.get(FacilioConstants.ContextNames.SpaceBooking.BOOKING_START_TIME);
                        // Check selected slot not overlaps the existing booked slots
                        if(startTime>=currentTime) {

                            if (((startTime < bookingStartTime && endTime <= bookingStartTime) || (startTime >= bookingEndTime && endTime > bookingEndTime) || ((startTime > bookingStartTime && startTime > bookingEndTime) && (endTime > bookingStartTime && endTime > bookingEndTime)) || ((startTime < bookingStartTime && startTime < bookingEndTime) && (endTime < bookingStartTime && endTime < bookingEndTime))) ) {
                                if(endTime > startTime){
                                    // Selected Booking Range
                                }
                                else{
                                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please select valid time range");

                                }
                            } else {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Parallel booking is not allowed for this space");
                            }
                        }
                        else{
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Please select valid upcoming time range");

                        }
                    }
                    }


        return false;
    } }
