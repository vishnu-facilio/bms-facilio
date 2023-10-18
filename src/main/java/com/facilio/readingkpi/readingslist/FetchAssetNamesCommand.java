package com.facilio.readingkpi.readingslist;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.readingkpi.ReadingKpiAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

public class FetchAssetNamesCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<Map<String, Object>> assets = new ArrayList<>();

        List<Map<String, Object>> assetProps = fetchBuilder(context, false).get();
        for (Map<String, Object> prop : assetProps) {
            Map<String, Object> row = new HashMap<>();
            row.put("name", prop.get("name"));
            row.put("id", prop.get("id"));
            assets.add(row);
        }

        long count = 0;
        Map<String, Object> countProps = fetchBuilder(context, true).fetchFirst();
        if (MapUtils.isNotEmpty(countProps)) {
            count = (long) countProps.get("id");
        }

        context.put(FacilioConstants.ContextNames.COUNT, count);
        context.put(FacilioConstants.ContextNames.DATA, assets);
        return false;
    }

    private GenericSelectRecordBuilder fetchBuilder(Context context, Boolean fetchCount) throws Exception {

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule assetModule = modBean.getModule(FacilioConstants.ContextNames.ASSET);
        FacilioModule resourceModule = modBean.getModule(FacilioConstants.ContextNames.RESOURCE);
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(assetModule.getName()));

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(assetModule.getTableName())
                .innerJoin(resourceModule.getTableName())
                .on(resourceModule.getTableName() + ".ID=" + assetModule.getTableName() + ".ID")
                .orderBy(assetModule.getTableName()+".ID DESC");
        Long categoryId = (Long) context.get(FacilioConstants.ContextNames.ASSET_CATEGORY_ID);
        if (categoryId != null) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("category"), Collections.singleton(categoryId), NumberOperators.EQUALS));
        }

        String searchText = (String) context.get(FacilioConstants.ContextNames.SEARCH_QUERY);
        if (StringUtils.isNotEmpty(searchText)) {
            builder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), searchText, StringOperators.CONTAINS));
        }
        return ReadingKpiAPI.addFilterAndReturnBuilder(context, fetchCount, assetModule, fieldsMap, builder);
    }
}

