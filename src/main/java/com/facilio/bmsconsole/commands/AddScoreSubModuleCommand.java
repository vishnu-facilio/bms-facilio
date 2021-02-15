package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.scoringrule.ScoringRuleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

public class AddScoreSubModuleCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String tableName = (String) context.get(FacilioConstants.ContextNames.TABLE_NAME);
        if (StringUtils.isEmpty(tableName)) {
            throw new IllegalArgumentException("Invalid table name");
        }

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(tableName)
                .select(Collections.singletonList(FieldFactory.getIdField()));
        try {
            builder.get();
        } catch (Exception ex) {
            throw new IllegalArgumentException(tableName + " is not found");
        }


        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        FacilioModule existingScoreModule = ScoringRuleAPI.getScoreModule(module.getModuleId(), false);
        if (existingScoreModule != null) {
            throw new IllegalArgumentException("Scoring module is already configured");
        }

        FacilioModule scoreModule = new FacilioModule();
        scoreModule.setName(moduleName + "Score");
        scoreModule.setDisplayName(module.getDisplayName() + " Score");
        scoreModule.setType(FacilioModule.ModuleType.SCORE);
        scoreModule.setTableName(tableName);
        long moduleId = modBean.addModule(scoreModule);
        scoreModule.setModuleId(moduleId);

        LookupField parentLookupField = (LookupField) FieldFactory.getField("parent", "Parent", "PARENT_ID", scoreModule, FieldType.LOOKUP);
        parentLookupField.setMainField(true);
        parentLookupField.setDefault(true);
        parentLookupField.setLookupModule(module);
        modBean.addField(parentLookupField);

        FacilioField scoreField = FieldFactory.getField("score", "Score", "SCORE", scoreModule, FieldType.DECIMAL);
        scoreField.setDefault(true);
        modBean.addField(scoreField);

        FacilioField scoreRuleField = FieldFactory.getField("scoreRuleId", "Score Rule Id", "SCORE_RULE_ID", scoreModule, FieldType.NUMBER);
        scoreRuleField.setDefault(true);
        modBean.addField(scoreRuleField);

        FacilioField createdTimeField = FieldFactory.getField("createdTime", "Created Time", "CREATED_TIME", scoreModule, FieldType.DATE_TIME);
        createdTimeField.setDefault(true);
        modBean.addField(createdTimeField);

        return false;
    }
}
