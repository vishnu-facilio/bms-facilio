package com.facilio.analytics.v2.command;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.asset.V3AssetCategoryContext;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.utils.PackageBeanUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.List;

public class GetModuleFromCategoryCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context)throws Exception
    {
        ModuleBean modBean = null;
        Long categoryId = (Long) context.get("categoryId");
        String type = (String) context.get("type");
        if(type != null && type.equals("asset"))
        {
            V3AssetCategoryContext asset_category= getAssetCategoryForIds(categoryId, "assetcategory");
            if(asset_category != null){
                modBean = Constants.getModBean();
                FacilioModule module = modBean.getModule(asset_category.getAssetModuleID());
                if(module != null){
                    context.put("moduleName", module.getName());
                }
            }
        }
        return false;
    }

    private V3AssetCategoryContext getAssetCategoryForIds(Long id, String moduleName) throws Exception {
        ModuleBean modBean = Constants.getModBean();
        FacilioModule assetCategoryModule = modBean.getModule("assetcategory");
        List<V3AssetCategoryContext> assetCategories = (List<V3AssetCategoryContext>) getCategoryList(id,assetCategoryModule, V3AssetCategoryContext.class);

        return assetCategories.size() > 0 ? assetCategories.get(0) : null;
    }
    public static List<?> getCategoryList(Long id, FacilioModule module, Class<?> clazz) throws Exception {
        ModuleBean moduleBean = Constants.getModBean();
        SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<>()
                .table(module.getTableName())
                .module(module)
                .beanClass((Class<ModuleBaseWithCustomFields>) clazz)
                .select(moduleBean.getAllFields(module.getName()))
                .andCondition(CriteriaAPI.getIdCondition(id, module));
        return builder.get();
    }
}
