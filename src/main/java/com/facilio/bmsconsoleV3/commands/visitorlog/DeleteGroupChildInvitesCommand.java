package com.facilio.bmsconsoleV3.commands.visitorlog;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.GroupInviteContextV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
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
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.facilio.apiv3.APIv3Config.getInviteVisitor;

public class DeleteGroupChildInvitesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        ArrayList recordMap = (ArrayList) context.get(Constants.RECORD_ID_LIST);
        long groupId= (long) recordMap.get(0);


            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

            SelectRecordsBuilder<InviteVisitorContextV3> builder = new SelectRecordsBuilder<InviteVisitorContextV3>()
                    .beanClass(InviteVisitorContextV3.class)
                    .module(module)
                    .select(modBean.getAllFields(module.getName()))
                    .andCondition(CriteriaAPI.getCondition(fieldMap.get("groupId"), "" + groupId, NumberOperators.EQUALS));
            List<InviteVisitorContextV3> childInvites = builder.get();
            ArrayList<Long> inviteIds = new ArrayList<>();
            for(InviteVisitorContextV3 invites: childInvites)
            {
                inviteIds.add(invites.getId());
            }
            V3RecordAPI.deleteRecordsById(FacilioConstants.ContextNames.INVITE_VISITOR,inviteIds);
        return false;
    }
}

