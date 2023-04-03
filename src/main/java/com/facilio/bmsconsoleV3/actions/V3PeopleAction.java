package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import com.facilio.v3.context.Constants;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;

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
}
