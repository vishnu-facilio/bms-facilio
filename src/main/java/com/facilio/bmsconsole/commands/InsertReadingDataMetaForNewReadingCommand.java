package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

import static com.facilio.bmsconsole.util.ReadingsAPI.getRDMContextForReadingAndResource;



public class InsertReadingDataMetaForNewReadingCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(InsertReadingDataMetaForNewReadingCommand.class.getName());

    @SuppressWarnings("unchecked")
    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("Inside InsertReadingDataMetaForNewReadingCommand");
        List<Long> parentIds = getParentIds(context);
        List<FacilioModule> modules = CommonCommandUtil.getModulesWithFields(context);

        if (parentIds != null && !parentIds.isEmpty() && modules != null && !modules.isEmpty()) {
            ReadingInputType moduleType = (ReadingInputType) context.get(FacilioConstants.ContextNames.READING_DATA_META_TYPE);
            long orgId = AccountUtil.getCurrentOrg().getOrgId();
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getReadingDataMetaModule().getTableName())
                    .fields(FieldFactory.getReadingDataMetaFields());
            long timestamp = System.currentTimeMillis();
            for (FacilioModule module : modules) {
                if (CollectionUtils.isNotEmpty(module.getFields())) {
                    ReadingInputType type = moduleType != null ? moduleType : ReadingsAPI.getRDMInputTypeFromModuleType(module.getTypeEnum());
                    List<FacilioField> fields = module.getFields();
                    List<FacilioField> dFields = FieldFactory.getDefaultReadingFields(module);
                    fields.removeAll(dFields);
                    for (Long parentId : parentIds) {
                        for (FacilioField field : fields) {
                            ReadingDataMeta dataMeta = getRDMContextForReadingAndResource(orgId, timestamp, parentId, type, field);
                            builder.addRecord(FieldUtil.getAsProperties(dataMeta));
                        }
                    }
                }
            }
            builder.save();
        }

        // TODO Auto-generated method stub
        return false;
    }

    private List<Long> getParentIds(Context context) {
        List<Long> parentIds = (List<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
        if (parentIds == null) {
            Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
            if (parentId != null) {
                parentIds = Collections.singletonList(parentId);
            }
        }
        return parentIds;
    }

}
