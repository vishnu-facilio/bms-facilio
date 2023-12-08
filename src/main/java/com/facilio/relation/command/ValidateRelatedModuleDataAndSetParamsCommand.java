package com.facilio.relation.command;

import com.facilio.beans.ModuleBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
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
        String extendedModuleName = (String) context.get(ContextNames.EXTENDED_MODULE_NAME);
        Boolean isFromDashboard = (Boolean) context.get(ContextNames.IS_FROM_DASHBOARD);
        if(StringUtils.isEmpty(relatedModuleName)) {
            throw new IllegalArgumentException("Related Module Name cannot be null");
        }
        /**
         * below code is used for handling specail case for dashboard , So don't remove this
         */
        if(isFromDashboard != null && isFromDashboard){
            context.put(ContextNames.MODULE_NAME, relatedModuleName);
            return false;
        }

        if ( isFromDashboard == null && StringUtils.isEmpty(relatedFieldName)) {
            throw new IllegalArgumentException("Related Field Name cannot be null");
        }

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioField relatedField = moduleBean.getField(relatedFieldName, relatedModuleName);

        if(relatedField == null){
            throw new IllegalArgumentException("Invalid related field");
        }

        String lookupModuleName = ((BaseLookupField) relatedField).getLookupModule().getName();
        if(StringUtils.isNotEmpty(extendedModuleName)) {
            FacilioModule relatedModule = moduleBean.getModule(extendedModuleName);
            boolean flag = false;
            while (relatedModule != null) {
                String extendedName = relatedModule.getName();
                if (extendedName.equals(lookupModuleName)) {
                    flag = true;
                    break;
                }
                relatedModule = relatedModule.getExtendModule();
            }

            if (!flag) {
                throw new IllegalArgumentException("Invalid extended module name");
            }
        }

        context.put(ContextNames.MODULE_NAME, lookupModuleName);

        return false;
    }

}
