<%@page import="java.util.ArrayList,java.util.Collections,java.util.List,java.util.Map,org.apache.commons.chain.Chain,org.apache.commons.chain.Command,org.apache.commons.chain.Context,org.apache.commons.collections4.CollectionUtils,org.apache.commons.lang3.exception.ExceptionUtils,org.apache.log4j.LogManager,org.json.simple.JSONObject,com.facilio.accounts.dto.Organization,com.facilio.accounts.util.AccountConstants,com.facilio.accounts.util.AccountUtil,com.facilio.activity.ActivityContext,com.facilio.beans.ModuleBean,com.facilio.bmsconsole.activity.AssetActivityType,com.facilio.bmsconsole.util.AssetsAPI,com.facilio.chain.FacilioChain,com.facilio.chain.FacilioContext,com.facilio.constants.FacilioConstants.ContextNames,com.facilio.db.builder.GenericSelectRecordBuilder,com.facilio.fw.BeanFactory,com.facilio.modules.FacilioModule,com.facilio.modules.FieldFactory,com.facilio.modules.FieldUtil,com.facilio.modules.InsertRecordBuilder"%>
<%

class AddAssetActivitiesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		Organization org = (Organization) context.get("organization");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(ContextNames.ASSET);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(Collections.singletonList(FieldFactory.getIdField(module)));
		
		List<Map<String, Object>> props = builder.get();
		
		if (CollectionUtils.isNotEmpty(props)) {
			List<ActivityContext> activities = new ArrayList<>();
			for(Map<String, Object> prop: props) {
				long id = (long) prop.get("id");
				
				ActivityContext activity = new ActivityContext();
				activity.setParentId(id);
				activity.setTtime(org.getCreatedTime());
				activity.setDoneBy(AccountUtil.getCurrentUser());
				activity.setType(AssetActivityType.ADD);
				activity.setInfo(new JSONObject());
				activities.add(activity);
				
			}
			
			FacilioModule activityModule = modBean.getModule(ContextNames.ASSET_ACTIVITY);
			InsertRecordBuilder<ActivityContext> insertBuilder = new InsertRecordBuilder<ActivityContext>()
					.fields(modBean.getAllFields(activityModule.getName()))
					.module(activityModule)
					.addRecords(activities)
					;
			insertBuilder.save();
			
		}
		
		return false;
	}
	
}

try {
	GenericSelectRecordBuilder orgBuilder = new GenericSelectRecordBuilder()
			.select(AccountConstants.getOrgFields())
			.table("Organizations");
	
	List<Organization> orgs = new ArrayList<>();
	List<Map<String, Object>> orgProps = orgBuilder.get();
	if (orgProps != null && !orgProps.isEmpty()) {
		for(Map<String, Object> prop : orgProps) {
			orgs.add(FieldUtil.getAsBeanFromMap(prop, Organization.class));
		}
	}
	
	
	for(Organization org: orgs) {
		
		AccountUtil.cleanCurrentAccount();
		AccountUtil.setCurrentAccount(org.getId());
		if (AccountUtil.getCurrentAccount() == null) {
			continue;
		}
		try {
			
			FacilioContext context = new FacilioContext();
			context.put("organization", org);

			Chain chain = FacilioChain.getTransactionChain();
			chain.addCommand(new AddAssetActivitiesCommand());
			chain.execute(context);
		}
		catch (Exception e) {
			LogManager.getLogger(AssetsAPI.class.getName()).error("Exception occured on migration of asset add activity for org " + org.getOrgId() , e);
			throw e;
		}
		
		System.out.println("Org done --- " + org.getName() + ", id - " + org.getId());

	}
	
	
}
catch(Exception e) {
	System.out.println("Exception - " + ExceptionUtils.getStackTrace(e));
	LogManager.getLogger(AssetsAPI.class.getName()).error("Exception occured on migration of asset activity add: - ", e);
}


%>