package com.facilio.bmsconsoleV3.context.meter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3BuildingContext;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.relation.context.RelationContext;
import lombok.AllArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.PreventiveMaintenance.PMAssignmentType;
import com.facilio.bmsconsole.context.PlannedMaintenance.PMScopeAssigmentType;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsoleV3.context.V3ResourceContext;
import com.facilio.bmsconsoleV3.context.V3SiteContext;
import com.facilio.bmsconsoleV3.util.BulkResourceAllocationUtil;
import com.facilio.modules.FieldUtil;
import com.facilio.ns.context.NameSpaceContext;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import com.facilio.v3.util.V3Util;

import io.jsonwebtoken.lang.Collections;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VirtualMeterTemplateContext extends V3Context{

	private static final long serialVersionUID = 1L;
	
	
	String name;
	String description;
	
	Long readingModuleId;
	Long relationShipId;
	
	PreventiveMaintenance.PMAssignmentType scope;
	VMTemplateStatus vmTemplateStatus;
	AssetCategoryContext assetCategory;
	SpaceCategoryContext spaceCategory;
	
	Boolean status;
	
	String meterName;
	String meterDescription;
	V3UtilityTypeContext utilityType;
	String meta;
	
	List<VirtualMeterTemplateReadingContext> readings;
	
	List<V3SiteContext> sites;
	List<V3BuildingContext> buildings;
	List<V3ResourceContext> resources;

	RelationContext relationShip;
	
	
	public Integer getScope() {
        if (scope == null) {
            return null;
        }
        return scope.getIndex();
    }

    public void setScope(Integer scope) {
        if (scope != null) {
            this.scope = PMAssignmentType.valueOf(scope);
        } else {
            this.scope = null;
        }
    }
    public void setEnumEnum(PMAssignmentType scope) {
        this.scope = scope;
    }

    public PMAssignmentType getScopeEnum() {
        return scope;
    }
	
	public JSONObject getMetaJSON() throws Exception {
		
		if(meta != null) {
			return FacilioUtil.parseJson(meta);
		}
		return null;
	}
	
	public Map<Long,V3MeterContext> constructParentVsVirtualMeters(List<Long> resourceIds) throws Exception {

		Map<Long,V3MeterContext> generatedMeterContext = new HashMap<>();
		
		for(Long resourceId : resourceIds) {
			
			V3ResourceContext resource = (V3ResourceContext)V3Util.getRecord(getScopeEnum().getModuleName(), resourceId, null);
			
			Map<String, Object> params = WorkflowRuleAPI.getRecordPlaceHolders(getScopeEnum().getModuleName(), resource, WorkflowRuleAPI.getOrgPlaceHolders());
			
            String meterName = StringSubstitutor.replace(getMeterName(), params);
            String meterDescription = StringSubstitutor.replace(getMeterDescription(), params);
            
            V3MeterContext meterContext = FieldUtil.getAsBeanFromJson(getMetaJSON() == null? new JSONObject() : getMetaJSON() , V3MeterContext.class);
            
            meterContext.setName(meterName);
            meterContext.setDescription(meterDescription);
            meterContext.setVirtualMeterTemplate(this);
			meterContext.setMeterTypeEnum(V3MeterContext.MeterType.VIRTUAL);
            meterContext.setSiteId(resource.getSiteId());
            meterContext.setUtilityType(getUtilityType());
			if(resource.getResourceTypeEnum().equals(V3ResourceContext.ResourceType.SPACE)) {
				meterContext.setMeterLocation((V3BaseSpaceContext)resource);
			}
            
            generatedMeterContext.put(resourceId,meterContext);
		}
		
		return generatedMeterContext;
	}
	
	private List<Long> resolveScopeAndGetParentResources() throws Exception {
		
		if(!Collections.isEmpty(getResources())) {
			return getResources().stream().map((res) -> res.getId()).collect(Collectors.toList());
		}
		else {
			List<Long> baseSpaceIds = null;
			
			if(getBuildings() != null) {
	            baseSpaceIds = getBuildings().stream().map(V3BuildingContext::getId).collect(Collectors.toList());
	        }
	        else {
	            baseSpaceIds = getSites().stream().map(V3SiteContext::getId).collect(Collectors.toList());
	        }
			
			Long assetCategoryId = assetCategory != null ? assetCategory.getId() : null;
			Long spaceCategoryId = spaceCategory != null ? spaceCategory.getId() : null;

			List<Long> resourcesIdList = BulkResourceAllocationUtil.getMultipleResourceIdToBeAddedFromPM(getScopeEnum(), baseSpaceIds, spaceCategoryId, assetCategoryId,null,null,false);
			
			return resourcesIdList;
		}
	}

	public Integer getVmTemplateStatus() {
		if (vmTemplateStatus == null) {
			return null;
		}
		return vmTemplateStatus.getIndex();
	}

	public void setVmTemplateStatus(Integer vmTemplateStatus) {
		if (vmTemplateStatus != null) {
			this.vmTemplateStatus = VMTemplateStatus.valueOf(vmTemplateStatus);
		} else {
			this.vmTemplateStatus = null;
		}
	}
	public void setVmTemplateStatusEnum(VMTemplateStatus vmTemplateStatus) {
		this.vmTemplateStatus = vmTemplateStatus;
	}

	public VMTemplateStatus getVmTemplateStatusEnum() {
		return vmTemplateStatus;
	}
	@AllArgsConstructor
	@Getter
	public static enum VMTemplateStatus implements FacilioIntEnum {

		UN_PUBLISHED("UnPublished"),
		PUBLISHED("Published"),
		;

		public int getVal() {
			return ordinal() + 1;
		}
		String name;
		@Override
		public String getValue() {
			// TODO Auto-generated method stub
			return this.name;
		}
		private static final VMTemplateStatus[] CREATION_TYPES = VMTemplateStatus.values();
		public static VMTemplateStatus valueOf(int type) {
			if (type > 0 && type <= CREATION_TYPES.length) {
				return CREATION_TYPES[type - 1];
			}
			return null;
		}
	}
}
