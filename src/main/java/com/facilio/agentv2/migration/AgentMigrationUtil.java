package com.facilio.agentv2.migration;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AgentMigrationUtil {

    public static Map<Long, Long> getSourceVsTargetCategories(long sourceOrgId, long targetOrgId) throws Exception {
        ModuleCRUDBean sourceModCrudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", sourceOrgId);
        ModuleCRUDBean targetModCrudBean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", targetOrgId);

        List<AssetCategoryContext> sourceCategories = sourceModCrudBean.getCategoryList();
        List<AssetCategoryContext> targetCategories = targetModCrudBean.getCategoryList();

        Map<Long, Long> categoryMap = new HashMap<>();
        Map<String, Long> nameVsId = sourceCategories.stream().collect(Collectors.toMap(AssetCategoryContext::getName, ModuleBaseWithCustomFields::getId, (p, c) -> c));
        for(AssetCategoryContext category: targetCategories) {
            categoryMap.put(nameVsId.get(category.getName()), category.getId());
        }
        return categoryMap;
    }

}
