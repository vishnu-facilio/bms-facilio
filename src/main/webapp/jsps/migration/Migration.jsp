
<%@page import="com.facilio.constants.FacilioConstants.TicketStatus"%>
<%@page import="com.facilio.chain.FacilioContext"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="java.util.Collections"%>
<%@page import="com.facilio.db.criteria.operators.NumberOperators"%>
<%@page import="com.facilio.db.criteria.CriteriaAPI"%>
<%@page import="com.facilio.modules.FieldUtil"%>
<%@page import="com.facilio.db.builder.GenericUpdateRecordBuilder"%>
<%@page import="com.facilio.bmsconsole.util.TenantsAPI"%>
<%@ page import="com.facilio.bmsconsole.commands.FacilioCommand" %>
<%@page import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext.TransitionType"%>
<%@page import="com.facilio.bmsconsole.util.TicketAPI"%>
<%@ page import="org.apache.commons.chain.Context" %>
<%@page import="com.facilio.bmsconsole.util.WorkflowRuleAPI"%>
<%@page import="com.facilio.bmsconsole.workflow.rule.EventType"%>
<%@ page import="org.apache.log4j.Logger" %>
<%@page import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType"%>
<%@ page import="org.apache.log4j.LogManager" %>
<%@ page import="com.facilio.accounts.dto.Organization" %>
<%@ page import="com.facilio.modules.FacilioStatus" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext" %>
<%@page import="org.apache.commons.collections4.CollectionUtils"%>
<%@page import="com.facilio.bmsconsole.tenant.TenantContext"%>
<%@page import="com.facilio.modules.SelectRecordsBuilder"%>
<%@ page import="com.facilio.bmsconsole.workflow.rule.StateFlowRuleContext" %>
<%@ page import="com.facilio.bmsconsole.workflow.rule.StateflowTransitionContext" %>
<%@ page import="java.util.List" %>
<%@ page import="com.facilio.db.builder.GenericInsertRecordBuilder" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.facilio.modules.ModuleFactory" %>
<%@ page import="com.facilio.accounts.util.AccountUtil" %>
<%@ page import="com.facilio.chain.FacilioChain" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>

<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="com.facilio.modules.fields.BooleanField" %>
<<<<<<< HEAD
<%@ page import="com.facilio.bmsconsole.commands.util.CommonCommandUtil" %>
=======
<%@ page import="com.facilio.constants.FacilioConstants" %>
<%@ page import="com.facilio.modules.fields.LookupField" %>
<%@ page import="com.facilio.modules.FieldType" %>
<%@ page import="com.facilio.modules.fields.NumberField" %>
<%@ page import="com.facilio.beans.ModuleBean" %>
<%@ page import="com.facilio.fw.BeanFactory" %>
<%@ page import="com.facilio.modules.FacilioModule" %>
<%@ page import="com.facilio.bmsconsole.tenant.TenantSpaceContext" %>
<%@ page import="com.facilio.modules.fields.FacilioField" %>
<%@ page import="com.facilio.modules.FacilioStatus.StatusType" %>
<%@ page import="com.facilio.modules.UpdateRecordBuilder" %>
<%@ page import="com.facilio.bmsconsole.util.TenantsAPI" %>
<%@ page import="com.facilio.bmsconsole.tenant.TenantContext" %>
<%@ page import="com.facilio.modules.SelectRecordsBuilder" %>
>>>>>>> tenant state flow related changes

<%--

  _____                      _          _                              _   _            _                       __                           _
 |  __ \                    | |        | |                            | | | |          | |                     / _|                         | |
 | |  | | ___    _ __   ___ | |_    ___| |__   __ _ _ __   __ _  ___  | |_| |__   ___  | |__   __ _ ___  ___  | |_ ___  _ __ _ __ ___   __ _| |_
 | |  | |/ _ \  | '_ \ / _ \| __|  / __| '_ \ / _` | '_ \ / _` |/ _ \ | __| '_ \ / _ \ | '_ \ / _` / __|/ _ \ |  _/ _ \| '__| '_ ` _ \ / _` | __|
 | |__| | (_) | | | | | (_) | |_  | (__| | | | (_| | | | | (_| |  __/ | |_| | | |  __/ | |_) | (_| \__ \  __/ | || (_) | |  | | | | | | (_| | |_
 |_____/ \___/  |_| |_|\___/ \__|  \___|_| |_|\__,_|_| |_|\__, |\___|  \__|_| |_|\___| |_.__/ \__,_|___/\___| |_| \___/|_|  |_| |_| |_|\__,_|\__|
                                                           __/ |
                                                          |___/

--%>

<%
    final class OrgLevelMigrationCommand extends FacilioCommand {
        private final Logger LOGGER = LogManager.getLogger(OrgLevelMigrationCommand.class.getName());
        @Override
        public boolean executeCommand(Context context) throws Exception {

            // Have migration commands for each org
            // Transaction is only org level. If failed, have to continue from the last failed org and not from first
			
            CommonCommandUtil.migrateFieldAccessType();

            try{	
            	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule tenantModule = modBean.getModule("tenant");
                FacilioModule ticketStatusModule = modBean.getModule("ticketstatus");
                if (tenantModule != null) {
                    LookupField moduleStateLF = new LookupField(tenantModule, "moduleState", "Module State", FacilioField.FieldDisplayType.LOOKUP_SIMPLE, "MODULE_STATE", FieldType.LOOKUP, true, false, true, false, ticketStatusModule);
                    modBean.addField(moduleStateLF);
                    NumberField stateFlowNF = new NumberField(tenantModule, "stateFlowId", "State Flow Id", FacilioField.FieldDisplayType.NUMBER, "STATE_FLOW_ID", FieldType.NUMBER, true, false, true, false);
                    modBean.addField(stateFlowNF);
                    FacilioModule tenantFieldUpdate = new FacilioModule();
                    tenantFieldUpdate.setModuleId(tenantModule.getModuleId());
                    tenantFieldUpdate.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                    tenantFieldUpdate.setStateFlowEnabled(true);
                    modBean.updateModule(tenantFieldUpdate);
                          
    /*                 Default entry for Tenant stateflow
     */                
                    FacilioStatus active = new FacilioStatus();
                    active.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                    active.setModuleId(ticketStatusModule.getModuleId());
                    active.setStatus("Active");
                    active.setDisplayName("Active");
                    active.setParentModuleId(tenantModule.getModuleId());
                    active.setTypeCode(1);
                   	TicketAPI.addStatus(active, tenantModule);
                    
                    FacilioStatus expired = new FacilioStatus();
                    expired.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                    expired.setModuleId(ticketStatusModule.getModuleId());
                    expired.setStatus("Expired");
                    expired.setDisplayName("Expired");
                    expired.setParentModuleId(tenantModule.getModuleId());
                    expired.setRecordLocked(true);
                    expired.setTypeCode(5);
                    TicketAPI.addStatus(expired, tenantModule);
                   	
        			
/*     				Default Active
 */        			
 
 
				 	StateFlowRuleContext defaultStateFlowRule = new StateFlowRuleContext();
					defaultStateFlowRule.setName("Default Tenant Stateflow");
					defaultStateFlowRule.setExecutionOrder(1);
					defaultStateFlowRule.setStatus(true);
					defaultStateFlowRule.setRuleType(RuleType.STATE_FLOW);
					defaultStateFlowRule.setLatestVersion(true);
					defaultStateFlowRule.setModuleId(tenantModule.getModuleId());
					defaultStateFlowRule.setActivityType(EventType.CREATE);
					defaultStateFlowRule.setDefaultStateId(active.getId());
					defaultStateFlowRule.setDefaltStateFlow(true);
					WorkflowRuleAPI.addWorkflowRule(defaultStateFlowRule);

/*     				--expired to active 
 */    				StateflowTransitionContext activeExpiredStateFlow = new StateflowTransitionContext();
    				activeExpiredStateFlow.setName("Make Active");
    				activeExpiredStateFlow.setRuleType(RuleType.STATE_RULE);
    				activeExpiredStateFlow.setLatestVersion(true);
    				activeExpiredStateFlow.setModuleId(tenantModule.getModuleId());
    				activeExpiredStateFlow.setActivityType(EventType.STATE_TRANSITION);
    				activeExpiredStateFlow.setFromStateId(expired.getId());
    				activeExpiredStateFlow.setToStateId(active.getId());
    				activeExpiredStateFlow.setType(TransitionType.NORMAL);
    				activeExpiredStateFlow.setStateFlowId(defaultStateFlowRule.getId());
					WorkflowRuleAPI.addWorkflowRule(activeExpiredStateFlow);
 
    				
/*     				active to expired
 */    				
				 	StateflowTransitionContext expiredActiveStateFlow = new StateflowTransitionContext();
    				expiredActiveStateFlow.setName("Mark Expire");
    				expiredActiveStateFlow.setRuleType(RuleType.STATE_RULE);
    				expiredActiveStateFlow.setLatestVersion(true);
    				expiredActiveStateFlow.setModuleId(tenantModule.getModuleId());
    				expiredActiveStateFlow.setActivityType(EventType.STATE_TRANSITION);
    				expiredActiveStateFlow.setFromStateId(active.getId());
    				expiredActiveStateFlow.setToStateId(expired.getId());
    				expiredActiveStateFlow.setType(TransitionType.NORMAL);
    				expiredActiveStateFlow.setStateFlowId(defaultStateFlowRule.getId());
					WorkflowRuleAPI.addWorkflowRule(expiredActiveStateFlow);
 
    				
    				SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
    						.module(tenantModule)
    						.beanClass(TenantContext.class)
    						.select(modBean.getAllFields(FacilioConstants.ContextNames.TENANT))
    						;
    				List<TenantContext> tenants = builder.get();
    				System.out.print("test" + builder);
    				if (CollectionUtils.isNotEmpty(tenants)) {
    					
    					for(TenantContext tenant: tenants) {
    						if (tenant.getStatus() == -1 || tenant.getStatus() == 1) {
    							tenant.setModuleState(active);
    							tenant.setStateFlowId(defaultStateFlowRule.getId());
    						}
    						else {
    							tenant.setModuleState(expired);
    							tenant.setStateFlowId(defaultStateFlowRule.getId());
    						}
    						List<Long> spaceIds = new ArrayList<>();
    						TenantsAPI.updateTenant(tenant, spaceIds);
    					}
    					
    					
    					
    				}
 
                }
            }
            catch(Exception e) {
            		e.printStackTrace();
            }


            return false;
        }
    }
%>

<%
    List<Organization> orgs = AccountUtil.getOrgBean().getOrgs();
    for (Organization org : orgs) {
        AccountUtil.setCurrentAccount(org.getOrgId());
        FacilioChain c = FacilioChain.getTransactionChain();
        c.addCommand(new OrgLevelMigrationCommand());
        c.execute();

        AccountUtil.cleanCurrentAccount();
    }
%>