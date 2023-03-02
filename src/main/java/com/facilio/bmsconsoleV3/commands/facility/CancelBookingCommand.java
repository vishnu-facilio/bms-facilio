package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.SlotContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.FacilityAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CancelBookingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        String moduleName = Constants.getModuleName(context);
        if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("cancelBooking")) {
            if((Boolean)bodyParams.get("cancelBooking") == true && !bodyParams.containsKey("cancel")) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
                FacilioModule slotModule = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.SLOTS);

                Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
                List<V3FacilityBookingContext> bookings = recordMap.get(moduleName);
                Collection<JSONObject> jsonList = new ArrayList<>();

                for(V3FacilityBookingContext booking : bookings) {
                    Map<String, Object> map = FieldUtil.getAsProperties(booking);
                    map.put("isCancelled", true);
                    JSONObject json = new JSONObject();
                    json.putAll(map);
                    jsonList.add(json);

                    //delete booking & slots rel
                   List<BookingSlotsContext> bookingSlots  = FacilityAPI.getBookingSlots(booking.getId());
                   if(CollectionUtils.isNotEmpty(bookingSlots)) {
                       for(BookingSlotsContext bookingSlot : bookingSlots) {
                           SlotContext slot = (SlotContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.SLOTS, bookingSlot.getSlot().getId(), SlotContext.class);
                           if (slot != null && slot.getBookingCount() !=null && booking.getNoOfAttendees()!=null) {
                               //reverting booking count in slot
                               slot.setBookingCount(slot.getBookingCount() - booking.getNoOfAttendees());
                               V3RecordAPI.updateRecord(slot, slotModule, modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.SLOTS));
                           }
                       }
//                       DeleteRecordBuilder<BookingSlotsContext> deleteBuilder = new DeleteRecordBuilder<BookingSlotsContext>()
//                               .module(module)
//                               .andCondition(CriteriaAPI.getCondition("FACILITY_BOOKING", "booking", String.valueOf(booking.getId()), NumberOperators.EQUALS));
//                       deleteBuilder.delete();
                   }
                }

                if(CollectionUtils.isNotEmpty(jsonList)) {
                    FacilioChain patchChain = ChainUtil.getBulkPatchChain(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
                    FacilioContext patchContext = patchChain.getContext();
                    V3Config v3Config = ChainUtil.getV3Config(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
                    Class beanClass = ChainUtil.getBeanClass(v3Config, module);

                    //to avoid validations
                    JSONObject json = new JSONObject();
                    json.put("cancel", true);
                    Constants.setBodyParams(patchContext, json);

                    Constants.setModuleName(patchContext, FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
                    Constants.setBulkRawInput(patchContext, (Collection) jsonList);
                    patchContext.put(Constants.BEAN_CLASS, beanClass);
                    patchContext.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.EDIT);
                    patchContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_WRITE);
                    patchChain.execute();
                }
            }

        }

        return false;
    }
}
