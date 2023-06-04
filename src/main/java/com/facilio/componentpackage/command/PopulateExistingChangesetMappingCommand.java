package com.facilio.componentpackage.command;

import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class PopulateExistingChangesetMappingCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);
		for(ComponentType componentType : ComponentType.ORDERED_COMPONENT_TYPE_LIST) {
			if(componentType.getComponentClass() == null) {
				continue;
			}
			PackageBean implClass = componentType.getPackageComponentClassInstance();

			Map<Long, Long> systemComponentIdVsParentId = implClass.fetchSystemComponentIdsToPackage();
			if (MapUtils.isNotEmpty(systemComponentIdVsParentId)) {
				PackageUtil.createBulkChangesetMapping(packageContext.getId(), systemComponentIdVsParentId, packageContext.getVersion(),
						componentType, true);
			}

			Map<Long, Long> customComponentIdVsParentId = implClass.fetchCustomComponentIdsToPackage();
			if (MapUtils.isNotEmpty(customComponentIdVsParentId)) {
				PackageUtil.createBulkChangesetMapping(packageContext.getId(), customComponentIdVsParentId, packageContext.getVersion(),
						componentType, false);
			}
		}

		return false;
	}

}
