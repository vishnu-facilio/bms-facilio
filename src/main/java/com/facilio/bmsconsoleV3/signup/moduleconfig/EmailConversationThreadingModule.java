package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;

import java.util.*;

public class EmailConversationThreadingModule extends BaseModuleConfig{
    public EmailConversationThreadingModule(){
        setModuleName(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> emailConversationThreading = new ArrayList<FacilioView>();
        emailConversationThreading.add(getAllEmailConversationThreadingViews().setOrder(order++));

        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
        groupDetails.put("views", emailConversationThreading);
        groupVsViews.add(groupDetails);

        return groupVsViews;
    }

    private static FacilioView getAllEmailConversationThreadingViews() {

        List<SortField> sortFields = Arrays.asList(new SortField(FieldFactory.getField("id", "Email_Conversation_Threading.ID", FieldType.NUMBER), false));

        FacilioView allView = new FacilioView();
        allView.setName("all");
        allView.setDisplayName("All Email Conversation");
        allView.setModuleName(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME);
        allView.setSortFields(sortFields);

        List<AppDomain.AppDomainType> appDomains = new ArrayList<>();
        appDomains.add(AppDomain.AppDomainType.FACILIO);

        return allView;
    }
}
