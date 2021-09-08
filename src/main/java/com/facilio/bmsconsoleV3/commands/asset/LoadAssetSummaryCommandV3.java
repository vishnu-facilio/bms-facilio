package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.view.CustomModuleData;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

public class LoadAssetSummaryCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<V3AssetContext> assetList = (List<V3AssetContext>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get("asset"));
        List<ModuleBaseWithCustomFields> recordList = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(assetList)){

            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(assetList.get(0).getCategory().getId());
            long assetModuleID = assetCategory.getAssetModuleID();
            FacilioModule module = modBean.getModule(assetModuleID);
            String moduleName = module.getName();

            List<FacilioField> fields = modBean.getAllFields(moduleName);

            V3Config v3Config = ChainUtil.getV3Config(moduleName);
            Class beanClassName = ChainUtil.getBeanClass(v3Config, module);
            if (beanClassName == null) {
                beanClassName = ModuleBaseWithCustomFields.class;
            }

            List<SupplementRecord> supplementFields = new ArrayList<>();

            Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);

            SupplementRecord identifiedLocationField = (SupplementRecord) fieldsAsMap.get("identifiedLocation");
            SupplementRecord spaceField = (SupplementRecord) fieldsAsMap.get("space");
            SupplementRecord moduleStateField = (SupplementRecord) fieldsAsMap.get("moduleState");
            SupplementRecord categoryField = (SupplementRecord) fieldsAsMap.get("category");

            supplementFields.add(identifiedLocationField);
            supplementFields.add(spaceField);
            supplementFields.add(moduleStateField);
            supplementFields.add(categoryField);

            LookupField sysCreatedBy = (LookupField) FieldFactory.getSystemField("sysCreatedBy", modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
            supplementFields.add(sysCreatedBy);
            LookupField sysModifiedBy = (LookupField) FieldFactory.getSystemField("sysModifiedBy", modBean.getModule(FacilioConstants.ContextNames.RESOURCE));
            supplementFields.add(sysModifiedBy);

            for(V3AssetContext asset :assetList){
                SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
                        .module(module)
                        .beanClass(beanClassName)
                        .select(fields)
                        .andCondition(CriteriaAPI.getIdCondition(asset.getId(), module))
                        ;

                if (CollectionUtils.isNotEmpty(supplementFields)) {
                    builder.fetchSupplements(supplementFields);
                }

                List<ModuleBaseWithCustomFields> records = builder.get();
                if(records.size() > 0) {
                    ResourceAPI.loadModuleResources(records, fields);
                    recordList.add( records.get(0));
                }
            }

            Map<String,Object> recordMap = new HashMap<String, Object>();

            recordMap.put("asset", recordList);

            context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);

        }

        return false;
    }

}
