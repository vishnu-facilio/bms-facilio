package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SummaryCommand extends FacilioCommand {
    private FacilioModule module;

    public SummaryCommand(FacilioModule module) {
        this.module = module;
    }

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> recordIds = Constants.getRecordIds(context);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = module.getName();
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        Class beanClass = (Class) context.get(Constants.BEAN_CLASS);

        SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
                .select(fields)
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(recordIds, module))
                .beanClass(beanClass);

        List<SupplementRecord> supplementFields = (List<SupplementRecord>) context.get(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS);
        if (CollectionUtils.isNotEmpty(supplementFields)) {
            selectRecordsBuilder.fetchSupplements(supplementFields);
        }

        List<ModuleBaseWithCustomFields> list = selectRecordsBuilder.get();

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = new HashMap<>();
        recordMap.put(moduleName, list);

        context.put(Constants.RECORD_MAP, recordMap);

        return false;
    }
}
