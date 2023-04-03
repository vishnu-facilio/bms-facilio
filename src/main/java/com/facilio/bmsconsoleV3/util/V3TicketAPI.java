package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsoleV3.context.V3TaskContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class V3TicketAPI {

    private static Logger LOGGER = LogManager.getLogger(V3TicketAPI.class.getName());


    public static void associateTenant (V3TicketContext ticket) throws Exception {
        if (ticket.getResource() != null && ticket.getResource().getId() != -1) {
            if (ticket.getTenant() != null && ticket.getTenant().getId() > 0 ) {
                if (AccountUtil.getCurrentOrg().getOrgId() != 320l) {
                    List<V3TenantContext> tenants = V3TenantsAPI.getAllTenantsForResource(ticket.getResource().getId());
                    if (tenants == null || tenants.stream().noneMatch(tenant -> tenant.getId() == ticket.getTenant().getId())) {
                        throw new RESTException(ErrorCode.VALIDATION_ERROR, "The tenant associated doesn't belong to the workorder space/asset");
                    }
                }
            }
            else if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.TENANTS)) {
                V3TenantContext tenant = V3TenantsAPI.getTenantForResource(ticket.getResource().getId());
                ticket.setTenant(tenant);
            }
        }
    }

    public static <T extends V3TicketContext> void validateSiteSpecificData(T ticket, List<T> oldTickets) throws Exception {
        boolean isWorkOrder = ticket instanceof V3WorkOrderContext;
        if (ticket.getResource() != null) {
            ResourceContext resource = ResourceAPI.getResource(ticket.getResource().getId());
            long resourceSiteId = -1;
            if (resource.getResourceTypeEnum() == ResourceContext.ResourceType.SPACE) {
                BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(resource.getId());
                if (baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
                    resourceSiteId = baseSpace.getId();
                } else {
                    resourceSiteId = baseSpace.getSiteId();
                }
            } else {
                AssetContext asset = AssetsAPI.getAssetInfo(resource.getId(), false); //check deleted ?
                if (asset.getSpaceId() > 0) {
                    BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(asset.getSpaceId());
                    if (baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
                        resourceSiteId = baseSpace.getId();
                    } else {
                        resourceSiteId = baseSpace.getSiteId();
                    }
                }
            }

            if (resourceSiteId > 0) {
                for(T oldWo: oldTickets) {
                    long siteId = oldWo.getSiteId();
                    if (resourceSiteId != siteId) {
                        if (resource.getResourceTypeEnum() == ResourceContext.ResourceType.SPACE) {
                            if (isWorkOrder) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Space - "+resource.getName()+" does not belong in the Workorder's Site.");
                            } else {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Space - "+resource.getName()+" does not belong in the Workorder request's Site.");
                            }

                        } else {
                            if (isWorkOrder) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Asset - "+resource.getName()+" does not belong in the Workorder's Site.");
                            } else {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Asset - "+resource.getName()+" does not belong in the Workorder request's Site.");
                            }
                        }
                    }
                }
            }
        } else if(ticket.getAssignedTo() != null || ticket.getAssignmentGroup() != null) {
            User assignedTo = ticket.getAssignedTo();
            Group assignmentGroup = ticket.getAssignmentGroup();
            long groupSiteId = -1;
            Set<Long> userSiteIds = new HashSet<>();
            if (ticket.getAssignedTo() != null && assignedTo.getOuid() > 0) {
                assignedTo = AccountUtil.getUserBean().getUser(assignedTo.getOuid(), true);
                List<Long> accessibleSpace = assignedTo.getAccessibleSpace();
                Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);
                if (accessibleSpace != null && !accessibleSpace.isEmpty()) {
                    for (long id: accessibleSpace) {
                        BaseSpaceContext space = idVsBaseSpace.get(id);
                        if (space.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
                            userSiteIds.add(space.getId());
                        } else {
                            userSiteIds.add(space.getSiteId());
                        }
                    }
                }
            }
            if (assignmentGroup != null && assignmentGroup.getId() > 0) {
                assignmentGroup = AccountUtil.getGroupBean().getGroup(assignmentGroup.getId());
                groupSiteId = assignmentGroup.getSiteId();
            }

            for (V3TicketContext oldWo: oldTickets) {
                long siteId = oldWo.getSiteId();
                if (groupSiteId > 0 && groupSiteId != siteId) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Team does not belong to current site.");
                }

                if (!userSiteIds.isEmpty() && !userSiteIds.contains(siteId)) {
                    throw new RESTException(ErrorCode.VALIDATION_ERROR, "The User does not belong to current site.");
                }
            }
        }
    }

    public static <T extends V3TicketContext> void validateSiteSpecificData(T ticket) throws Exception {
        long siteId = ticket.getSiteId();
        skipSiteValidation(ticket);

        if (siteId == -1) {
            return;
        }

        boolean isWorkOrder = ticket instanceof V3WorkOrderContext;
        if (ticket.getResource() != null && ticket.getResource().getId() != -1) {
            LOGGER.error("validateSiteSpecificData(): ID = " + ticket.getResource().getId());
            ResourceContext resource = ResourceAPI.getResource(ticket.getResource().getId());
            long resourceSiteId = -1;
            if (resource.getResourceTypeEnum() == ResourceContext.ResourceType.SPACE) {
                BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(resource.getId());
                if (baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
                    resourceSiteId = baseSpace.getId();
                } else {
                    resourceSiteId = baseSpace.getSiteId();
                }
            } else {
                AssetContext asset = AssetsAPI.getAssetInfo(resource.getId(), false); //check deleted ?
                if (asset.getSpaceId() > 0) {
                    BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(asset.getSpaceId(), true);
                    try {
                        if (baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
                            resourceSiteId = baseSpace.getId();
                        } else {
                            resourceSiteId = baseSpace.getSiteId();
                        }
                    } catch (NullPointerException ex) {
                        throw ex;
                    }

                }
            }

            if (resourceSiteId > 0) {
                if (resourceSiteId != siteId) {
                    if (resource.getResourceTypeEnum() == ResourceContext.ResourceType.SPACE) {
                        if (isWorkOrder) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Space does not belong in the Workorder's Site.");
                        } else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Space does not belong in the Workorder request's Site.");
                        }

                    } else {
                        if (isWorkOrder) {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Asset does not belong in the Workorder's Site.");
                        } else {
                            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Asset does not belong in the Workorder request's Site.");
                        }
                    }
                }
            }
        }

        User assignedTo = ticket.getAssignedTo();
        Group assignmentGroup = ticket.getAssignmentGroup();

        Set<Long> userSiteIds = new HashSet<>();
        if (assignedTo != null && assignedTo.getOuid() > 0) {
            assignedTo = AccountUtil.getUserBean().getUser(assignedTo.getOuid(), true);
            if (assignedTo != null) {
                List<Long> accessibleSpace = assignedTo.getAccessibleSpace();
                Map<Long, BaseSpaceContext> idVsBaseSpace = SpaceAPI.getBaseSpaceMap(accessibleSpace);
                if (accessibleSpace != null && !accessibleSpace.isEmpty()) {
                    for (long id : accessibleSpace) {
                        BaseSpaceContext space = idVsBaseSpace.get(id);
                        if (space.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SITE) {
                            userSiteIds.add(space.getId());
                        } else {
                            userSiteIds.add(space.getSiteId());
                        }
                    }
                }
            }
            else {
                ticket.setAssignedTo(null); //Setting assigned to as null if there's no such user
            }
        }

        long groupSiteId = -1;
        if (assignmentGroup != null && assignmentGroup.getId() != -1) {
            assignmentGroup = AccountUtil.getGroupBean().getGroup(assignmentGroup.getId());
            groupSiteId = assignmentGroup.getSiteId();
        }

        if (groupSiteId > 0 && groupSiteId != siteId) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The Team does not belong to current site.");
        }

        if (!userSiteIds.isEmpty() && !userSiteIds.contains(siteId)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "The User does not belong to current site.");
        }

    }

    private static <T extends V3TicketContext> void skipSiteValidation(T ticket) {
        V3TicketContext.SourceType sourceType = ticket.getSourceTypeEnum();
        if (sourceType == null) {
            if (AccountUtil.getCurrentSiteId() != -1) {
                ticket.setSiteId(AccountUtil.getCurrentSiteId());
            }
            return;
        }

        switch (sourceType) {
            case THRESHOLD_ALARM:
            case ANOMALY_ALARM:
            case ALARM:
            case ML_ALARM:
                return;
            default:
                if (AccountUtil.getCurrentSiteId() != -1) {
                    ticket.setSiteId(AccountUtil.getCurrentSiteId());
                }
                break;
        }
    }

    public static void updateTicketAssignedBy(V3TicketContext ticket) {
        if ((ticket.getAssignedTo() != null && ticket.getAssignedTo().getId() != -1) || (ticket.getAssignmentGroup() != null && ticket.getAssignmentGroup().getId() != -1)) {
            ticket.setAssignedBy(AccountUtil.getCurrentUser());
        }
    }

    public static void updateTicketAssignedBy(List<? extends V3TicketContext> ticketContexts) {
        if (ticketContexts == null || ticketContexts.isEmpty()) {
            return;
        }

        for (int i = 0; i < ticketContexts.size(); i++) {
            V3TicketContext ticketContext = ticketContexts.get(i);
            if ((ticketContext.getAssignedTo() == null || ticketContext.getAssignedTo().getId() == -1)
                    && (ticketContext.getAssignmentGroup() == null || ticketContext.getAssignmentGroup().getId() != -1)) {
                continue;
            }

            ticketContext.setAssignedBy(AccountUtil.getCurrentUser());
        }
    }


    public static TicketPriorityContext getPriority(long orgId, String priority) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<TicketPriorityContext> builder = new SelectRecordsBuilder<TicketPriorityContext>()
                .table("TicketPriority")
                .moduleName(FacilioConstants.ContextNames.TICKET_PRIORITY)
                .beanClass(TicketPriorityContext.class)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_PRIORITY))
                .andCustomWhere("ORGID = ? AND PRIORITY = ?", orgId, priority)
                .orderBy("ID");
        List<TicketPriorityContext> categories = builder.get();
        if(CollectionUtils.isNotEmpty(categories)) {
        	return categories.get(0);
        }
        return null;
    }

    public static FacilioStatus getStatus(long stateId) throws Exception {
        return getStatus(AccountUtil.getCurrentOrg().getId(), stateId);
    }

    public static FacilioStatus getStatus(long orgId, long id) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
                .table("TicketStatus")
                .moduleName("ticketstatus")
                .beanClass(FacilioStatus.class)
                .select(modBean.getAllFields("ticketstatus"))
                .andCustomWhere("ORGID = ? AND ID = ?", orgId, id)
                .orderBy("ID");
        List<FacilioStatus> statuses = builder.get();
        return statuses.get(0);
    }

    public static void updateTaskCount(Collection parentIds) throws Exception {
    	LOGGER.info("parentIds for update -- "+parentIds);
        if (CollectionUtils.isNotEmpty(parentIds)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            String moduleName = "task";

            FacilioField parentIdField = modBean.getField("parentTicketId", moduleName);
            FacilioModule module = modBean.getModule(moduleName);

            List<FacilioField> fields = new ArrayList<>();
            fields.add(parentIdField);

            FacilioField countField = new FacilioField();
            countField.setName("count");
            countField.setColumnName("COUNT(*)");
            countField.setDataType(FieldType.NUMBER);
            fields.add(countField);
            FacilioField preRequestField = new FacilioField();
            preRequestField.setName("IS_PRE_REQUEST");
            preRequestField.setColumnName("IS_PRE_REQUEST");
            preRequestField.setDataType(FieldType.BOOLEAN);

            Condition condition = CriteriaAPI.getCondition(parentIdField, parentIds, NumberOperators.EQUALS);
            Condition notPreRequestCondition = CriteriaAPI.getCondition(preRequestField, "1" , NumberOperators.NOT_EQUALS);
            GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                    .table(module.getTableName())
                    .select(fields)
                    .groupBy(parentIdField.getCompleteColumnName())
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                    .andCondition(condition).andCondition(notPreRequestCondition);

            List<Map<String, Object>> totalCountList = select.get();

            Map<Long, MutablePair<Integer, Integer>> updatedValues = new HashMap<>();
            for (Map<String, Object> map : totalCountList) {
                long id = ((Number) map.get("parentTicketId")).longValue();
                MutablePair<Integer, Integer> pair = new MutablePair<>();
                pair.setLeft(((Number) map.get("count")).intValue());
                updatedValues.put(id, pair);
            }

            FacilioField statusField = modBean.getField("statusNew", moduleName);
            Condition completedStatusCondition = CriteriaAPI.getCondition(statusField, NumberOperators.EQUALS);
            completedStatusCondition.setValue(String.valueOf(2));
            select = new GenericSelectRecordBuilder()
                    .table(module.getTableName())
                    .select(fields)
                    .groupBy(parentIdField.getCompleteColumnName())
                    .andCondition(condition)
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
                    .andCondition(completedStatusCondition).andCondition(notPreRequestCondition);

            List<Map<String, Object>> completedCountList = select.get();

            for (Map<String, Object> map : completedCountList) {
                long id = ((Number) map.get("parentTicketId")).longValue();
                MutablePair<Integer, Integer> pair = updatedValues.get(id);
                if (pair == null) {
                    pair = new MutablePair<>();
                }
                pair.setRight(((Number) map.get("count")).intValue());
            }

            String ticketModuleName = "ticket";
            FacilioModule ticketModule = modBean.getModule(ticketModuleName);

            FacilioField noOfTasksField = new FacilioField();
            noOfTasksField.setName("noOfTasks");
            noOfTasksField.setColumnName("NO_OF_TASKS");
            noOfTasksField.setDataType(FieldType.NUMBER);

            FacilioField noOfClosedTasksField = new FacilioField();
            noOfClosedTasksField.setName("noOfClosedTasks");
            noOfClosedTasksField.setColumnName("NO_OF_CLOSED_TASKS");
            noOfClosedTasksField.setDataType(FieldType.NUMBER);

            for (Long id: updatedValues.keySet()) {
                Map<String, Object> updateMap = new HashMap<>();
                MutablePair<Integer,Integer> pair = updatedValues.get(id);

                updateMap.put("noOfTasks", pair.getLeft() == null ? 0 : pair.getLeft());
                updateMap.put("noOfClosedTasks", pair.getRight() == null ? 0 : pair.getRight());

                FacilioField idField = FieldFactory.getIdField(ticketModule);
                Condition idFieldCondition = CriteriaAPI.getCondition(idField, NumberOperators.EQUALS);
                idFieldCondition.setValue(String.valueOf(id));

                GenericUpdateRecordBuilder recordBuilder = new GenericUpdateRecordBuilder()
                        .table(ticketModule.getTableName())
                        .fields(Arrays.asList(noOfTasksField, noOfClosedTasksField))
//						.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ticketModule))
                        .andCondition(idFieldCondition);
                recordBuilder.update(updateMap);
                LOGGER.info("updateMap for update -- "+updateMap);
            }
        }
    }

    public static void loadWorkOrderLookups(Collection<? extends V3WorkOrderContext> workOrders) throws Exception {
        loadTicketStatus(workOrders);
        loadTicketPriority(workOrders);
        loadTicketCategory(workOrders);
        loadWorkOrdersUsers(workOrders);
        loadTicketGroups(workOrders);
        loadTicketResources(workOrders);
//        loadTicketTenants(workOrders);
//        loadTicketVendors(workOrders);
        loadTicketDetails(workOrders);
    }


    public static List<FacilioStatus> getAllStatus(FacilioModule module, boolean ignorePreOpen) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_STATUS);

        SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
                .moduleName(FacilioConstants.ContextNames.TICKET_STATUS)
                .beanClass(FacilioStatus.class)
                .andCondition(CriteriaAPI.getCondition("PARENT_MODULEID", "parentModuleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS))
                .select(fields);

        if (modBean.getField("moduleState", FacilioConstants.ContextNames.WORK_ORDER) != null) {
            ignorePreOpen = false;
        }
        if (ignorePreOpen) {
            FacilioField typeField = FieldFactory.getAsMap(fields).get("typeCode");
            builder.andCondition(CriteriaAPI.getCondition(typeField, String.valueOf(FacilioStatus.StatusType.PRE_OPEN.getIntVal()), NumberOperators.NOT_EQUALS));
        }

        return builder.get();
    }

    @Deprecated
    public static List<FacilioStatus> getAllStatus(boolean ignorePreOpen) throws Exception {
        // Default method to get workorder states
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule workorderModule = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
        return getAllStatus(workorderModule, ignorePreOpen);
    }

    private static void loadTicketStatus(Collection<? extends V3TicketContext> tickets) throws Exception {
        if(tickets != null && !tickets.isEmpty()) {
            try {
                List<FacilioStatus> allStatus = getAllStatus(false);
                Map<Long, FacilioStatus> statuses = allStatus.stream().collect(Collectors.toMap(FacilioStatus::getId, Function.identity()));

                for(V3TicketContext ticket : tickets) {
                    if (ticket != null) {
                        FacilioStatus status = ticket.getStatus();
                        if(status != null) {
                            ticket.setStatus(statuses.get(status.getId()));
                        }
                    }
                }
            }
            catch(Exception e) {
                LOGGER.error("Exception occurred ", e);
                throw e;
            }
        }
    }

    private static void loadTicketPriority(Collection<? extends V3TicketContext> tickets) throws Exception {
        if(tickets != null && !tickets.isEmpty()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_PRIORITY);

            try{
                SelectRecordsBuilder<TicketPriorityContext> selectBuilder = new SelectRecordsBuilder<TicketPriorityContext>()
                        .select(fields)
                        .table("TicketPriority")
                        .moduleName(FacilioConstants.ContextNames.TICKET_PRIORITY)
                        .beanClass(TicketPriorityContext.class);
                Map<Long, TicketPriorityContext> priorities = selectBuilder.getAsMap();

                for (V3TicketContext ticket : tickets) {
                    if (ticket != null) {
                        TicketPriorityContext priority = ticket.getPriority();
                        if (priority != null) {
                            ticket.setPriority(priorities.get(priority.getId()));
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Exception occurred ", e);
                throw e;
            }
        }
    }

    private static void loadTicketDetails(Collection<? extends V3TicketContext> workorders) throws Exception {

        if (workorders == null || workorders.isEmpty()) {
            return;
        }

        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET);

            SelectRecordsBuilder<V3TicketContext> selectBuilder = new SelectRecordsBuilder<V3TicketContext>()
                    .select(fields)
                    .table("Tickets")
                    .moduleName(FacilioConstants.ContextNames.TICKET)
                    .beanClass(V3TicketContext.class);
            Map<Long, V3TicketContext> tickets = selectBuilder.getAsMap();

            for (V3TicketContext workorder : workorders) {
                if (workorder != null) {
                    V3TicketContext ticket = tickets.get(workorder.getId());
                    if (ticket == null) {
                        continue;
                    }
                    // scheduled work duration
                    workorder.setScheduledStart(ticket.getScheduledStart() != null ? ticket.getScheduledStart() : -1);
                    workorder.setEstimatedEnd(ticket.getEstimatedEnd() != null ? ticket.getEstimatedEnd() : null);
                    // actual work duration
                    workorder.setActualWorkEnd(ticket.getActualWorkEnd() != null ? ticket.getActualWorkEnd() : null);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred ", e);
            throw e;
        }

    }

    private static void loadTicketCategory(Collection<? extends V3TicketContext> tickets) throws Exception {
        if (tickets != null && !tickets.isEmpty()) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TICKET_CATEGORY);

            try {
                SelectRecordsBuilder<TicketCategoryContext> selectBuilder = new SelectRecordsBuilder<TicketCategoryContext>()
                        .select(fields)
                        .table("TicketCategory")
                        .moduleName(FacilioConstants.ContextNames.TICKET_CATEGORY)
                        .beanClass(TicketCategoryContext.class);
                Map<Long, TicketCategoryContext> categories = selectBuilder.getAsMap();

                for(V3TicketContext ticket : tickets) {
                    if (ticket != null) {
                        TicketCategoryContext category = ticket.getCategory();
                        if(category != null) {
                            ticket.setCategory(categories.get(category.getId()));
                        }
                    }
                }
            }
            catch(Exception e) {
                LOGGER.error("Exception occurred ", e);
                throw e;
            }
        }
    }

    private static void loadTicketUsers(Collection<? extends V3TicketContext> tickets) throws Exception {
        if(tickets != null && !tickets.isEmpty()) {
            List<User> users = AccountUtil.getOrgBean().getOrgUsers(AccountUtil.getCurrentOrg().getOrgId(), true);

            Map<Long, User> userMap = new HashMap<>();
            for(User user : users) {
                userMap.put(user.getId(), user);
            }

            for(V3TicketContext ticket : tickets) {
                if (ticket != null) {
                    User assignTo = ticket.getAssignedTo();
                    if(assignTo != null) {
                        ticket.setAssignedTo(userMap.get(assignTo.getId()));
                    }
                }
            }
        }
    }

    private static void loadWorkOrdersUsers(Collection<? extends V3WorkOrderContext> workOrders) throws Exception {
        if(workOrders != null && !workOrders.isEmpty()) {
            Set<Long> userId = new HashSet<>();
            for(V3WorkOrderContext workOrder : workOrders) {
                if (workOrder != null) {
                    if (workOrder.getAssignedTo() != null) {
                        userId.add(workOrder.getAssignedTo().getId());
                    }
                    if (workOrder.getRequestedBy() != null) {
                        userId.add(workOrder.getRequestedBy().getId());
                    }
                    if (workOrder.getRequester() != null) {
                        userId.add(workOrder.getRequester().getId());
                    }

                }
            }
            if (!userId.isEmpty()) {
                List<Long> userIdList = new ArrayList<Long>();
                userIdList.addAll(userId);

                Map<Long, User> userMap = AccountUtil.getUserBean().getUsersAsMap(null, userIdList);

                for(V3WorkOrderContext workOrder : workOrders) {
                    if (workOrder != null) {
                        User assignTo = workOrder.getAssignedTo();
                        if(assignTo != null) {
                            workOrder.setAssignedTo(userMap.get(assignTo.getId()));
                        }
                        User requesterBy = workOrder.getRequestedBy();
                        if(requesterBy != null) {
                            workOrder.setRequestedBy(userMap.get(requesterBy.getId()));
                        }
                        User requester = workOrder.getRequester();
                        if(requester != null) {
                            workOrder.setRequester(userMap.get(requester.getId()));
                        }
                    }
                }
            }

        }
    }

    private static void loadTicketGroups(Collection<? extends V3TicketContext> tickets) throws Exception {
        if(tickets != null && !tickets.isEmpty()) {
            List<Group> groups = AccountUtil.getGroupBean().getOrgGroups(AccountUtil.getCurrentOrg().getOrgId(), true, false);

            Map<Long, Group> groupMap = new HashMap<>();
            for(Group group : groups) {
                groupMap.put(group.getId(), group);
            }

            for(V3TicketContext ticket : tickets) {
                if (ticket != null) {
                    Group assignGroup = ticket.getAssignmentGroup();
                    if(assignGroup != null) {
                        ticket.setAssignmentGroup(groupMap.get(assignGroup.getId()));
                    }
                }
            }
        }
    }

    private static void loadTicketResources(Collection<? extends V3TicketContext> tickets) throws Exception {
        if(tickets != null && !tickets.isEmpty()) {
            List<Long> resourceIds = tickets.stream()
                    .filter(ticket -> ticket != null && ticket.getResource() != null)
                    .map(ticket -> ticket.getResource().getId())
                    .collect(Collectors.toList());
            Map<Long, ResourceContext> resources = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, true, true);
            if(resources != null && !resources.isEmpty()) {
                for(V3TicketContext ticket : tickets) {
                    if (ticket != null) {
                        ResourceContext resource = ticket.getResource();
                        if(resource != null) {
                            ResourceContext resourceDetail = resources.get(resource.getId());
                            ticket.setResource(resourceDetail);
                        }
                    }
                }
            }
        }
    }

    private static void loadTicketTenants(Collection<? extends V3TicketContext> tickets) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);

        if(tickets != null && !tickets.isEmpty()) {
            SelectRecordsBuilder<V3TenantContext> builder = new SelectRecordsBuilder<V3TenantContext>()
                    .module(module)
                    .beanClass(V3TenantContext.class)
                    .select(modBean.getAllFields(FacilioConstants.ContextNames.TENANT))
                    ;
            List<V3TenantContext> tenantList = builder.get();
            if(tenantList.size() > 0) {
                V3TenantsAPI.loadTenantLookups(tenantList);
                Map<Long, V3TenantContext> tenants = FieldUtil.getAsMap(tenantList);
                for(V3TicketContext ticket : tickets) {
                    if (ticket != null) {
                        V3TenantContext tenant = ticket.getTenant();
                        if (tenant != null) {
                            V3TenantContext tenantDetail = tenants.get(tenant.getId());
                            ticket.setTenant(tenantDetail);
                        }
                    }
                }
            }
        }
    }

    private static boolean isInvesta() {
        Organization org = AccountUtil.getCurrentOrg();
        return org != null && org.getOrgId() == 17L;
    }

    private static void loadTicketVendors(Collection<? extends V3TicketContext> tickets) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDORS);

        if (tickets != null && !tickets.isEmpty()) {
            SelectRecordsBuilder<V3VendorContext> builder = new SelectRecordsBuilder<V3VendorContext>()
                    .module(module)
                    .beanClass(V3VendorContext.class)
                    .select(modBean.getAllFields(FacilioConstants.ContextNames.VENDORS))
                    ;

             List<V3VendorContext> vendorList = builder.get();

            if (vendorList.size() > 0) {
                Map<Long, V3VendorContext> vendors = FieldUtil.getAsMap(vendorList);
                for (V3TicketContext ticket : tickets) {

                    if (ticket != null) {
                        V3VendorContext vendor = ticket.getVendor();
                        if (vendor != null) {
                            V3VendorContext tenantDetail = vendors.get(vendor.getId());
                            ticket.setVendor(tenantDetail);
                        }
                    }
                }
            }
        }
    }

    public static void loadRelatedModules(V3TicketContext ticket) throws Exception {
        if(ticket != null) {
            ticket.setTaskSections(getRelatedTaskSections(ticket.getId()));
            ticket.setTasks(groupTaskBySection(getRelatedTasks(ticket.getId())));
            ticket.setNotes(getRelatedNotes(ticket.getId()));
            ticket.setAttachments(getRelatedAttachments(ticket.getId()));
            if(ticket.getResource() != null)
            {
                ticket.setResource(ResourceAPI.getResource(ticket.getResource().getId(),true));
            }
        }
    }

    public static void loadPmV1IntoWorkOrderObject(List<V3WorkOrderContext> workOrderContexts) throws Exception {
        for(V3WorkOrderContext wo: workOrderContexts){
            if(wo.getPm() != null && wo.getPm().getId() > 0){
                PreventiveMaintenance pm = PreventiveMaintenanceAPI.getPM(wo.getPm().getId(), false);
                wo.setPm(pm);
            }
        }
    }

    public static List<NoteContext> getRelatedNotes(long ticketId) throws Exception
    {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TICKET_NOTES);

        SelectRecordsBuilder<NoteContext> selectBuilder = new SelectRecordsBuilder<NoteContext>()
                .select(modBean.getAllFields(FacilioConstants.ContextNames.TICKET_NOTES))
                .module(module)
                .beanClass(NoteContext.class)
                .andCustomWhere("PARENT_ID = ?", ticketId);

        return selectBuilder.get();
    }

    public static Map<Long, TaskSectionContext> getRelatedTaskSections(long parentTicketId) throws Exception {
        if(parentTicketId != -1) {
            FacilioModule module = ModuleFactory.getTaskSectionModule();
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .select(FieldFactory.getTaskSectionFields())
                    .table(module.getTableName())
                    .andCustomWhere("PARENT_TICKET_ID = ?", parentTicketId);

            return getTasksSectionsFromMapList(selectBuilder.get());

        }
        return null;
    }

    private static Map<Long, TaskSectionContext> getTasksSectionsFromMapList(List<Map<String, Object>> sectionProps) {
        Map<Long, TaskSectionContext> sections = new HashMap<>();
        if(sectionProps != null && !sectionProps.isEmpty()) {
            for(Map<String, Object> sectionProp : sectionProps) {
                TaskSectionContext section = FieldUtil.getAsBeanFromMap(sectionProp, TaskSectionContext.class);
                sections.put(section.getId(), section);
            }
        }
        return sections;
    }

    public static Map<Long, List<V3TaskContext>> groupTaskBySection(List<V3TaskContext> tasks) throws Exception {
        if(tasks != null && !tasks.isEmpty()) {
            Map<Long, List<V3TaskContext>> taskMap = new HashMap<>();
            for(V3TaskContext task : tasks) {
                List<V3TaskContext> taskList = taskMap.get(task.getSectionId());
                if (taskList == null) {
                    taskList = new ArrayList<>();
                    taskMap.put(task.getSectionId() == null ? -1 : task.getSectionId(), taskList);
                }
                taskList.add(task);
            }
            return taskMap;
        }
        return null;
    }

    public static List<V3TaskContext> getRelatedTasks(List<Long> ticketIds) throws Exception {
        return getRelatedTasks(ticketIds, true, false);
    }

    public static List<V3TaskContext> getRelatedTasks(List<Long> ticketIds,
                                                      boolean fetchChildren, boolean fetchResources) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields("task");
        FacilioField parentId = FieldFactory.getAsMap(fields).get("parentTicketId");

        SelectRecordsBuilder<V3TaskContext> builder = new SelectRecordsBuilder<V3TaskContext>()
                .table("Tasks")
                .moduleName(FacilioConstants.ContextNames.TASK)
                .beanClass(V3TaskContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition(parentId, ticketIds, NumberOperators.EQUALS))
                .orderBy("ID");

        List<V3TaskContext> tasks = builder.get();
        if (CollectionUtils.isEmpty(tasks)) {
            return tasks;
        }

        if (fetchChildren) {
            for(V3TaskContext task: tasks) {
                if (task.getLastReading() == null && task.getInputTypeEnum() == V3TaskContext.InputType.READING && task.getResource() != null) {
                    FacilioField readingField = modBean.getField(task.getReadingFieldId());
                    ReadingDataMeta meta = ReadingsAPI.getReadingDataMeta(task.getResource().getId(), readingField);
                    task.setLastReading(meta.getValue());
                }
            }
        }

        if (fetchResources) {
            List<Long> resourceIds = tasks.stream().filter(task -> task.getResource() != null && task.getResource().getId() > 0)
                    .map(task -> task.getResource().getId()).collect(Collectors.toList());
            if (!resourceIds.isEmpty()) {
                Map<Long, ResourceContext> resources = ResourceAPI.getResourceAsMapFromIds(resourceIds);
                for (V3TaskContext task : tasks) {
                    if (task.getResource() != null && task.getResource().getId() > 0 && resources.containsKey(task.getResource().getId())) {
                        task.setResource(resources.get(task.getResource().getId()));
                    }
                }
            }
        }
        setTaskInputData(tasks);
        return tasks;
    }

    public static void setTaskInputData(List<V3TaskContext> tasks) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        for (V3TaskContext task : tasks) {
            switch (task.getInputTypeEnum()) {
                case BOOLEAN:
                    BooleanField field = (BooleanField) modBean.getField(task.getReadingFieldId());
                    task.setReadingField(field);
                    task.setTruevalue(field.getTrueVal());
                    task.setFalsevalue(field.getFalseVal());
                    List<String> options = new ArrayList<>();
                    options.add(field.getTrueVal());
                    options.add(field.getFalseVal());
                    task.setOptions(options);
                    break;
            }
        }
    }

    public static List<AttachmentContext> getRelatedAttachments(long ticketId) throws Exception {
        return AttachmentsAPI.getAttachments(FacilioConstants.ContextNames.TICKET_ATTACHMENTS, ticketId);
    }

    public static List<V3TaskContext> getRelatedTasks(long ticketId) throws Exception {
        return getRelatedTasks(Collections.singletonList(ticketId));
    }


}
