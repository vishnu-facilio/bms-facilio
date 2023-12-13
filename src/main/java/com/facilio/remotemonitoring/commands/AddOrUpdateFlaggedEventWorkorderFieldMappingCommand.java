package com.facilio.remotemonitoring.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.context.FlaggedEventRuleContext;
import com.facilio.remotemonitoring.context.FlaggedEventWorkorderFieldMappingContext;
import com.facilio.remotemonitoring.signup.FlaggedEventRuleModule;
import com.facilio.remotemonitoring.utils.RemoteMonitorUtils;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddOrUpdateFlaggedEventWorkorderFieldMappingCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<FlaggedEventRuleContext> flaggedEventRules = (List<FlaggedEventRuleContext>) recordMap.get(FlaggedEventRuleModule.MODULE_NAME);
        List<Map<String,Object>> fieldsMapList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(flaggedEventRules)) {
            for(FlaggedEventRuleContext flaggedEventRule : flaggedEventRules) {
                if(CollectionUtils.isNotEmpty(flaggedEventRule.getFieldMapping())) {
                    String ticketModuleName = RemoteMonitorUtils.getTicketModuleName(flaggedEventRule);
                    for(FlaggedEventWorkorderFieldMappingContext fieldMap : flaggedEventRule.getFieldMapping()) {
                        deleteFieldMappings(flaggedEventRule.getId());
                        FacilioField woField = modBean.getField(fieldMap.getLeftFieldName(), ticketModuleName);
                        fieldMap.setLeftFieldId(woField.getId());
                        fieldMap.setParentId(flaggedEventRule.getId());
                        Map<String,Object> prop = FieldUtil.getAsProperties(fieldMap);
                        fieldsMapList.add(prop);
                    }
                }
            }
        }
        if(CollectionUtils.isNotEmpty(fieldsMapList)) {
            GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                    .fields(FieldFactory.getFlaggedEventWorkorderTemplateFieldMappingField())
                    .table(ModuleFactory.getflaggedEventWorkorderTemplateFieldMappingModule().getTableName())
                    .addRecords(fieldsMapList);
            insertRecordBuilder.save();
        }
        return false;
    }

    private static void deleteFieldMappings(Long parentId) throws Exception {
        GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getflaggedEventWorkorderTemplateFieldMappingModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId",String.valueOf(parentId),NumberOperators.EQUALS));
        deleteRecordBuilder.delete();
    }
}