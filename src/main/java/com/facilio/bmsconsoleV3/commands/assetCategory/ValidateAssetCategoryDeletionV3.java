package com.facilio.bmsconsoleV3.commands.assetCategory;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

public class ValidateAssetCategoryDeletionV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        ArrayList<Long> recordIDs = (ArrayList<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        long recordID = recordIDs.get(0);

        FacilioModule assetModule = ModuleFactory.getAssetsModule();
        GenericSelectRecordBuilder assetSelectBuilder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(FieldFactory.getIdField(assetModule)))
                .table(assetModule.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(assetModule))
                .andCustomWhere(" Resources.SYS_DELETED IS NULL AND Assets.CATEGORY = ?", recordID)
                .innerJoin(" Resources ")
                .on(" Resources.ID = Assets.ID ")
                .limit(1);
        List<Map<String, Object>> result = assetSelectBuilder.get();
        List<String> moduleNames = new ArrayList<>();

        boolean stopChain = false;
        if (!result.isEmpty()) {
            stopChain = true;
            moduleNames.add(assetModule.getName());
        }

        FacilioModule readingRuleModule = ModuleFactory.getReadingRuleModule();
        GenericSelectRecordBuilder readingRuleSelectBuilder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(FieldFactory.getIdField(readingRuleModule)))
                .table(readingRuleModule.getTableName())
                .andCustomWhere("ASSET_CATEGORY_ID = ?", recordID).limit(1);
        result = readingRuleSelectBuilder.get();

        if (!result.isEmpty()) {
            stopChain = true;
            moduleNames.add(readingRuleModule.getDisplayName());
        }

        FacilioModule assetCategoryModule = ModuleFactory.getAssetCategoryModule();
        GenericSelectRecordBuilder assetCategorySelectBuilder = new GenericSelectRecordBuilder()
                .select(Arrays.asList(FieldFactory.getIdField(assetCategoryModule)))
                .table(assetCategoryModule.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(assetCategoryModule))
                .andCustomWhere("PARENT_CATEGORY_ID = ? AND SYS_DELETED IS NULL", recordID)
                .limit(1);
        result = assetCategorySelectBuilder.get();

        if (!result.isEmpty()) {
            stopChain = true;
            moduleNames.add(assetCategoryModule.getDisplayName());
        }

        // TODO - check this
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<V3AssetCategoryContext> selectRecordsBuilder = new SelectRecordsBuilder<V3AssetCategoryContext>()
                .beanClass(V3AssetCategoryContext.class)
                .moduleName(assetCategoryModule.getName())
                .select(modBean.getAllFields(assetCategoryModule.getName()))
                .andCondition(CriteriaAPI.getIdCondition(recordID, assetCategoryModule));
        List<V3AssetCategoryContext> list = selectRecordsBuilder.get();
        if (CollectionUtils.isNotEmpty(list)) {
            long assetModuleId = list.get(0).getAssetModuleID();
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table("SubModulesRel")
                    .select(Collections.singletonList(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER)))
                    .andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", String.valueOf(assetModuleId), NumberOperators.EQUALS));
            result = builder.get();
            if (!result.isEmpty()) {
                stopChain = true;
                moduleNames.add("Asset Readings");
            }
        }

        //context.put(FacilioConstants.ContextNames.MODULE_NAMES, moduleNames);
        if(moduleNames.size() > 0){
            StringBuilder builder = new StringBuilder("This Asset Category is being used by following modules ");
            for (String name : moduleNames) {
                builder.append(" ").append(name);
            }
            throw new RESTException(ErrorCode.VALIDATION_ERROR, builder.toString());
        }
        return stopChain;
    }


}
