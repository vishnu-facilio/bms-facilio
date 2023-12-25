package com.facilio.datasandbox.commands;

import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import org.apache.commons.collections4.CollectionUtils;
import com.facilio.command.FacilioCommand;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Context;
import com.facilio.beans.ModuleBean;
import com.facilio.fw.BeanFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComputeModuleSequenceForPackageInstallation extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long targetOrgId = (long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        List<String> moduleSequenceList = (List<String>) context.get(DataMigrationConstants.MODULE_SEQUENCE);
        Map<String, Criteria> moduleVsCriteria = (Map<String, Criteria>) context.getOrDefault("moduleVsCriteria", new HashMap());

        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean", targetOrgId);
        DataMigrationBean migrationBean = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);

        List<FacilioModule> allModules = migrationBean.getAllModules();
        Map<String, FacilioModule> allModulesMap = allModules.stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity(), (a, b) -> b));

        List<FacilioModule> migrationModules = new ArrayList<>();
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = null;
        Map<String, Map<String, Object>> orderedModuleNameVsDetails = new LinkedHashMap<>();


        if (CollectionUtils.isNotEmpty(moduleSequenceList)) {
            migrationModules = SandboxModuleConfigUtil.getModuleObjects(moduleSequenceList, allModulesMap);
            Map<String, FacilioModule> migrationModuleNameVsModObj = migrationModules.stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity(), (a, b) -> b));

            migrationModuleNameVsDetails = SandboxModuleConfigUtil.getModuleDetails(moduleBean, migrationModules, moduleVsCriteria, migrationModuleNameVsModObj);

            for (String moduleName : moduleSequenceList) {
                orderedModuleNameVsDetails.put(moduleName, migrationModuleNameVsDetails.get(moduleName));
            }
        } else {
            List<String> dataConfigModuleNames = DataPackageFileUtil.getDataConfigModuleNames();
            if (CollectionUtils.isNotEmpty(dataConfigModuleNames)) {
                migrationModules = SandboxModuleConfigUtil.getModuleObjects(dataConfigModuleNames, allModulesMap);
                Map<String, FacilioModule> migrationModuleNameVsModObj = migrationModules.stream().collect(Collectors.toMap(FacilioModule::getName, Function.identity(), (a, b) -> b));

                migrationModuleNameVsDetails = SandboxModuleConfigUtil.getModuleDetails(moduleBean, migrationModules, moduleVsCriteria, migrationModuleNameVsModObj);
                for (String moduleName : dataConfigModuleNames) {
                    orderedModuleNameVsDetails.put(moduleName, migrationModuleNameVsDetails.get(moduleName));
                }
            }
            context.put(DataMigrationConstants.MODULE_SEQUENCE, dataConfigModuleNames);
        }

        context.put(DataMigrationConstants.MODULES_VS_DETAILS, orderedModuleNameVsDetails);

        return false;
    }
}
