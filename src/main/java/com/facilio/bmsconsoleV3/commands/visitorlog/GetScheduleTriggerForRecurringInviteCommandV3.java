package com.facilio.bmsconsoleV3.commands.visitorlog;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsoleV3.context.RecurringInviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;

public class GetScheduleTriggerForRecurringInviteCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<RecurringInviteVisitorContextV3> list = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(list) && list.get(0) != null) {
        	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
    	            .table(ModuleFactory.getBaseSchedulerModule().getTableName())
    	            .select(FieldFactory.getBaseSchedulerFields())
    	            .andCondition(CriteriaAPI.getIdCondition(list.get(0).getScheduleId(), ModuleFactory.getBaseSchedulerModule()));
        	
        	List<Map<String, Object>> props = selectBuilder.get();
    		if (props != null && !props.isEmpty()) {			
    			BaseScheduleContext scheduleTrigger = FieldUtil.getAsBeanFromMap(props.get(0), BaseScheduleContext.class);
    			list.get(0).setScheduleTrigger(scheduleTrigger);
    		}
        }
        return false;
    }
}
