package com.facilio.bmsconsole.page;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter @Setter
public class RelatedListContext {
	private static final long serialVersionUID = 1L;

	public RelatedListContext() {}

	public static final List<String> DELETE_DISABLED_MODULES = Collections.unmodifiableList(Arrays.asList(new String[]{
			"purchaseorder",
			"purchaserequest",
			"contracts",
			"vendorDocuments",
			"insurance",
			"contact",
			"workorder",
			"workpermit",
			"tenantcontact",
			"vendorcontact",
			"tenantspaces",
			"clientcontact",
			"site"
	}));

	public static final List<String> EDIT_DISABLED_MODULES = Collections.unmodifiableList(Arrays.asList(new String[]{
			"purchaseorder",
			"purchaserequest",
			"contracts",
			"vendorDocuments",
			"insurance",
			"contact",
			"workorder",
			"safetyPlanHazard",
			"hazardPrecaution",
			"workorderHazard",
			"assetHazard",
			"workpermit",
			"tenantcontact",
			"vendorcontact",
			"clientcontact",
			"tenantspaces",
			"site",
			"quoteterms",
			"poterms",
			"prterms"
	}));

	public static final List<String> CREATE_ENABLED_MODULES = Collections.unmodifiableList(Arrays.asList(new String[]{
			"vendorDocuments",
			"vendorcontact",
			"clientcontact",
			"custom_tenantutility"
//			"tenant",
//			"tenantunit",
//			"workorder",
	}));

}
