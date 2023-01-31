package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3WorkOderAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;

import java.util.*;

public class ValidateWOForUpdate extends FacilioCommand {
    private static org.apache.log4j.Logger log = LogManager.getLogger(ValidateWOForUpdate.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List> recordMap = (Map<String, List>)
                context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        List<V3WorkOrderContext> oldWos = (List<V3WorkOrderContext>)
                context.get(FacilioConstants.TicketActivity.OLD_TICKETS);

        Map<Long, V3WorkOrderContext> oldWosMap = new HashMap<>();
        for (V3WorkOrderContext rec : oldWos) {
            oldWosMap.put(rec.getId(), rec);
        }

        for (V3WorkOrderContext wo : wos) {
            V3WorkOrderContext oldWo = oldWosMap.get(wo.getId());

            if (siteChangeBreach(wo, oldWo)) {
                throw new IllegalArgumentException("Site cannot be changed for work order");
            }

            if (foreignTenantBreach(wo, oldWo)) {
                throw new IllegalArgumentException("The selected tenant belongs to another site");
            }

            if (openChildWorkOrderBreach(wo, oldWo)) {
                throw new IllegalArgumentException("Please close all child work orders before closing the workorder");
            }

            if (parentWoValidityBreach(wo, oldWo)) {
                throw new IllegalArgumentException("Cannot add an open work order to a closed parent work order");
            }

            if (openTasksBreach(wo, oldWo)) {
                throw new IllegalArgumentException("Please close all tasks before closing the work order");
            }

            if (closureSignatureBreach(wo, oldWo)) {
                throw new IllegalArgumentException("Please enter the signature before closing the work order");
            }

            if (foreignSpaceBreach(wo, oldWo)) {
                throw new IllegalArgumentException("The Space does not belong in the work order's site");
            }

            if (foreignAssetBreach(wo, oldWo)) {
                throw new IllegalArgumentException("The Asset does not belong in the work order's site");
            }

            if (userAccessBreach(wo, oldWo)) {
                throw new IllegalArgumentException("The User does not belong to current site");
            }

            if (teamAccessBreach(wo, oldWo)) {
                throw new IllegalArgumentException("The Team does not belong to current site");
            }
        }
        return false;
    }

    private boolean teamAccessBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) throws Exception {
        if (wo.getAssignmentGroup() == null ||
                (wo.getAssignmentGroup().getId() <= 0)) {
            return false;
        }
        Group assignmentGroup = AccountUtil.getGroupBean().getGroup(wo.getAssignmentGroup().getId());
        long groupSiteId = assignmentGroup.getSiteId();
        long siteId = oldWo.getSiteId();
        return (groupSiteId > 0 && groupSiteId != siteId);
    }

    private boolean userAccessBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) throws Exception {
        if (wo.getAssignedTo() == null ||
                (wo.getAssignedTo().getId() <= 0)) {
            return false;
        }
        User assignedTo = AccountUtil.getUserBean().getUser(wo.getAssignedTo().getOuid(), true);
        List<Long> accessibleSpace = assignedTo.getAccessibleSpace();
        Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);
        if (idVsBaseSpace == null || idVsBaseSpace.isEmpty()) {
            return false;
        }
        Set<Long> userSiteIds = new HashSet<>();
        for (long id : accessibleSpace) {
            BaseSpaceContext space = idVsBaseSpace.get(id);
            if (space.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
                userSiteIds.add(space.getId());
            } else {
                userSiteIds.add(space.getSiteId());
            }
        }
        long siteId = oldWo.getSiteId();
        return !userSiteIds.isEmpty() && !userSiteIds.contains(siteId);
    }

    private boolean foreignSpaceBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) throws Exception {
        if (wo.getResource() == null || wo.getResource().isDeleted()) {
            return false;
        }
        ResourceContext resource = ResourceAPI.getResource(wo.getResource().getId());
        if (resource.getResourceTypeEnum() != ResourceContext.ResourceType.SPACE) {
            return false;
        }
        BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(resource.getId());
        if (baseSpace == null || !(baseSpace.getId() > 0)) {
            return false;
        }
        long resourceSiteID = baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE ?
                baseSpace.getId() : baseSpace.getSiteId();

        return resourceSiteID != oldWo.getSiteId();
    }

    private boolean foreignAssetBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) throws Exception {
        if (wo.getResource() == null || wo.getResource().isDeleted()) {
            return false;
        }
        ResourceContext resource = ResourceAPI.getResource(wo.getResource().getId());
        if (resource.getResourceTypeEnum() != ResourceContext.ResourceType.ASSET) {
            return false;
        }
        AssetContext asset = AssetsAPI.getAssetInfo(resource.getId(), false);
        if (asset == null || !(asset.getSpaceId() > 0)) {
            return false;
        }
        BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(asset.getSpaceId());
        long resourceSiteID = baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE ?
                baseSpace.getId() : baseSpace.getSiteId();

        return resourceSiteID != oldWo.getSiteId();
    }

    private boolean openChildWorkOrderBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) throws Exception {
        if (wo.getStatus().getType() != FacilioStatus.StatusType.CLOSED) {
            return false;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<V3WorkOrderContext> builder = new SelectRecordsBuilder<V3WorkOrderContext>()
                .beanClass(V3WorkOrderContext.class)
                .module(modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER))
                .select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
                .innerJoin("TicketStatus")
                .on("TicketStatus.ID = Tickets.STATUS_ID")
                .andCondition(CriteriaAPI.getCondition("PARENT_WOID", "parentWO", String.valueOf(oldWo), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("STATUS_TYPE", "statusType", String.valueOf(2), NumberOperators.NOT_EQUALS));
        List<V3WorkOrderContext> workOrderContexts = builder.get();
        return CollectionUtils.isNotEmpty(workOrderContexts);
    }

    private boolean closureSignatureBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) {
        if (wo.getStatus().getType() != FacilioStatus.StatusType.CLOSED) {
            return false;
        }
        return oldWo.isUserSignatureRequired() && wo.getSignature() == null;
    }

    // WO with open tasks cannot be closed.
    private boolean openTasksBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) throws Exception {
        if (wo.getStatus().getType() != FacilioStatus.StatusType.CLOSED) {
            return false;
        }
        if (wo.getNoOfTasks() == null) {
            return false;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = ModuleFactory.getTasksModule();
        List<FacilioField> fields = modBean.getAllFields("task");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(Collections.singletonList(FieldFactory.getField("closedTasks", "COUNT(Tasks.ID)", FieldType.NUMBER)))
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("parentTicketId"), String.valueOf(oldWo.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("statusNew"), "2", NumberOperators.EQUALS));

        List<Map<String, Object>> task = builder.get();
        if (FacilioUtil.isEmptyOrNull(task)) {
            return false;
        }
        long closedTaskCount = ((Number) task.get(0).get("closedTasks")).longValue();
        return closedTaskCount != wo.getNoOfTasks();
    }

    private boolean parentWoValidityBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) throws Exception {
        if (wo.getParentWO() == null) {
            return false;
        }
        V3WorkOrderContext parentWO = V3WorkOderAPI.getWorkOrder(wo.getParentWO().getId());
        FacilioStatus parentWOStatus = TicketAPI.getStatus(parentWO.getStatus().getId());
        FacilioStatus woStatus = TicketAPI.getStatus(wo.getStatus().getId());

        return (woStatus.getType() == parentWOStatus.getType()) &&
                (parentWOStatus.getType() == FacilioStatus.StatusType.CLOSED);
    }

    private boolean tenantPresent(V3WorkOrderContext wo) {
        return wo.getTenant() != null && wo.getTenant().getId() > 0;
    }

    private boolean tenantChanged(V3WorkOrderContext wo, V3WorkOrderContext oldWo) {
        if (oldWo.getTenant() == null) {
            return true;
        }
        return wo.getTenant().getId() != oldWo.getTenant().getId();
    }

    // Tenant belonging to a different site of WO cannot be added as a tenant
    private boolean foreignTenantBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) throws Exception {
        if (tenantPresent(wo) && tenantChanged(wo, oldWo)) {
            TenantContext newTenant = TenantsAPI.getTenant(wo.getTenant().getId());
            return newTenant == null || newTenant.getSiteId() != oldWo.getSiteId();
        }
        return false;
    }
    // site cannot be changed for WOs
    private boolean siteChangeBreach(V3WorkOrderContext wo, V3WorkOrderContext oldWo) {
        return wo.getSiteId() != oldWo.getSiteId();
    }
}
