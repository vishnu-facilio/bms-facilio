package com.facilio.emailtemplate.command;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.emailtemplate.context.EMailStructure;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class GetAllEmailTemplatesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Boolean fetchAll = (Boolean) context.get(FacilioConstants.ContextNames.FETCH_ALL);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(FieldFactory.getTemplateFields());
        fields.addAll(FieldFactory.getEMailStructureFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getEMailStructureModule().getTableName())
                .innerJoin(ModuleFactory.getTemplatesModule().getTableName())
                    .on(ModuleFactory.getEMailStructureModule().getTableName() + ".ID = " + ModuleFactory.getTemplatesModule().getTableName() + ".ID")
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        if (fetchAll == null || fetchAll.equals(false)){
            builder.andCondition(CriteriaAPI.getCondition("DRAFT","draft", String.valueOf(false), BooleanOperators.IS));
        }

        List<EMailStructure> emailTemplates = FieldUtil.getAsBeanListFromMapList(builder.get(), EMailStructure.class);

        context.put(FacilioConstants.ContextNames.EMAIL_STRUCTURES, emailTemplates);
        return false;
    }
}
