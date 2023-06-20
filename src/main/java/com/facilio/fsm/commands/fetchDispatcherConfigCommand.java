package com.facilio.fsm.commands;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class fetchDispatcherConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long)context.get(FacilioConstants.Dispatcher.DISPATCHER_ID);
        if(id>0) {
            long orgId = AccountUtil.getCurrentOrg().getOrgId();
            DispatcherSettingsContext dispatcherConfig = new DispatcherSettingsContext();
            List<FacilioField> selectFields = new ArrayList<>();
            selectFields.addAll(FieldFactory.getDispatcherFields(ModuleFactory.getDispatcherModule()));
            selectFields.add(FieldFactory.getIdField(ModuleFactory.getDispatcherModule()));
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(selectFields)
                    .table(ModuleFactory.getDispatcherModule().getTableName())
                    .andCondition(CriteriaAPI.getIdCondition(id, ModuleFactory.getDispatcherModule()));
            List<Map<String, Object>> props = builder.get();

            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> prop : props) {
                    dispatcherConfig = FieldUtil.getAsBeanFromMap(prop, DispatcherSettingsContext.class);
                }
            }
            if (dispatcherConfig.getResourceCriteriaId() != -1) {
                Criteria criteria = CriteriaAPI.getCriteria(orgId, dispatcherConfig.getResourceCriteriaId());
                dispatcherConfig.setResourceCriteria(criteria);
            }
            context.put(FacilioConstants.Dispatcher.DISPATCHER_CONFIG, dispatcherConfig);
        }
        return false;
    }
}
