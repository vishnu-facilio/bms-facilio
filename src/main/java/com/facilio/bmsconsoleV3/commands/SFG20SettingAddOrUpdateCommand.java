package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.SFG20JobPlan.SFG20SettingsContext;
import com.facilio.bmsconsoleV3.util.SFG20JobPlanAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.weekends.WeekendContext;
import org.apache.commons.chain.Context;

import java.util.Map;

public class SFG20SettingAddOrUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        SFG20SettingsContext sfg20SettingsContext = (SFG20SettingsContext) context.get(FacilioConstants.ContextNames.SFG20.SETTING);
        FacilioModule sfg20SettingModule = ModuleFactory.getSFG20SettingModule();
        long id = sfg20SettingsContext.getId();
        Map<String, Object> props = FieldUtil.getAsProperties(sfg20SettingsContext);
        if(sfg20SettingsContext.getCustomerSecret().isEmpty() || sfg20SettingsContext.getCustomerKey().isEmpty())
        {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Client Key And Secret key are Mandatory");
        }
       if(SFG20JobPlanAPI.getSFG20ClientAccessToken(sfg20SettingsContext).containsKey("error"))
       {
           throw new RESTException(ErrorCode.VALIDATION_ERROR, "Customer key or Customer secret is not valid");
       }
        if(id > 0)
        {
            GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(sfg20SettingModule.getTableName())
                    .fields(FieldFactory.getSFG20SettingsFields())
                    .andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(sfg20SettingsContext.getId()), NumberOperators.EQUALS));
            updateRecordBuilder.update(props);
        }
        else
        {
            GenericInsertRecordBuilder insert = new GenericInsertRecordBuilder()
                    .table(sfg20SettingModule.getTableName())
                    .fields(FieldFactory.getSFG20SettingsFields());
            id = insert.insert(props);
        }
        sfg20SettingsContext.setId(id);
        context.put(FacilioConstants.ContextNames.SFG20.SETTING,sfg20SettingsContext);
        return false;
    }
}