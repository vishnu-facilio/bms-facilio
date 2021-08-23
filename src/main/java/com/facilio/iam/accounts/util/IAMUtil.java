package com.facilio.iam.accounts.util;

import java.util.List;

import com.facilio.accounts.dto.IAMAccount;
import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.dto.Organization;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.cache.LRUCache;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.iam.accounts.bean.IAMOrgBean;
import com.facilio.iam.accounts.bean.IAMUserBean;

public class IAMUtil {
	
	public static IAMUserBean getUserBean() throws Exception {
		IAMUserBean userBean = (IAMUserBean) BeanFactory.lookup("IAMUserBean");
		return userBean;
	}

	public static IAMUserBean getTransactionalUserBean() throws Exception {
		return (IAMUserBean) TransactionBeanFactory.lookup("IAMUserBean");
	}

	public static IAMOrgBean getOrgBean() throws Exception {
		Object ob = BeanFactory.lookup("IAMOrgBean");
		IAMOrgBean orgBean = (IAMOrgBean) ob;
		return orgBean;
	}

	public static void dropUserSecurityPolicyCache(List<Long> userIds) {
		userIds.forEach(i -> LRUCache.getUserSecurityPolicyCache().remove(i+""));
	}

	/* Removing as there's no usage of the method
	public static void appendModuleNameInKey(String moduleName, String prefix, Map<String, Object> beanMap,
			Map<String, Object> placeHolders) throws Exception {
		if (beanMap != null) {
			if (moduleName != null && !moduleName.isEmpty() && !LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> fields = modBean.getAllFields(moduleName);

				if (fields != null && !fields.isEmpty()) {
					for (FacilioField field : fields) {
						if (field.getDataTypeEnum() == FieldType.LOOKUP && prefix.split("\\.").length < 5) {
							Map<String, Object> props = (Map<String, Object>) beanMap.remove(field.getName());
							if (props != null && !props.isEmpty() && props.get("id") != null) {
								LookupField lookupField = (LookupField) field;
								// Commenting out because max level is set as 0 by default and anyway we need
								// this. And also because of the change in library of mapper
//								if(props.size() <= 3) {
								Object lookupVal = FieldUtil.getLookupVal(lookupField, (long) props.get("id"), 0);
								placeHolders.put(prefix + "." + field.getName(), lookupVal);
								props = FieldUtil.getAsProperties(lookupVal);
//								}
								String childModuleName = lookupField.getLookupModule() == null
										? lookupField.getSpecialType()
										: lookupField.getLookupModule().getName();
								appendModuleNameInKey(childModuleName, prefix + "." + field.getName(), props,
										placeHolders);
							}
						} else {
							placeHolders.put(prefix + "." + field.getName(), beanMap.remove(field.getName()));
						}
					}
				}
			}
			for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
				if (entry.getValue() instanceof Map<?, ?>) {
					appendModuleNameInKey(null, prefix + "." + entry.getKey(), (Map<String, Object>) entry.getValue(),
							placeHolders);
				} else {
					placeHolders.put(prefix + "." + entry.getKey(), entry.getValue());
				}
			}
		}
	}
	*/

	public static IAMAccount getCurrentAccount(Organization org, IAMUser user) {
		IAMAccount account = new IAMAccount(org, null);
		account.setUser(user);
		return account;

	}

	public static IAMAccount getCurrentAccount(long orgId, IAMUser user) throws Exception {
		Organization org = getOrgBean().getOrgv2(orgId);
		return getCurrentAccount(org, user);
	}

}
