package com.facilio.v3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

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
        long id = (long) context.get(Constants.RECORD_ID);

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = module.getName();
        List<FacilioField> fields = modBean.getAllFields(moduleName);

        SelectRecordsBuilder selectRecordsBuilder = new SelectRecordsBuilder()
                .select(fields)
                .module(module)
                .andCondition(CriteriaAPI.getIdCondition(id, module));

        List asProps = selectRecordsBuilder.getAsProps();

        Map<String, List> recordMap = new HashMap<>();
        recordMap.put(moduleName, asProps);

        context.put(Constants.RECORD_MAP, recordMap);

        return false;
    }
}
