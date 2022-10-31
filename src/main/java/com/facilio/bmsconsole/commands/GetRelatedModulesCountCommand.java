package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ModuleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class GetRelatedModulesCountCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        Integer moduleType = (Integer) context.get(FacilioConstants.ContextNames.MODULE_TYPE);
        String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);

        if (moduleType == null || moduleType <= 0) {
            moduleType = FacilioModule.ModuleType.BASE_ENTITY.getValue();
        }

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule parentModule = modBean.getModule(moduleName);

        long count = ModuleAPI.getRelatedModulesCount(parentModule, moduleType, searchString);

        context.put(FacilioConstants.ContextNames.COUNT, count);
        return false;
    }

    private String getTypes(FacilioModule.ModuleType... types) {
        StringJoiner joiner = new StringJoiner(",");
        for (FacilioModule.ModuleType type : types) {
            joiner.add(String.valueOf(type.getValue()));
        }
        return joiner.toString();
    }
}
