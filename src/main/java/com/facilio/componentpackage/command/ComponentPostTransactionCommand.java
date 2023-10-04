package com.facilio.componentpackage.command;

import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageFileContext;
import com.facilio.componentpackage.context.PackageFolderContext;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.sandbox.utils.SandboxAPI;
import com.facilio.sandbox.utils.SandboxConstants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j
public class ComponentPostTransactionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        XMLBuilder packageConfigXML = (XMLBuilder) context.get(PackageConstants.PACKAGE_CONFIG_XML);
        PackageFolderContext rootFolder = (PackageFolderContext) context.get(PackageConstants.PACKAGE_ROOT_FOLDER);

        List<XMLBuilder> allComponents = packageConfigXML.getElement(PackageConstants.PackageXMLConstants.COMPONENTS)
                .getFirstLevelElementListForTagName(PackageConstants.PackageXMLConstants.COMPONENT);
        PackageFolderContext componentsFolder = rootFolder.getFolder(PackageConstants.COMPONENTS_FOLDER_NAME);
        long sandboxId = (long) context.getOrDefault(SandboxConstants.SANDBOX_ID, -1L);
        long sourceOrgId = (long) context.getOrDefault(PackageConstants.SOURCE_ORG_ID, -1L);

        List<Integer> skipComponents = (List<Integer>) context.getOrDefault(PackageConstants.SKIP_COMPONENTS, new ArrayList<>());

        float i = 90;
        int count = 0;
        for (XMLBuilder component : allComponents) {
            ComponentType componentType = ComponentType.valueOf(component.getAttribute(PackageConstants.PackageXMLConstants.COMPONENT_TYPE));
            if (componentType.isPostTransactionRequired()) {
                count++;
            }
        }
        float parts = (float) 10 /count;
        int progress;
        for(XMLBuilder component : allComponents) {
            ComponentType componentType = ComponentType.valueOf(component.getAttribute(PackageConstants.PackageXMLConstants.COMPONENT_TYPE));
            if (componentType.getComponentClass() == null || !componentType.isPostTransactionRequired() || skipComponents.contains(componentType.getIndex())) {
                continue;
            }

            LOGGER.info("####Sandbox - Started PostTransaction for ComponentType - " + componentType.name());
            Map<String, Long> uniqueIdVsComponentIdList = PackageUtil.getComponentsUIdVsComponentIdForComponent(componentType);

            String componentTypeFilePath = component.getText();
            PackageFileContext componentXMLContext = componentsFolder.getFile(componentTypeFilePath);
            List<XMLBuilder> componentXMLBuilders = componentXMLContext.getXmlContent().getElementList(componentType.name());

            Map<Long, XMLBuilder> componentIdVsXMLBuilder = new HashMap<>();
            for(XMLBuilder componentXML : componentXMLBuilders) {
                String uniqueIdentifier = componentXML.getAttribute(PackageConstants.PackageXMLConstants.UNIQUE_IDENTIFIER);
                if (uniqueIdVsComponentIdList.containsKey(uniqueIdentifier)) {
                    componentIdVsXMLBuilder.put(uniqueIdVsComponentIdList.get(uniqueIdentifier), componentXML);
                }
            }

            if (MapUtils.isNotEmpty(componentIdVsXMLBuilder)) {
                componentType.getPackageComponentClassInstance().postComponentAction(componentIdVsXMLBuilder);
            }

            LOGGER.info("####Sandbox - Completed PostTransaction for ComponentType - " + componentType.name());
            if(sandboxId > -1L) {
                i = i + parts;
                progress = (int)i;
                SandboxAPI.sendSandboxProgress(progress, sandboxId, "Post Transaction done for component name--> "+ componentType.name(), sourceOrgId);
            }
        }

        return false;
    }
}
