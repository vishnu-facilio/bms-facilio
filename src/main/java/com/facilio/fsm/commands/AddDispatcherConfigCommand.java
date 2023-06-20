package com.facilio.fsm.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

import static com.facilio.db.criteria.CriteriaAPI.updateConditionField;

public class AddDispatcherConfigCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        DispatcherSettingsContext dispatcherConfig = (DispatcherSettingsContext)context.get(FacilioConstants.Dispatcher.DISPATCHER_CONFIG);
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if(dispatcherConfig != null) {

            if (dispatcherConfig.getResourceCriteria() != null) {
                Criteria criteria = dispatcherConfig.getResourceCriteria();
                updateConditionField( FacilioConstants.ContextNames.PEOPLE, criteria);
                if(criteria != null) {
                    long criteriaId = CriteriaAPI.addCriteria(criteria, orgId);
                    dispatcherConfig.setResourceCriteriaId(criteriaId);
                }
            }
            GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getDispatcherModule().getTableName())
                    .fields(FieldFactory.getDispatcherFields(ModuleFactory.getDispatcherModule()));

            Map<String, Object> props = FieldUtil.getAsProperties(dispatcherConfig);
            insertBuilder.addRecord(props);
            insertBuilder.save();

            dispatcherConfig.setId((Long) props.get("id"));

        }

        return false;
    }
}
