package com.facilio.apiv3.sample;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

//Handle things before you save here, things like validation, or changes in the
//bean happen here
public class SampleBeforeSaveCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(Constants.MODULE_NAME);

        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

        List list = recordMap.get(moduleName);
        ModuleBaseWithCustomFields testContext = (ModuleBaseWithCustomFields) list.get(0);

        String name = ((CustomModuleData) testContext).getName();
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name is mandatory");
        }

        name = name.trim();
        ((CustomModuleData) testContext).setName(name);
        
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = modBean.getAllFields(moduleName);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
                .select(Collections.singleton(FieldFactory.getIdField(module)))
                .moduleName(moduleName)
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), name, StringOperators.IS))
                .beanClass(ModuleBaseWithCustomFields.class);

        List<ModuleBaseWithCustomFields> duplicates = selectRecordsBuilder.get();

        if (!CollectionUtils.isEmpty(duplicates)) {
            throw new IllegalArgumentException("name already exists");
        }

        return false;
    }
}
