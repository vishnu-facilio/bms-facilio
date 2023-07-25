package com.facilio.bmsconsoleV3.commands;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SettingsContext;
import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SyncHistoryContext;
import com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.kafka.common.protocol.types.Field;

import java.util.List;
import java.util.Map;

public class GetSFGSyncHistoryCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioModule module = ModuleFactory.getSFG20SyncHistoryModule();
        List<FacilioField> fields = FieldFactory.getSFG20SyncHistoryFields();
        Criteria criteria = (Criteria) context.get(FacilioConstants.ContextNames.FILTER_CRITERIA);
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .orderBy("ID DESC");
        if(criteria !=null)
        {
            selectBuilder.andCriteria(criteria);
        }


         List<Map<String,Object>>props = selectBuilder.get();
        List<SFG20SyncHistoryContext> historyList = FieldUtil.getAsBeanListFromMapList(props,SFG20SyncHistoryContext.class);
        context.put(FacilioConstants.ContextNames.SFG20.SYNC_HISTORY,historyList);
        return false;
    }
}