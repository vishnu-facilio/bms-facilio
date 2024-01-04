package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.util.V3Util;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class FetchReadingModuleFieldsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Collection<Long> parentIds = (Collection<Long>) context.get(FacilioConstants.ContextNames.PARENT_ID_LIST);
        if (parentIds == null || parentIds.isEmpty()) {
            Long parentId = (Long) context.get(FacilioConstants.ContextNames.PARENT_ID);
            if (parentId != null) {
                parentIds = Collections.singletonList(parentId);
            }
        }
        if (parentIds != null && !parentIds.isEmpty()) {
            String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);

            if (moduleName != null) {
                Map<Long, FacilioField> readingModuleFieldsMap = new HashedMap<>();
                switch (moduleName) {
                    case FacilioConstants.ContextNames.ASSET:
                        for (Long resourceId : parentIds) {
                            V3AssetContext resourceRecord = (V3AssetContext) V3Util.getRecord(FacilioConstants.ContextNames.ASSET, resourceId, null);
                            V3AssetCategoryContext assetCategory = resourceRecord != null ? resourceRecord.getCategory() : null;
                            if (assetCategory != null) {
                                long assetModuleID = assetCategory.getAssetModuleID();
                                readingModuleFieldsMap = getReadingModuleFieldMap( assetModuleID,  readingModuleFieldsMap);
                            }
                        }
                        if (readingModuleFieldsMap != null) {
                            context.put("readingModuleFields", readingModuleFieldsMap);
                        }
                        break;
                    case FacilioConstants.Meter.METER:
                        for (Long resourceId : parentIds) {
                            V3MeterContext resourceRecord = (V3MeterContext) V3Util.getRecord(FacilioConstants.Meter.METER, resourceId, null);
                            V3UtilityTypeContext utilityType = resourceRecord != null ? resourceRecord.getUtilityType() : null;
                            if (utilityType != null) {
                                long meterModuleID = utilityType.getMeterModuleID();
                                readingModuleFieldsMap = getReadingModuleFieldMap( meterModuleID,  readingModuleFieldsMap);
                            }
                        }
                        if (readingModuleFieldsMap != null) {
                            context.put("readingModuleFields", readingModuleFieldsMap);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return false;
    }

    public Map<Long, FacilioField> getReadingModuleFieldMap(long moduleId, Map<Long, FacilioField> readingModuleFieldsMap) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> subModuleRelFields = new ArrayList<>();
        subModuleRelFields.add(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER));
        subModuleRelFields.add(FieldFactory.getField("childModuleId", "CHILD_MODULE_ID", FieldType.NUMBER));

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table("SubModulesRel")
                .select(subModuleRelFields)
                .andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", String.valueOf(moduleId), NumberOperators.EQUALS));
        List<Map<String, Object>> props = selectRecordBuilder.get();

        if (CollectionUtils.isNotEmpty(props)) {
            for (Map<String, Object> prop : props) {
                FacilioModule readingModule = modBean.getModule((long) prop.get("childModuleId"));
                if (readingModule.getTypeEnum().isReadingType()) {
                    List<FacilioField> readingModuleFields = modBean.getAllFields(readingModule.getName());
                    for (FacilioField readingModuleField : readingModuleFields) {
                        long readingModuleFieldId = readingModuleField.getFieldId();
                        readingModuleFieldsMap.put(readingModuleFieldId,readingModuleField);
                    }
                }
            }
        }
        return readingModuleFieldsMap;
    }
}
