package com.facilio.bundle.context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InstalledBundleContext {

	Long id;
	Long orgId;
	Long bundleId;
	String bundleGlobalName;
	String installedVersion;
	Long installedTime;
	
}
