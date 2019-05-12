package com.facilio.license;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioField;
import com.facilio.modules.FieldUtil;
import com.facilio.license.LicenseContext.FacilioLicense;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LicenseApi {
	
	public static void createLicense(long orgId) throws Exception {
		
		List<FacilioField> fields = AccountConstants.getLicenseFields();

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(AccountConstants.getLicenseModule().getTableName())
				.fields(fields);
		
	

	       FacilioLicense[] licenses = FacilioLicense.values();
	       for(FacilioLicense license: licenses) {
	    	   Map<String, Object> props = new HashMap<>();
		   		props.put("orgId", orgId);
		   		props.put("license", license.getValue());
		   		props.put("totalLicense", license.getDefaultLicenses());
		   		insertBuilder.addRecord(props);
	       }
		
		insertBuilder.save();
		
	}
	
	public static void deleteLicense(long id) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(AccountConstants.getLicenseModule().getTableName())
				.andCustomWhere("id = ?", id);

		builder.delete();
	}
	
	public static boolean updateUsedLicense(FacilioLicense license) throws Exception {

		List<FacilioField> fields = AccountConstants.getLicenseFields();
		LicenseContext sample = getLicensecontext(license);
		Integer usedLicense = sample.getUsedLicense();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getLicenseModule().getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(AccountConstants.getLicenseModule()))
				.andCustomWhere("LICENSE = ?", license.getValue());

		Map<String, Object> props = new HashMap<>();
		props.put("usedLicense", ++usedLicense);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	public static LicenseContext getLicensecontext(FacilioLicense license) throws Exception {

		List<FacilioField> fields = AccountConstants.getLicenseFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getLicenseModule().getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(AccountConstants.getLicenseModule()))
				.andCustomWhere("LICENSE = ?", license.getValue());
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), LicenseContext.class);
		}
		return null;
	}
	
	public static boolean updateTotalLicense(FacilioLicense license, Integer totalLicense) throws Exception {

		List<FacilioField> fields = AccountConstants.getLicenseFields();
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(AccountConstants.getLicenseModule().getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(AccountConstants.getLicenseModule()))
				.andCustomWhere("LICENSE = ?", license.getValue());

		Map<String, Object> props = new HashMap<>();
		props.put("totalLicense", totalLicense);
		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}
	
	public static List<LicenseContext> getLicenseMap() throws Exception {
		
		List<FacilioField> fields = AccountConstants.getLicenseFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(AccountConstants.getLicenseModule().getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(AccountConstants.getLicenseModule()));
				
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			List<LicenseContext> licenses = new ArrayList<>();  
			for (Map<String, Object> prop : props) {
				LicenseContext licenseContext = FieldUtil.getAsBeanFromMap(prop, LicenseContext.class);
				licenses.add(licenseContext);
			}
			return licenses;
		}
		return null;
	}
}
