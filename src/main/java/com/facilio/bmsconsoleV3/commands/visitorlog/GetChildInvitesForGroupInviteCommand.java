package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsoleV3.context.GroupInviteContextV3;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.RecurringInviteVisitorContextV3;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class GetChildInvitesForGroupInviteCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<GroupInviteContextV3> list = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(list) && list.get(0) != null) {       	
        	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.INVITE_VISITOR);
    		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(module.getName()));

    		SelectRecordsBuilder<InviteVisitorContextV3> builder = new SelectRecordsBuilder<InviteVisitorContextV3>()
    				.beanClass(InviteVisitorContextV3.class)
    				.module(module)
    				.select(modBean.getAllFields(module.getName()))
    				.andCondition(CriteriaAPI.getCondition(fieldMap.get("groupId"), ""+list.get(0).getId(), NumberOperators.EQUALS));
    		List<InviteVisitorContextV3> childInvites = builder.get();
			long checkedIn=0;
			for(InviteVisitorContextV3 invite : childInvites)
			{
				if(invite.hasCheckedIn())
				{
					checkedIn++;
				}
			}
    		if (childInvites != null && !childInvites.isEmpty()) {			
    			list.get(0).setGroupChildInvites(childInvites);
				list.get(0).setTotalInvites((long) childInvites.size());
				list.get(0).setCheckedInCount(checkedIn);
				list.get(0).setNotCheckedInCount(childInvites.size()-checkedIn);
    		}
        }
        return false;
    }
}