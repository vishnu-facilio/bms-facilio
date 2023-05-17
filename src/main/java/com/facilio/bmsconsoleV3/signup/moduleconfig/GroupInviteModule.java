package com.facilio.bmsconsoleV3.signup.moduleconfig;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.fields.FacilioField;

import java.util.*;

public class GroupInviteModule extends BaseModuleConfig{
    public GroupInviteModule(){
        setModuleName(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE);
    }

    @Override
    public List<Map<String, Object>> getViewsAndGroups() throws Exception {
        List<Map<String, Object>> groupVsViews = new ArrayList<>();
        Map<String, Object> groupDetails;

        int order = 1;
        ArrayList<FacilioView> groupInvite = new ArrayList<FacilioView>();
        groupInvite.add(getAllGroupInvitesView().setOrder(order));


        groupDetails = new HashMap<>();
        groupDetails.put("name", "systemviews");
        groupDetails.put("displayName", "System Views");
        groupDetails.put("moduleName", FacilioConstants.ContextNames.GROUP_VISITOR_INVITE);
        groupDetails.put("views", groupInvite);
        groupVsViews.add(groupDetails);
        return groupVsViews;
    }
    private static FacilioView getAllGroupInvitesView() throws Exception {

        FacilioView allView = new FacilioView();
        allView.setName("group_invite_all");
        allView.setDisplayName("All Group Invites");

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule groupInviteModule = modBean.getModule(FacilioConstants.ContextNames.GROUP_VISITOR_INVITE);
        FacilioField name = FieldFactory.getField("name", "NAME", groupInviteModule, FieldType.STRING);
        List<String> appLinkNames = new ArrayList<>();
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.MAINTENANCE_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.IWMS_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
        appLinkNames.add(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
        allView.setAppLinkNames(appLinkNames);

        allView.setSortFields(Arrays.asList(new SortField(name, true)));

        return allView;
    }
}
