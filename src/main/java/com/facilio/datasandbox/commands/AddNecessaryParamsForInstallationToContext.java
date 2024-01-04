package com.facilio.datasandbox.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.context.PackageChangeSetMappingContext;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.datamigration.beans.DataMigrationBean;
import com.facilio.datamigration.context.DataMigrationStatusContext;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.context.ModuleCSVFileContext;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.datasandbox.util.SandboxModuleConfigUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONObject;

import java.util.*;

public class AddNecessaryParamsForInstallationToContext extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long packageId = (Long) context.get(DataMigrationConstants.PACKAGE_ID);
        Long sourceOrgId = (Long) context.get(DataMigrationConstants.SOURCE_ORG_ID);
        Long targetOrgId = (Long) context.get(DataMigrationConstants.TARGET_ORG_ID);
        Long dataMigrationId = (Long) context.get(DataMigrationConstants.DATA_MIGRATION_ID);
        Map<String, JSONObject> moduleNameVsFilter = (Map<String, JSONObject>) context.get(DataMigrationConstants.FILTERS);
        List<String> skipDataMigrationModules = (List<String>) context.getOrDefault(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, new ArrayList<>());

        long transactionTimeOut = (long) context.getOrDefault(DataMigrationConstants.TRANSACTION_TIME_OUT, DataMigrationConstants.MAX_THREAD_TIME);
        context.put(DataMigrationConstants.TRANSACTION_START_TIME, System.currentTimeMillis());
        context.put(DataMigrationConstants.TRANSACTION_TIME_OUT, transactionTimeOut);

        // Modules copied in Meta are reUpdated in DataMigration flow
        List<String> updateOnlyModulesList = SandboxModuleConfigUtil.updateOnlyModulesList();
        List<String> pickListTypeModules = SandboxModuleConfigUtil.pickListTypeModules();
        Map<String, String> pickListModuleNameVsFieldName = SandboxModuleConfigUtil.unhandledPickListModuleNameVsFieldName();

        skipDataMigrationModules.addAll(pickListTypeModules);
        skipDataMigrationModules.addAll(new ArrayList<>(pickListModuleNameVsFieldName.keySet()));
        context.put(DataMigrationConstants.UPDATE_ONLY_MODULES, updateOnlyModulesList);
        context.put(DataMigrationConstants.SKIP_DATA_MIGRATION_MODULE_NAMES, skipDataMigrationModules);

        DataMigrationBean targetConnection = (DataMigrationBean) BeanFactory.lookup("DataMigrationBean", true, targetOrgId);
        DataMigrationStatusContext dataMigrationObj = targetConnection.checkAndAddDataMigrationStatus(sourceOrgId, dataMigrationId, false);
        context.put(DataMigrationConstants.DATA_MIGRATION_CONTEXT, dataMigrationObj);
        context.put(DataMigrationConstants.DATA_MIGRATION_ID, dataMigrationObj.getId());

        Map<String, Stack<ModuleCSVFileContext>> moduleNameVsCsvFileContext = DataPackageFileUtil.getModuleNameVsXmlFileName();
        context.put(DataMigrationConstants.MODULENAME_VS_CSV_FILE_CONTEXT, moduleNameVsCsvFileContext);

        Map<ComponentType, Map<Long, Long>> componentTypeVsOldVsNewId = new HashMap<>();
        Map<ComponentType, List<PackageChangeSetMappingContext>> allPackageChangesets = PackageUtil.getAllPackageChangsets(packageId);

        if (MapUtils.isNotEmpty(allPackageChangesets)) {
            for (ComponentType componentType : allPackageChangesets.keySet()) {
                List<PackageChangeSetMappingContext> packageChangeSetMappingContexts = allPackageChangesets.get(componentType);
                if (CollectionUtils.isNotEmpty(packageChangeSetMappingContexts)) {
                    Map<Long, Long> uIDVsCompId = new HashMap<>();
                    for (PackageChangeSetMappingContext changeSet : packageChangeSetMappingContexts) {
                        uIDVsCompId.put(Long.parseLong(changeSet.getUniqueIdentifier()), changeSet.getComponentId());
                    }
                    componentTypeVsOldVsNewId.put(componentType, uIDVsCompId);
                }
            }
        }
        context.put(DataMigrationConstants.PACKAGE_CHANGE_SET, componentTypeVsOldVsNewId);

        Map<String, Criteria> clientCriteria = SandboxModuleConfigUtil.getClientCriteria(moduleNameVsFilter);
        context.put(DataMigrationConstants.MODULE_VS_CRITERIA, clientCriteria);

        return false;
    }
}
