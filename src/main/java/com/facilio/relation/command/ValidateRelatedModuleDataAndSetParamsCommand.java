package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.relation.context.RelationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants.*;
import com.facilio.relation.util.RelationUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.relation.context.RelationRequestContext;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.RelationshipOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidateRelatedModuleDataAndSetParamsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String relatedFieldName = (String) context.get(ContextNames.RELATED_FIELD_NAME);
        String relatedModuleName = (String) context.get(ContextNames.RELATED_MODULE_NAME);

        if(StringUtils.isNotEmpty(relatedFieldName)) {
            ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule relatedModule = moduleBean.getModule(relatedFieldName);

            if(relatedModule == null) {
                throw new IllegalArgumentException("Invalid Related Field Name");
            }

            relatedModuleName = relatedModule.getName();
        }

        if(StringUtils.isEmpty(relatedModuleName)) {
            throw new IllegalArgumentException("Related Module Name cannot be null");
        }

        if (StringUtils.isEmpty(relatedFieldName)) {
            throw new IllegalArgumentException("Related Field Name cannot be null");
        }
        context.put(ContextNames.MODULE_NAME, relatedModuleName);
        return false;
    }

}
