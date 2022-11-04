package com.facilio.bmsconsoleV3.commands.asset;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.activity.AssetActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.asset.V3AssetContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.v3.context.Constants;
import lombok.extern.log4j.Log4j;
import lombok.var;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j
public class ConstructAddAssetActivityCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> moduleBaseWithCustomFields = recordMap.get(Constants.getModuleName(context));

        List<ModuleBaseWithCustomFields> assetList = (List<ModuleBaseWithCustomFields>) (recordMap.get(Constants.getModuleName(context)));
        AssetCategoryContext assetCategory = AssetsAPI.getCategoryForAsset(((V3AssetContext)(assetList.get(0))).getCategory().getId());
        long assetModuleID = assetCategory.getAssetModuleID();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(assetModuleID);
        String moduleName = module.getName();

        for (var record: moduleBaseWithCustomFields) {
            long recordId = record.getId();
            Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = (Map<String, Map<Long, List<UpdateChangeSet>>>) context.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
            Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) changeSetMap.get(Constants.getModuleName(context));
            if (changeSet == null) {
                return false;
            }

            List<UpdateChangeSet> changeSets = changeSet.get(recordId);

            JSONObject info = new JSONObject();
            List<Object> changeList = new ArrayList<Object>();
            for (UpdateChangeSet changeset : changeSets) {
                long fieldid = changeset.getFieldId();
                Object oldValue = changeset.getOldValue();
                Object newValue = changeset.getNewValue();

                if (newValue ==null && oldValue == null) {
                    continue;
                }

                LOGGER.debug("Asset Add activity command Field Id : "+fieldid +" moduleName : "+ moduleName);

                FacilioField field = modBean.getField(fieldid, moduleName);

                if (field == null) {
                    LOGGER.debug("Field is null for Add Activity command");
                    continue;
                }

                JSONObject changeObj = new JSONObject();
                changeObj.put("field", field.getName());
                changeObj.put("displayName", field.getDisplayName());
                if (field instanceof LookupField && oldValue != null) {
                    long recId = (long) oldValue;
                    oldValue = RecordAPI.getPrimaryValue(((LookupField)field).getLookupModule().getName(), recId);
                    info.put("oldRecordId", recId);
                }
                changeObj.put("oldValue", oldValue);

                if (newValue != null) {
                    if (field instanceof LookupField) {
                        long recId = (long) newValue;
                        newValue = RecordAPI.getPrimaryValue(((LookupField)field).getLookupModule().getName(), recId);
                        info.put("recordId", recId);
                    }
                    else if (field instanceof MultiLookupField) {
                        newValue = CommonCommandUtil.getMultiLookupValues(newValue, field);
                    }
                }
                changeObj.put("newValue", newValue);

                changeList.add(changeObj);
            }
            info.put("changeSet", changeList);

            LOGGER.debug("Asset Activity info : "+ info);

            CommonCommandUtil.addActivityToContext(recordId, -1, AssetActivityType.ADD, info, (FacilioContext) context);
        }

        return false;
    }
}


