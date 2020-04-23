package com.facilio.energystar.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarCustomerContext.Data_Exchange_Mode;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.context.EnergyStarProperyUseContext;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;

public class EnergyStarUtil {
	
	public static final String ENERGY_STAR_CUSTOMER_CONTEXT = "energyStarCustomerContext";
	
	public static final String ENERGY_STAR_PROPERTY_CONTEXT = "energyStarPropertyContext";
	
	public static final String ENERGY_STAR_PROPERTY_USE_CONTEXTS = "energyStarPropertyUseContext";
	
	public static final String ENERGY_STAR_METER_CONTEXTS = "energyStarMeterContext";
	
	
	public static EnergyStarCustomerContext getEnergyStarCustomer() throws Exception {
		List<FacilioField> fields = FieldFactory.getEnergyStarCustomerFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getEnergyStarCustomerModule().getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getEnergyStarCustomerModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			EnergyStarCustomerContext energyStarCustomerContext = FieldUtil.getAsBeanFromMap(props.get(0), EnergyStarCustomerContext.class);
			return energyStarCustomerContext;
		}
		return null;
	}
	
	private static String getUserNameFromOrgName(String orgName) {
		orgName =  orgName.replace(" ", "");
		return orgName;
	}
	private static String getPasswordFromUserName(String userName) {
		userName =  userName + "Facilio@123";
		return userName;
	}
	
	public static EnergyStarCustomerContext getEnergyStarCustomerContext() {
		
		EnergyStarCustomerContext customerContext = new EnergyStarCustomerContext();
		
		Organization org = AccountUtil.getCurrentOrg();
		
		customerContext.setOrgId(org.getId());
		customerContext.setCreatedTime(DateTimeUtil.getCurrenTime());
		customerContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
		customerContext.setUserName(getUserNameFromOrgName(org.getName()));
		customerContext.setPassword(getPasswordFromUserName(customerContext.getUserName()));
		
		List<Data_Exchange_Mode> exchangeModeList = customerContext.getAvailableDataExchangeModes() == null ? new ArrayList<>() : customerContext.getAvailableDataExchangeModes();
		
		exchangeModeList.add(Data_Exchange_Mode.ELECTRIC);
		
		customerContext.setAvailableDataExchangeModes(exchangeModeList);
		
		long total = 0;
		if(customerContext.getAvailableDataExchangeModes() != null) {
			for(Data_Exchange_Mode dataExchangeMode :customerContext.getAvailableDataExchangeModes()) {
				total = total | dataExchangeMode.getLicence();
			}
		}
		
		customerContext.setDataExchangeMode(total);
		
		return customerContext;
	}
	
	public static EnergyStarCustomerContext addEnergyStarCustomer(EnergyStarCustomerContext customerContext) throws Exception {
		
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getEnergyStarCustomerModule().getTableName())
				.fields(FieldFactory.getEnergyStarCustomerFields());

		Map<String, Object> props = FieldUtil.getAsProperties(customerContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		customerContext.setId((Long) props.get("id"));
		return customerContext;
	}
	
	public static EnergyStarPropertyContext addEnergyStarProperty(EnergyStarPropertyContext energyStarPropertyContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getEnergyStarPropertyModule().getTableName())
				.fields(FieldFactory.getEnergyStarPropertyFields());

		Map<String, Object> props = FieldUtil.getAsProperties(energyStarPropertyContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		energyStarPropertyContext.setId((Long) props.get("id"));
		return energyStarPropertyContext;
	}
	
	public static EnergyStarProperyUseContext addEnergyStarPropertyUse(EnergyStarProperyUseContext energyStarProperyUseContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getEnergyStarPropertyUseModule().getTableName())
				.fields(FieldFactory.getEnergyStarPropertyUseFields());

		Map<String, Object> props = FieldUtil.getAsProperties(energyStarProperyUseContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		energyStarProperyUseContext.setId((Long) props.get("id"));
		return energyStarProperyUseContext;
	}
	
	public static EnergyStarMeterContext addEnergyStarMetercontext(EnergyStarMeterContext energyStarMeterContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getEnergyStarMeterModule().getTableName())
				.fields(FieldFactory.getEnergyStarMeterFields());

		Map<String, Object> props = FieldUtil.getAsProperties(energyStarMeterContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		energyStarMeterContext.setId((Long) props.get("id"));
		return energyStarMeterContext;
	}
	
	public static int updateEnergyStarRelModule(FacilioModule module,List<FacilioField> fields,Object context,Criteria updateCriteria) throws Exception {
		
		GenericUpdateRecordBuilder updatetBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCriteria(updateCriteria);

		Map<String, Object> props = FieldUtil.getAsProperties(context);
		return updatetBuilder.update(props);

	}
	
	public static int deleteEnergyStarRelated(FacilioModule module,Criteria deleteCriteria) throws Exception {
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCriteria(deleteCriteria);

		return deleteBuilder.delete();
	}
}
