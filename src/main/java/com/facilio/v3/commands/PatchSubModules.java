package com.facilio.v3.commands;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.V3Builder.V3Config;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import java.util.*;

public class PatchSubModules extends FacilioCommand {

    private List<ModuleBaseWithCustomFields> getSubModuleRecords(String subModuleName, List<Long> recordIds) throws Exception{
        FacilioChain fetchRecordChain = ChainUtil.getFetchRecordChain(subModuleName);
        FacilioContext fetchRecordChainContext = fetchRecordChain.getContext();

        FacilioModule module = ChainUtil.getModule(subModuleName);
        V3Config config = ChainUtil.getV3Config(subModuleName);

        Constants.setRecordIds(fetchRecordChainContext, recordIds);
        fetchRecordChainContext.put(FacilioConstants.ContextNames.PERMISSION_TYPE, FieldPermissionContext.PermissionType.READ_ONLY);
        Class beanClass = ChainUtil.getBeanClass(config, module);
        fetchRecordChainContext.put(Constants.BEAN_CLASS, beanClass);
        Constants.setModuleName(fetchRecordChainContext, subModuleName);

        fetchRecordChain.execute();

        Map<String, List<ModuleBaseWithCustomFields>> subModuleRecordMap = Constants.getRecordMap(fetchRecordChainContext);
        return subModuleRecordMap.get(subModuleName);
    }


    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        String moduleName = Constants.getModuleName(context);

        List<ModuleBaseWithCustomFields> recordList = recordMap.get(moduleName);
        Map<String, List<Long>> moduleNameVsRecordId = new HashMap<>();
        for (ModuleBaseWithCustomFields record: recordList) {
            Map<String, List<Map<String, Object>>> subForm = record.getSubForm();
            if (MapUtils.isEmpty(subForm)) {
                continue;
            }
            Set<String> subModuleNameList = subForm.keySet();
            for (String subModuleName: subModuleNameList) {
                List<Map<String, Object>> subModulePropList = subForm.get(subModuleName);
                for (Map<String, Object> subModuleProp: subModulePropList) {
                    Long id = (Long) subModuleProp.get("id");
                    if (id == null) {
                        continue;
                    }
                    moduleNameVsRecordId.computeIfAbsent(subModuleName, k -> new ArrayList<>());
                    moduleNameVsRecordId.get(subModuleName).add(id);
                }
            }
        }

        Map<String, Map<Long, ModuleBaseWithCustomFields>> subModuleMap = new HashMap<>();
        Set<String> subModuleNameList = moduleNameVsRecordId.keySet();
        for (String subModuleName: subModuleNameList) {
            List<Long> recordIds = moduleNameVsRecordId.get(subModuleName);
            List<ModuleBaseWithCustomFields> subModuleRecords;
            if (ChainUtil.getV3Config(subModuleName) == null) {
                subModuleRecords = (List<ModuleBaseWithCustomFields>) V3RecordAPI.getRecordsList(subModuleName, recordIds);
            } else {
                subModuleRecords = getSubModuleRecords(subModuleName, recordIds);
            }

            subModuleMap.put(subModuleName, FieldUtil.getAsMap(subModuleRecords));
        }

        for (ModuleBaseWithCustomFields record: recordList) {
            Map<String, List<Map<String, Object>>> subForm = record.getSubForm();
            if (MapUtils.isNotEmpty(subForm)) {
                Set<String> subModuleNames = subForm.keySet();
                for (String subModuleName : subModuleNames) {
                    Map<Long, ModuleBaseWithCustomFields> subModuleRecordMap
                            = subModuleMap.get(subModuleName);
                    List<Map<String, Object>> subModulePropList = subForm.get(subModuleName);

                    List<Map<String, Object>> changedProps = new ArrayList<>();
                    for (Map<String, Object> subModuleProp : subModulePropList) {
                        Long id = (Long) subModuleProp.get("id");
                        if (id == null) {
                            changedProps.add(subModuleProp);
                            continue;
                        }

                        ModuleBaseWithCustomFields subModuleRecord = subModuleRecordMap.get(id);
                        Map<String, Object> newProp = FieldUtil.getAsProperties(subModuleRecord);
                        Set<String> propKeys = newProp.keySet();
                        for (String prop : propKeys) {
                            if (!subModuleProp.containsKey(prop)) {
                                continue;
                            }

                            newProp.put(prop, subModuleProp.get(prop));
                        }
                        changedProps.add(newProp);
                    }
                    subForm.put(subModuleName, changedProps);
                }
            }
        }
        return false;
    }
}
