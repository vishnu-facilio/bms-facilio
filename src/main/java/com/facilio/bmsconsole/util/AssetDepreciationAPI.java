package com.facilio.bmsconsole.util;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetDepreciationContext;
import com.facilio.bmsconsole.context.AssetDepreciationRelContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.modules.fields.MultiLookupMeta;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssetDepreciationAPI {

    public static AssetDepreciationContext getAssetDepreciation(long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_DEPRECIATION);

        SelectRecordsBuilder<AssetDepreciationContext> builder = new SelectRecordsBuilder<AssetDepreciationContext>()
                .module(module)
                .beanClass(AssetDepreciationContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.ASSET_DEPRECIATION))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        AssetDepreciationContext assetDepreciationContext = builder.fetchFirst();

        if (assetDepreciationContext != null) {
            assetDepreciationContext.setAssetDepreciationRelList(getRelList(assetDepreciationContext.getId()));
        }
        return assetDepreciationContext;
    }

    public static List<AssetDepreciationRelContext> getRelList(long id) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                .select(FieldFactory.getAssetDepreciationRelFields())
                .andCondition(CriteriaAPI.getCondition("DEPRECIATION_ID", "depreciationId", String.valueOf(id), NumberOperators.EQUALS));
        return FieldUtil.getAsBeanListFromMapList(builder.get(), AssetDepreciationRelContext.class);
    }

    public static void addRelList(long id, List<Long> assetIds) throws Exception {
        if (id > 0 && CollectionUtils.isNotEmpty(assetIds)) {
            GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
                    .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                    .fields(FieldFactory.getAssetDepreciationRelFields());

            List<AssetDepreciationRelContext> list = new ArrayList<>();
            for (Long assetId : assetIds) {
                AssetDepreciationRelContext relContext = new AssetDepreciationRelContext();
                relContext.setAssetId(assetId);
                relContext.setDepreciationId(id);
                list.add(relContext);
            }
            builder.addRecords(FieldUtil.getAsMapList(list, AssetDepreciationRelContext.class));
            builder.save();
        }

    }

    public static void deleteRelList(long id) throws Exception {
        GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
                .table(ModuleFactory.getAssetDepreciationRelModule().getTableName())
                .andCondition(CriteriaAPI.getCondition("DEPRECIATION_ID", "depreciationId", String.valueOf(id), NumberOperators.EQUALS));
        builder.delete();
    }
}
