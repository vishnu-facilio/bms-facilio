package com.facilio.relation.command;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.relation.context.RelationDataContext;
import com.facilio.relation.context.RelationMappingContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AppendRelationDataCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        RelationMappingContext relationMapping = (RelationMappingContext) context.get(FacilioConstants.ContextNames.RELATION_MAPPING);
        String moduleName = Constants.getModuleName(context);
        long parentId = FacilioUtil.parseLong(Constants.getQueryParamOrThrow(context, "parentId"));

        ModuleBaseWithCustomFields parentData = new ModuleBaseWithCustomFields(parentId);

        List<ModuleBaseWithCustomFields> relationDataList = Constants.getRecordListFromContext((FacilioContext) context, moduleName);
        if (CollectionUtils.isNotEmpty(relationDataList)) {
            for (ModuleBaseWithCustomFields data : relationDataList) {
                RelationDataContext relationData = (RelationDataContext) data;

                String name = relationMapping.getPositionEnum().getFieldName();
                PropertyUtils.setProperty(relationData, name, parentData);

                if (relationData.getLeft() == null || relationData.getRight() == null) {
                    throw new IllegalArgumentException("Invalid data");
                }
            }
        }

        return false;
    }
}
