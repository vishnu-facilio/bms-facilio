package com.facilio.fsm.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.fsm.context.DispatcherSettingsContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

import java.util.Map;

import static com.facilio.db.criteria.CriteriaAPI.updateConditionField;

public class UpdateDispatcherConfigCommand extends FacilioCommand {
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

            Map<String, Object> prop = FieldUtil.getAsProperties(dispatcherConfig);

            GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                    .table(ModuleFactory.getDispatcherModule().getTableName())
                    .fields(FieldFactory.getDispatcherFields(ModuleFactory.getDispatcherModule()))
                    .andCondition(CriteriaAPI.getIdCondition(dispatcherConfig.getId(), ModuleFactory.getDispatcherModule()));
            updateBuilder.update(prop);
        }
        return false;
    }
}
