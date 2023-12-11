package com.facilio.qa.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTemplateContext;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.context.Constants;

public class InspectionTemplateBeforeSaveCommand extends FacilioCommand {
	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<InspectionTemplateContext> inspections = Constants.getRecordList((FacilioContext) context);
		
		for(InspectionTemplateContext inspection : inspections) {
			if(inspection.getStatus() == null) {
				inspection.setStatus(Boolean.TRUE);
			}
			
			if(inspection.getSites() != null) {
				List<Long> siteIds = new ArrayList<Long>();
				List<SiteContext> sitesToBeRemoved = new ArrayList<SiteContext>();
				for(SiteContext site : inspection.getSites()) {
					
					if(siteIds.contains(site.getId())) {
						sitesToBeRemoved.add(site);
					}
					else {
						siteIds.add(site.getId());
					}
				}
				if(!sitesToBeRemoved.isEmpty()) {
					inspection.getSites().removeAll(sitesToBeRemoved);
				}
			}
			
			if(inspection.getBuildings() != null) {
				List<Long> buildingIds = new ArrayList<Long>();
				List<BuildingContext> buildingToBeRemoved = new ArrayList<BuildingContext>();
				for(BuildingContext building : inspection.getBuildings()) {
					
					if(buildingIds.contains(building.getId())) {
						buildingToBeRemoved.add(building);
					}
					else {
						buildingIds.add(building.getId());
					}
				}
				if(!buildingToBeRemoved.isEmpty()) {
					inspection.getBuildings().removeAll(buildingToBeRemoved);
				}
			}

			if(inspection.getIsPublished() == null){
				inspection.setIsPublished(false);
			}
		}
		return false;
	}

}
