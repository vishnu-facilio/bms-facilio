package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class DeleteApplicationRelatedAppsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Long> relatedApplicationIds = (List<Long>) context.get(FacilioConstants.ContextNames.APPLICATION_RELATED_APPS_LIST);
        Long appId = (Long) context.get(FacilioConstants.ContextNames.APPLICATION_ID);
        FacilioUtil.throwIllegalArgumentException(appId<=0||appId==null,"Application id cannot be empty");
        FacilioUtil.throwIllegalArgumentException(CollectionUtils.isEmpty(relatedApplicationIds),"Related Application id cannot be empty");

        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getApplicationRelatedAppsModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("RELATED_APPLICATION_ID", "relatedApplicationId", StringUtils.join(relatedApplicationIds, ","), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("APPLICATION_ID", "applicationId", String.valueOf(appId), NumberOperators.EQUALS));

        builder.delete();
        return false;
    }
}
