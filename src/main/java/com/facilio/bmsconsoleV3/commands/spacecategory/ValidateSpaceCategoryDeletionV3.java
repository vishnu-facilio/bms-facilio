package com.facilio.bmsconsoleV3.commands.spacecategory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class ValidateSpaceCategoryDeletionV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayList<Long> recordIDs = (ArrayList<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        long recordID = recordIDs.get(0);
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String spaceModuleName = FacilioConstants.ContextNames.SPACE;
        FacilioModule module = modBean.getModule(spaceModuleName);
        List<FacilioField> fields = modBean.getAllFields(spaceModuleName);
        SelectRecordsBuilder<SpaceContext> selectRecordsBuilder = new SelectRecordsBuilder<SpaceContext>()
                .module(module)
                .beanClass(SpaceContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("SPACE_CATEGORY_ID", "spaceCategoryId", String.valueOf(recordID), NumberOperators.EQUALS));
        List<SpaceContext> result = selectRecordsBuilder.get();

        boolean stopChain = false;
        if (!result.isEmpty()) {
            stopChain = true;
        }

        if(stopChain){
            StringBuilder builder = new StringBuilder("This Space Category is already being used in existing spaces");
            throw new RESTException(ErrorCode.VALIDATION_ERROR, builder.toString());
        }

        return stopChain;
    }
}
