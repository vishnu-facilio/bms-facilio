package com.facilio.componentpackage.command;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageUtil;
import com.facilio.sandbox.utils.SandboxAPI;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class PopulateExistingChangesetMappingCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<Integer> skipComponentsFromOrgInfo = SandboxAPI.getSkippedComponentsFromOrgInfo(AccountUtil.getCurrentOrg().getOrgId());
		List<Integer> skipComponents = (List<Integer>) context.getOrDefault(PackageConstants.SKIP_COMPONENTS, new ArrayList<>());
		Map<Integer, List<Long>> skipComponentVsIdsFromOrgInfo = SandboxAPI.getSkippedComponentIdsFromOrgInfo(AccountUtil.getCurrentOrg().getOrgId());
		skipComponents.addAll(skipComponentsFromOrgInfo);
		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);
		for(ComponentType componentType : ComponentType.ORDERED_COMPONENT_TYPE_LIST) {
			if(componentType.getComponentClass() == null || skipComponents.contains(componentType.getIndex()) ) {
				continue;
			}
			PackageBean implClass = componentType.getPackageComponentClassInstance();

			LOGGER.info("####Sandbox - Started Populating Changeset Mapping for ComponentType - " + componentType.name());
			Map<Long, Long> systemComponentIdVsParentId = implClass.fetchSystemComponentIdsToPackage();
			removeSkippedIdsFromComponentIds(skipComponentVsIdsFromOrgInfo, componentType, systemComponentIdVsParentId);
			if (MapUtils.isNotEmpty(systemComponentIdVsParentId)) {
				PackageUtil.createBulkChangesetMapping(packageContext.getId(), systemComponentIdVsParentId, packageContext.getVersion(),
						componentType, true);
			}

			Map<Long, Long> customComponentIdVsParentId = implClass.fetchCustomComponentIdsToPackage();
			removeSkippedIdsFromComponentIds(skipComponentVsIdsFromOrgInfo, componentType, customComponentIdVsParentId);
			if (MapUtils.isNotEmpty(customComponentIdVsParentId)) {
				PackageUtil.createBulkChangesetMapping(packageContext.getId(), customComponentIdVsParentId, packageContext.getVersion(),
						componentType, false);
			}
			LOGGER.info("####Sandbox - Completed Populating Changeset Mapping for ComponentType - " + componentType.name());
		}

		return false;
	}
	private void removeSkippedIdsFromComponentIds(Map<Integer ,List<Long>> skipComponentVsIdsFromOrgInfo, ComponentType componentType, Map<Long, Long> componentIdVsParentId){
		if(skipComponentVsIdsFromOrgInfo != null && !skipComponentVsIdsFromOrgInfo.isEmpty()){
			if(skipComponentVsIdsFromOrgInfo.containsKey(componentType.getIndex())){
				List<Long> skipComponentIds = skipComponentVsIdsFromOrgInfo.get(componentType.getIndex());
				for (Long id : skipComponentIds) {
					componentIdVsParentId.remove(id);
				}
			}
		}
	}

}
