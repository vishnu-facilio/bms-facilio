package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.owasp.esapi.util.CollectionsUtil;

import com.facilio.bmsconsole.context.PMResourcePlanner;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class UpdateResourcePlannerOnPMSitesUpdateCommand extends FacilioCommand {
	
    @Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub


    	List<PlannedMaintenance> newPms = Constants.getRecordList((FacilioContext)context);
    	
    	Map<Long,PlannedMaintenance> oldPMs = Constants.getOldRecordMap(context);
    	
    	
    	for(PlannedMaintenance newPm : newPms) {

			if (newPm instanceof PlannedMaintenance && oldPMs.get(newPm.getId()) instanceof PlannedMaintenance) {

				PlannedMaintenance oldPm = oldPMs.get(newPm.getId());

				List<V3SiteContext> deletedSites = getDeletedSites(newPm, oldPm);

				if (CollectionUtils.isNotEmpty(deletedSites)) {
					removeSitesResourceFormPlanner(deletedSites, newPm);
				}
			}
		}
    	
		return false;
	}

	private void removeSitesResourceFormPlanner(List<V3SiteContext> deletedSites, PlannedMaintenance newPm) throws Exception {
		
		List<Long> siteIds = deletedSites.stream().map(V3SiteContext::getId).collect(Collectors.toList());
		
		FacilioModule plannerModule = Constants.getModBean().getModule(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER);
		FacilioModule resourceModule = Constants.getModBean().getModule(FacilioConstants.ContextNames.RESOURCE);
		
		Map<String, FacilioField> resourcePlannerFieldMap = FieldFactory.getAsMap(Constants.getModBean().getAllFields(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER));
		
		SelectRecordsBuilder<PMResourcePlanner> select = new SelectRecordsBuilder<PMResourcePlanner>()
				.module(plannerModule)
				.beanClass(PMResourcePlanner.class)
				.select(Constants.getModBean().getAllFields(FacilioConstants.PM_V2.PM_V2_RESOURCE_PLANNER))
				.innerJoin(resourceModule.getTableName())
				.on("PM_V2_Resource_Planner.RESOURCE_ID = Resources.ID")
				.andCondition(CriteriaAPI.getCondition(resourcePlannerFieldMap.get("pmId"), newPm.getId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getSiteIdCondition(siteIds, resourceModule))
				;
				
		List<PMResourcePlanner> resourcePlanners = select.get();
		
		if(CollectionUtils.isNotEmpty(resourcePlanners)) {
			
			List<Long> plannerIds = resourcePlanners.stream().map(PMResourcePlanner::getId).collect(Collectors.toList());
			
			DeleteRecordBuilder<PMResourcePlanner> delete = new DeleteRecordBuilder<PMResourcePlanner>()
					.module(plannerModule)
					.andCondition(CriteriaAPI.getIdCondition(plannerIds, plannerModule))
					;
			
			delete.delete();
		}
	}

	private List<V3SiteContext> getDeletedSites(PlannedMaintenance newPm, PlannedMaintenance oldPm) {

		Map<Long,V3SiteContext> newSiteMap = newPm.getSites().stream().collect(Collectors.toMap(V3SiteContext::getId, Function.identity()));
		
		List<V3SiteContext> deletedSites = new ArrayList<>();
		
		for(V3SiteContext site : oldPm.getSites()) {
			
			if(newSiteMap.get(site.getId()) == null) {
				deletedSites.add(site);
			}
		}
		
		return deletedSites;
	}

}
