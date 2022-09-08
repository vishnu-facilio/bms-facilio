package com.facilio.bmsconsoleV3.commands.visitorlog;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.GroupInviteContextV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.vendorquotes.V3VendorQuotesContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class FetchVisitorTypeForGroupInvites extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));
        List<GroupInviteContextV3> groupInvitesList = Constants.getRecordList((FacilioContext) context);
        try
        {
        for(GroupInviteContextV3 groupInvite : groupInvitesList) {
            SelectRecordsBuilder<InviteVisitorContextV3> builder = new SelectRecordsBuilder<InviteVisitorContextV3>()
                    .beanClass(InviteVisitorContextV3.class)
                    .module(module)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("groupId"), "" + groupInvite.getId(), NumberOperators.EQUALS));
            InviteVisitorContextV3 childInvite = builder.get().get(0);
            if (childInvite != null) {
                groupInvite.setVisitorTypeId(childInvite.getVisitorType().getId());
            }
        }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }
}

