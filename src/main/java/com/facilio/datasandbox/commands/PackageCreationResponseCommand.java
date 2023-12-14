package com.facilio.datasandbox.commands;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.datamigration.util.DataMigrationConstants;
import com.facilio.datasandbox.util.DataPackageFileUtil;
import com.facilio.componentpackage.utils.PackageUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import com.facilio.command.FacilioCommand;
import com.facilio.xml.builder.XMLBuilder;
import org.apache.commons.chain.Context;
import org.apache.commons.io.FileUtils;

import java.util.HashMap;
import java.util.Map;

public class PackageCreationResponseCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, String> moduleNameVsCsvFileName = (Map<String, String>) context.get(DataMigrationConstants.MODULENAME_VS_CSV_FILENAME);
        Map<String, Map<String, Object>> migrationModuleNameVsDetails = (HashMap<String, Map<String, Object>>) context.get(DataMigrationConstants.MODULES_VS_DETAILS);

        if (MapUtils.isNotEmpty(moduleNameVsCsvFileName)) {
            // dataConfig.xml
            XMLBuilder dataConfigXML = XMLBuilder.create(PackageConstants.DATA_FOLDER_NAME);
            XMLBuilder modulesXMLBuilder = dataConfigXML.element(PackageConstants.MODULES);

            for (Map.Entry<String, Map<String, Object>> moduleNameVsDetails : migrationModuleNameVsDetails.entrySet()) {
                String moduleName = moduleNameVsDetails.getKey();
                String moduleFileName = moduleNameVsCsvFileName.get(moduleName);

                if (StringUtils.isNotEmpty(moduleFileName)) {

                    XMLBuilder dataModuleNameConfigXML = modulesXMLBuilder.element(PackageConstants.MODULE);
                    dataModuleNameConfigXML.attr(PackageConstants.NAME, moduleName);
                    dataModuleNameConfigXML.text(moduleFileName);
                }
            }

            DataPackageFileUtil.addDataConfFile(dataConfigXML.getAsXMLString());
        }

        String url = DataPackageFileUtil.getFileUrl(PackageUtil.getRootFolderPath());
        CommonCommandUtil.insertOrgInfo(DataMigrationConstants.SANDBOX_DATA_PACKAGE_URL, url);

        // clean temp folder
        FileUtils.deleteDirectory(DataPackageFileUtil.getTempFolderRoot());

        // remove threadlocal
        context.put(DataMigrationConstants.ROOT_FOLDER_PATH, url);
        PackageUtil.removeRootFolderPath();

        return false;
    }
}
