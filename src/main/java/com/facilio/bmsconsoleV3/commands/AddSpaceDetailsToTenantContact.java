package com.facilio.bmsconsoleV3.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantSpaceContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AddSpaceDetailsToTenantContact extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        boolean fetchAnnouncementPeople = FacilioUtil.parseBoolean(Constants.getQueryParam(context, "fetchAnnouncementPeople"));
        if(fetchAnnouncementPeople && moduleName.equals(FacilioConstants.ContextNames.TENANT_CONTACT)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);

            Map<Long, Set> tenantVsUnit = new HashMap<>();
            Map<Long, Set> tenantVsSite = new HashMap<>();
            Map<Long, Set> tenantVsBuilding = new HashMap<>();

            List<V3TenantContactContext> tenantcontactList = recordMap.get(moduleName);
            List<Long> tenantIds = new ArrayList<>();
            List<Long> tenantunitIds = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(tenantcontactList)) {
                for (V3TenantContactContext tenantcontact : tenantcontactList) {
                    if (tenantcontact.getTenant() != null && tenantcontact.getTenant().getId() > -1) {
                        tenantIds.add(tenantcontact.getTenant().getId());
                    }
                }
            }
            Set<Long> basespaceId = new HashSet<>();
            List<SupplementRecord> supplementRecords = new ArrayList<SupplementRecord>();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.TENANT_SPACES));
            LookupField space = (LookupField) fieldsMap.get("space");
            supplementRecords.add(space);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", StringUtils.join(tenantIds, ","), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition("CURRENTLY_OCCUPIED", "currentlyOccupied", String.valueOf(true), BooleanOperators.IS));
            List<V3TenantSpaceContext> tenantspaces = V3RecordAPI.getRecordsListWithSupplements(FacilioConstants.ContextNames.TENANT_SPACES, null, V3TenantSpaceContext.class, criteria, supplementRecords);
            if (CollectionUtils.isNotEmpty(tenantspaces)) {
                for (V3TenantSpaceContext tenantSpace : tenantspaces) {
                    Set<Long> tenantunit;
                    if (tenantVsUnit.containsKey(tenantSpace.getTenant().getId())) {
                        tenantunit = tenantVsUnit.get(tenantSpace.getTenant().getId());
                    } else {
                        tenantunit = new HashSet<>();
                    }
                    tenantunit.add(tenantSpace.getSpace().getId());
                    tenantVsUnit.put(tenantSpace.getTenant().getId(), tenantunit);
                    basespaceId.add(tenantSpace.getSpace().getId());
                    Set<Long> sites;
                    if (tenantVsSite.containsKey(tenantSpace.getTenant().getId())) {
                        sites = tenantVsSite.get(tenantSpace.getTenant().getId());
                    } else {
                        sites = new HashSet<>();
                    }
                    if (tenantSpace.getSpace().getSite() != null) {
                        sites.add(tenantSpace.getSpace().getSite().getId());
                        tenantVsSite.put(tenantSpace.getTenant().getId(), sites);
                        basespaceId.add(tenantSpace.getSpace().getSite().getId());
                    }
                    Set<Long> buildings;
                    if (tenantVsBuilding.containsKey(tenantSpace.getTenant().getId())) {
                        buildings = tenantVsBuilding.get(tenantSpace.getTenant().getId());
                    } else {
                        buildings = new HashSet<>();
                    }
                    if (tenantSpace.getSpace().getBuilding() != null) {
                        buildings.add(tenantSpace.getSpace().getBuilding().getId());
                        tenantVsBuilding.put(tenantSpace.getTenant().getId(), buildings);
                        basespaceId.add(tenantSpace.getSpace().getBuilding().getId());
                    }
                }
            }
            Map<Long, V3BaseSpaceContext> basespaces = new HashMap<>();
            if (CollectionUtils.isNotEmpty(basespaceId)) {
                basespaces = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.BASE_SPACE, basespaceId, V3BaseSpaceContext.class);
            }
            if (CollectionUtils.isNotEmpty(tenantcontactList)) {
                for (V3TenantContactContext tenantcontact : tenantcontactList) {
                    if (tenantcontact.getTenant() != null && tenantcontact.getTenant().getId() > -1) {
                        Set<Long> tenantunits = tenantVsUnit.get(tenantcontact.getTenant().getId());
                        Set<Long> buildings = tenantVsBuilding.get(tenantcontact.getTenant().getId());
                        Set<Long> sites = tenantVsSite.get(tenantcontact.getTenant().getId());
                        if (tenantunits != null) {
                            if (tenantunits.size() > 1) {
                                tenantcontact.setDatum("tenantunit", "Multiple Unit");
                            } else {
                                tenantcontact.setDatum("tenantunit", basespaces.get(tenantunits.toArray()[0]).getName());
                            }
                        }
                        if (buildings != null) {
                            if (buildings.size() > 1) {
                                tenantcontact.setDatum("building", "Multiple Building");
                            } else {
                                tenantcontact.setDatum("building", basespaces.get(buildings.toArray()[0]).getName());
                            }
                        }
                        if (sites != null) {
                            if (sites.size() > 1) {
                                tenantcontact.setDatum("site", "Multiple Site");
                            } else {
                                tenantcontact.setDatum("site", basespaces.get(sites.toArray()[0]).getName());
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
