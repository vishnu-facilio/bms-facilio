package com.facilio.bundle.context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class InstalledBundleContext {

	Long id;
	Long orgId;
	String bundleGlobalName;
	Double installedVersion;
	Long installedTime;
	
}
