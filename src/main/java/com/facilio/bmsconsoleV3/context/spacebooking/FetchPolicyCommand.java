package com.facilio.bmsconsoleV3.context.spacebooking;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ParkingStallContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3DeskContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class FetchPolicyCommand extends FacilioCommand {
    public boolean executeCommand(Context context) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        String module = (String) context.get(FacilioConstants.ContextNames.SpaceBooking.PARENT_MODULE_NAME);
        V3SpaceBookingContext bookingContext = (V3SpaceBookingContext) context.get(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING);
        long spaceId = (long) context.get(FacilioConstants.ContextNames.SpaceBooking.SPACE_ID);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map;

        if (module.equals(FacilioConstants.ContextNames.Floorplan.DESKS)) {
            V3DeskContext deskcontext = (V3DeskContext) context.get("dataContext");
            map = mapper.convertValue(deskcontext, new TypeReference<Map<String, Object>>() {
            });

        } else if (module.equals(FacilioConstants.ContextNames.Floorplan.PARKING)) {
            V3ParkingStallContext parkingContext = (V3ParkingStallContext) context.get("dataContext");
            map = mapper.convertValue(parkingContext, new TypeReference<Map<String, Object>>() {
            });
        } else {
            V3SpaceContext spaceContext = (V3SpaceContext) context.get("dataContext");
            map = mapper.convertValue(spaceContext, new TypeReference<Map<String, Object>>() {
            });
        }

        FacilioContext mapContext = new FacilioContext();
        mapContext.put("data", context.get("dataContext"));
        FacilioModule policyModule = modBean.getModule(FacilioConstants.ContextNames.SpaceBooking.SPACE_BOOKING_POLICY);

        List<FacilioField> fields = modBean.getAllFields(policyModule.getName());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        List<FacilioField> selectFields = Arrays.asList(fieldMap.get(FacilioConstants.ContextNames.MODULE_NAME), fieldMap.get(FacilioConstants.ContextNames.SpaceBooking.CRITERIAID), FieldFactory.getIdField(policyModule));

        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .table(policyModule.getTableName())
                .select(selectFields)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(FacilioConstants.ContextNames.MODULE_NAME), module, StringOperators.IS));


        List<Map<String, Object>> props = selectBuilder.get();
        Long criteriaId;
        List<Long> policyIds = new ArrayList<>();
        if (props != null && props.size() > 0) {
            for (Map<String, Object> prop : props) {
                criteriaId = (Long) prop.get(FacilioConstants.ContextNames.SpaceBooking.CRITERIAID);
                boolean flag = evaluateCriteria(map, mapContext, criteriaId);
                if (flag) {
                    policyIds.add((Long) prop.get("id"));

                }
            }
        }
        context.put("POLICIES",policyIds);
        return false;
    }
    public boolean evaluateCriteria (Map<String, Object> record, FacilioContext context,Long policyCriteriaId) throws Exception {

        boolean criteriaFlag = true;
        Criteria criteria;
        criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), policyCriteriaId);

        if (criteria != null && record != null) {
            criteriaFlag = criteria.computePredicate(record).evaluate(record);
        }
        return criteriaFlag;
    }
}
