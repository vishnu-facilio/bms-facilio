package com.facilio.componentpackage.command;

import com.facilio.command.FacilioCommand;
import com.facilio.componentpackage.constants.ComponentType;
import com.facilio.componentpackage.constants.PackageConstants;
import com.facilio.componentpackage.context.PackageContext;
import com.facilio.componentpackage.interfaces.PackageBean;
import com.facilio.componentpackage.utils.PackageUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j
public class PopulateExistingChangesetMappingCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {

		PackageContext packageContext = (PackageContext) context.get(PackageConstants.PACKAGE_CONTEXT);
		for(ComponentType componentType : ComponentType.ORDERED_COMPONENT_TYPE_LIST) {
			PackageBean implClass = componentType.getPackageComponentClassInstance();

			List<Long> systemComponentIds = implClass.fetchSystemComponentIdsToPackage();
			Map<Long, String> systemIdVsUniqueIds = systemComponentIds.stream().collect(Collectors.toMap(id -> id, id -> String.valueOf(id)));
			PackageUtil.createBulkChangesetMapping(packageContext.getId(), systemIdVsUniqueIds, packageContext.getVersion(),
					componentType, true);

			List<Long> customComponentIds = implClass.fetchCustomComponentIdsToPackage();
			Map<Long, String> customIdVsUniqueIds = customComponentIds.stream().collect(Collectors.toMap(id -> id, id -> String.valueOf(id)));
			PackageUtil.createBulkChangesetMapping(packageContext.getId(), customIdVsUniqueIds, packageContext.getVersion(),
					componentType, false);

		}

		return false;
	}

}
