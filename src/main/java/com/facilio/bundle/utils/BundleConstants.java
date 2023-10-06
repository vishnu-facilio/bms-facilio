package com.facilio.bundle.utils;

import java.util.ArrayList;
import java.util.List;

public class BundleConstants {
	
	public static final List<String> ALLOWED_EXTN = new ArrayList<>();
	
	static {
		ALLOWED_EXTN.add("xml");
		ALLOWED_EXTN.add("fs");
	}
	
	

	public static final String COMPONENT_ID = "componentId";
	
	public static final String CHANGE_SET = "changeSet";
	
	public static final String COMPONENT_OBJECT = "componentObject";
	
	public static final String PARENT_COMPONENT_OBJECT = "parentComponentObject";
	
	public static final String PARENT_COMPONENT_ID = "parentComponentId";
	public static final String PARENT_COMPONENT_NAME = "parentComponentName";
	
	public static final String BUNDLED_COMPONENTS = "bundledComponents";
	
	public static final String DESTINATION_ORG = "destinationOrg";
	
	public static final String COMPONENT_TYPE = "componentType";
	public static final String COMPONENT_NAME = "componentName";
	
	public static final String BUNDLE_FILE_NAME = "bundle";
	
	public static final String BUNDLE_XML_BUILDER = "bundleXmlBuilder";
	
	public static final String COMPONENT_XML_BUILDER = "componentXmlBuilder";
	
	public static final String BUNDLE_CHANGE = "bundleChange";
	
	public static final String BUNDLE_CONTEXT = "bundleContext";
	
	public static final String BUNDLE_VERSION_LIST = "bundleVersionList";
	
	public static final String VERSION = "version";
	public static final String GLOBAL_NAME = "globalName";
	public static final String COMPONENTS = "components";
	public static final String COMPONENT = "component";
	public static final String VALUES = "values";
	
	public static final String XML_FILE_EXTN = "xml";
	public static final String CSV_FILE_EXTN = "csv";
	public static final String COMPONENTS_FOLDER_NAME = "Components";
	
	public static final String COMPONENTS_FOLDER = "componentFolder";
	
	public static final String BUNDLE_FOLDER = "bundleFolder";

	public static final String DOWNLOAD_URL = "downloadUrl";

	public static final String BUNDLE_ZIP_FILE = "bundleZIPFile";
	public static final String BUNDLE_ZIP_FILE_NAME = "bundleZIPFileName";
	public static final String BUNDLE_ZIP_FILE_CONTENT_TYPE = "application/zip";

	public static final String BUNDLED_XML_COMPONENT_FILE = "bundledXMLComponentFile";

	public static final String BUNDLE_CHANGE_SET_LIST = "bundleChangeSetList";

	public static final String BUNDLE_CONTEXT_LIST = "bundleList";

	public static final String INSTALLED_BUNDLE = "installedBundle";
	
	public static final String INSTALLED_BUNDLE_VERSION_ID_LIST = "installedBundleVersionIdList";
	
	public static final String INSTALLED_BUNDLES = "installedBundles";
	
	public static final String INSTALLED_COMPONENT_LIST = "installedComponentsList";

	public static final String CHANGE_SET_CACHE = "changeSetMapCache";

	public static final String TO_BE_INSTALLED_VERSIONS_LIST = "toBeInstalledVersionList";

	public static final String INSTALL_MODE = "installMode";

	public static class Components {
		public static final String NAME = "name";
		public static final String DISPLAY_NAME = "displayName";
		public static final String MODE = "mode";
	}
	
	public static class Sandbox {
		public static final String SANDBOX = "sandbox";
	}
}
