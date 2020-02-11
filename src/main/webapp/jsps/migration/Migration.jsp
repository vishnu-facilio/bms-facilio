<%@page import="com.facilio.logging.SysOutLogger"%>
<%@page import="com.facilio.bmsconsole.commands.FacilioChainFactory"%>
<%@page import="com.facilio.chain.FacilioChain"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="org.apache.commons.chain.Context"%>
<%@page import="org.apache.commons.collections4.CollectionUtils"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="com.facilio.accounts.dto.Organization"%>
<%@page import="com.facilio.accounts.util.AccountUtil"%>
<%@page import="com.facilio.beans.ModuleBean"%>
<%@page import="com.facilio.bmsconsole.commands.FacilioCommand"%>
<%@page import="com.facilio.bmsconsole.tenant.TenantContext"%>
<%@page import="com.facilio.bmsconsole.tenant.TenantSpaceContext"%>
<%@page import="com.facilio.db.builder.GenericInsertRecordBuilder"%>
<%@page import="com.facilio.db.builder.GenericSelectRecordBuilder"%>
<%@page import="com.facilio.db.criteria.CriteriaAPI"%>
<%@page import="com.facilio.db.criteria.operators.BooleanOperators"%>
<%@page import="com.facilio.db.criteria.operators.NumberOperators"%>
<%@page import="com.facilio.fw.BeanFactory"%>
<%@page import="com.facilio.modules.FacilioModule"%>
<%@page import="com.facilio.modules.FacilioModule.ModuleType"%>
<%@page import="com.facilio.modules.FieldFactory"%>
<%@page import="com.facilio.modules.FieldType"%>
<%@page import="com.facilio.modules.ModuleFactory"%>
<%@page import="com.facilio.modules.SelectRecordsBuilder"%>
<%@page import="com.facilio.modules.fields.FacilioField"%>
<%@page import="com.facilio.modules.fields.FacilioField.FieldDisplayType"%>
<%@page import="com.facilio.modules.fields.LookupField"%>
<%@page import="com.facilio.bmsconsole.util.TenantsAPI"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="org.apache.log4j.Logger"%>
<%
	final class MigrationCommand extends FacilioCommand {
		@Override
		public boolean executeCommand(Context context) throws Exception {
			final Logger LOGGER = LogManager.getLogger(TenantsAPI.class.getName());
			try{
			List<Organization> organizations = AccountUtil.getOrgBean().getOrgs();
			for (Organization org : organizations) {
				Long orgId = org.getOrgId();
				AccountUtil.cleanCurrentAccount();
				AccountUtil.setCurrentAccount(orgId);
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule tenantModule = modBean.getModule("tenant");
				FacilioModule tenantSpaceModule = modBean.getModule("tenantspaces");
				if (tenantModule != null && tenantSpaceModule == null) {
					
					tenantSpaceModule = new FacilioModule();
					tenantSpaceModule.setOrgId(orgId);
					tenantSpaceModule.setName("tenantspaces");
					tenantSpaceModule.setDisplayName("Tenant Spaces");
					tenantSpaceModule.setTableName("Tenant_spaces");
					tenantSpaceModule.setType(ModuleType.SUB_ENTITY);
					long tenantSpaceModuleId = modBean.addModule(tenantSpaceModule);
					tenantSpaceModule.setModuleId(tenantSpaceModuleId);
					
					LookupField spaceField = new LookupField(tenantSpaceModule, "space", "Space", FieldDisplayType.LOOKUP_SIMPLE, "SPACE", FieldType.LOOKUP, true, false, true, false,modBean.getModule("basespace"));
					modBean.addField(spaceField);
					
					LookupField tenantField = new LookupField(tenantSpaceModule, "tenant", "Tenant", FieldDisplayType.LOOKUP_POPUP, "TENANT_ID", FieldType.LOOKUP, true, false, true, false,tenantModule);
					modBean.addField(tenantField);
					
					/* modBean.addSubModule(tenantModule.getModuleId(), tenantSpaceModuleId); */
					FacilioModule contactModule = modBean.getModule("contact");
					if (contactModule != null) {
						modBean.addSubModule(tenantModule.getModuleId(), modBean.getModule("contact").getModuleId());
					}
					modBean.addSubModule(tenantModule.getModuleId(), modBean.getModule("workorder").getModuleId());
					
					
					// Data Migration
					FacilioField zoneField = FieldFactory.getField("zone", "ZONE_ID", tenantModule, FieldType.LOOKUP);
					List<FacilioField> fields = new ArrayList<>();
					fields.add(zoneField);
					SelectRecordsBuilder<TenantContext> builder = new SelectRecordsBuilder<TenantContext>()
						.module(tenantModule)
						.beanClass(TenantContext.class)
						.select(fields)
						;
					List<TenantContext> tenants = builder.get();
					System.out.print("test" + builder);
					if (CollectionUtils.isNotEmpty(tenants)) {
						List<Long> zoneIds = tenants.stream().filter(tenant -> tenant.getZone() != null && tenant.getZone().getId() != -1)
									.map(tenant -> tenant.getZone().getId()).collect(Collectors.toList());
						Map<Long, Long> zoneVsTenant = tenants.stream().filter(tenant -> tenant.getZone() != null && tenant.getZone().getId() != -1)
									.collect(Collectors.toMap(tenant -> tenant.getZone().getId(), tenant -> tenant.getId()));
						if (CollectionUtils.isNotEmpty(zoneIds)) {
							FacilioModule zoneRelModule = ModuleFactory.getZoneRelModule();
							GenericSelectRecordBuilder zoneBuilder = new GenericSelectRecordBuilder()
									.select(FieldFactory.getZoneRelFields()).table(zoneRelModule.getTableName())
									.andCondition(CriteriaAPI.getCondition("ZONE_ID", "zoneId", StringUtils.join(zoneIds, ","), NumberOperators.EQUALS))
									.andCondition(CriteriaAPI.getCondition("IS_IMMEDIATE", "isImmediate", Boolean.TRUE.toString(), BooleanOperators.IS))
									;
							List<Map<String, Object>> zoneRels = zoneBuilder.get();
							if (CollectionUtils.isNotEmpty(zoneRels)) {
								List<FacilioField> tenantSpaceFields = FieldFactory.getTenantSpacesFields();
								GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
										.table(tenantSpaceModule.getTableName())
										.fields(tenantSpaceFields)
										;
								for (Map<String, Object> zoneRel : zoneRels) {
									TenantSpaceContext tenantSpace = new TenantSpaceContext();
									tenantSpace.setTenantId(zoneVsTenant.get((long) zoneRel.get("zoneId")));
									tenantSpace.setSpace((long) zoneRel.get("zoneId"));
									Map<String, Object> prop = new HashMap<>();
									prop.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
									prop.put("tenantId", zoneVsTenant.get((long) zoneRel.get("zoneId")));
									prop.put("space", (long) zoneRel.get("basespaceId"));
									insertBuilder.addRecord(prop);
								}
								insertBuilder.save();
							}
						}
					}
				}
			}
		}
			catch(Exception e) {
				LOGGER.error("Error occurred", e);
				throw e;
			}
			return false;	
		}
	}
%>

<%
	FacilioChain c = FacilioChain.getTransactionChain();
	c.addCommand(new MigrationCommand());
	c.execute();
%>


