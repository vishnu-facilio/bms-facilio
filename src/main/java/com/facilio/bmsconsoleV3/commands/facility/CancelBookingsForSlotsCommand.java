package com.facilio.bmsconsoleV3.commands.facility;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.facilitybooking.BookingSlotsContext;
import com.facilio.bmsconsoleV3.context.facilitybooking.V3FacilityBookingContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class CancelBookingsForSlotsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = Constants.getRecordIds(context);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FacilityBooking.BOOKING_SLOTS);

        if(CollectionUtils.isNotEmpty(recordIds)) {
            SelectRecordsBuilder<BookingSlotsContext> builder = new SelectRecordsBuilder<BookingSlotsContext>()
                    .module(module)
                    .beanClass(BookingSlotsContext.class)
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("SLOT_ID", "slot", StringUtils.join(recordIds, ","), NumberOperators.EQUALS))
                    ;

            List<BookingSlotsContext> records = builder.get();
            if(CollectionUtils.isNotEmpty(records)) {
                Collection<JSONObject> jsonList = new ArrayList<>();
                for(BookingSlotsContext bs : records) {
                    V3FacilityBookingContext booking = (V3FacilityBookingContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING, bs.getBooking().getId(), V3FacilityBookingContext.class);
                    booking.setIsCancelled(true);
                    Map<String, Object> map = FieldUtil.getAsProperties(booking);
                    map.put("isCancelled", true);
                    JSONObject json = new JSONObject();
                    json.putAll(map);
                    jsonList.add(json);
                }

                if(CollectionUtils.isNotEmpty(jsonList)) {
                    FacilioChain patchChain = ChainUtil.getBulkPatchChain(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
                    FacilioContext patchContext = patchChain.getContext();
                    V3Config v3Config = ChainUtil.getV3Config(FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
                    Class beanClass = ChainUtil.getBeanClass(v3Config, module);

                    Constants.setModuleName(patchContext, FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING);
                    Constants.setBulkRawInput(patchContext, (Collection) jsonList);

                    //to avoid validations
                    JSONObject json = new JSONObject();
                    json.put("cancel", true);
                    Constants.setBodyParams(patchContext, json);

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
