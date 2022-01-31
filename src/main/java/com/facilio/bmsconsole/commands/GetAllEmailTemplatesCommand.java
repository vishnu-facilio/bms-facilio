package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetAllEmailTemplatesCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        if (module == null) {
            throw new IllegalArgumentException("Invalid module");
        }

        List<FacilioField> fields = new ArrayList<>();
        fields.addAll(FieldFactory.getTemplateFields());
        fields.addAll(FieldFactory.getEMailTemplateFields());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getEMailTemplatesModule().getTableName())
                .innerJoin(ModuleFactory.getTemplatesModule().getTableName())
                    .on(ModuleFactory.getEMailTemplatesModule().getTableName() + ".ID = " + ModuleFactory.getTemplatesModule().getTableName() + ".ID")
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

        List<EMailTemplate> emailTemplates = FieldUtil.getAsBeanListFromMapList(builder.get(), EMailTemplate.class);

        if (CollectionUtils.isNotEmpty(emailTemplates)) {
            TemplateAPI.fillEmailTemplate(emailTemplates);
        }

        context.put(FacilioConstants.ContextNames.EMAIL_TEMPLATES, emailTemplates);
        return false;
    }
}
