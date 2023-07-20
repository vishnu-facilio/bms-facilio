package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.ReadOnlyChainFactoryV3;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.attendance.Attendance;
import com.facilio.bmsconsoleV3.context.shift.ShiftSlot;
import com.facilio.bmsconsoleV3.util.AttendanceAPI;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class V3PeopleAction extends V3Action {

    Long roleId;
    String appLinkName;
    Long securityPolicyId;
    Long lookupId;
    Integer peopleType;
    List<Long> peopleIds;
    String email;
    Long userId;
    String password;
    boolean sendInvitation;

    public String fetchActivePeoples() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.fetchActivePeopleChain();
        FacilioContext context = chain.getContext();
        chain.execute();

        setData((JSONObject) context.get(FacilioConstants.ContextNames.DATA));
        return SUCCESS;
    }

    public String fetchAppListForPeople() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.fetchAppListForPeopleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID,getId());
        chain.execute();

        setData((JSONObject) context.get(FacilioConstants.ContextNames.DATA));
        return SUCCESS;
    }

    public String addAppAccess() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.addAppAccessForPeopleChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.PEOPLE_ID,getId());
        context.put(FacilioConstants.ContextNames.ROLE_ID,getRoleId());
        context.put(FacilioConstants.ContextNames.APP_LINKNAME,getAppLinkName());
        context.put(FacilioConstants.ContextNames.SECURITY_POLICY_ID,getSecurityPolicyId());
        context.put(FacilioConstants.ContextNames.EMAIL,getEmail());
        context.put(FacilioConstants.ContextNames.PASSWORD,getPassword());
        context.put(FacilioConstants.ContextNames.SEND_INVITE,isSendInvitation());
        chain.execute();
        return SUCCESS;
    }

    public String convertPeopleType() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.convertPeopleTypeChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,getPeopleIds());
        context.put(FacilioConstants.ContextNames.PEOPLE_TYPE,getPeopleType());
        context.put("lookupId",getLookupId());
        chain.execute();
        return SUCCESS;
    }
    public String updateAppAccess() throws Exception{
        FacilioChain chain=TransactionChainFactoryV3.updateAppAccessChain();
        FacilioContext context=chain.getContext();
        context.put(FacilioConstants.ContextNames.PEOPLE_ID,getId());
        context.put(FacilioConstants.ContextNames.ROLE_ID,getRoleId());
        context.put(FacilioConstants.ContextNames.APP_LINKNAME,getAppLinkName());
        context.put(FacilioConstants.ContextNames.SECURITY_POLICY_ID,getSecurityPolicyId());
        context.put(FacilioConstants.ContextNames.USER_ID,getUserId());
        chain.execute();
        return SUCCESS;
    }
    public String getShiftDetailsForCurrentDay() throws Exception {
        if(getId() > 0){
            long currentTime = DateTimeUtil.getCurrenTime();
            List<ShiftSlot> shift = ShiftAPI.fetchShiftSlots(Collections.singletonList(getId()),DateTimeUtil.getDayStartTimeOf(currentTime),DateTimeUtil.getDayEndTimeOf(currentTime));
            Attendance attendance = AttendanceAPI.getAttendanceForToday(getId());
            JSONObject result = new JSONObject();
            result.put(FacilioConstants.ContextNames.SHIFT,shift);
            result.put(FacilioConstants.ContextNames.ATTENDANCE,attendance);
            setData(result);
        }
        return SUCCESS;
    }
}
