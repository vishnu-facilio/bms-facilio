package com.facilio.bundle.context;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BundleFolderContext {

	String name;
	String path;
	
	Map<String,BundleFolderContext> folders;
	Map<String,BundleFileContext> files;
	
	public BundleFolderContext(String name) {
		this.name = name;
		folders = new HashMap<String, BundleFolderContext>();
		files = new HashMap<String, BundleFileContext>();
	}
	
	public BundleFolderContext addFolder(String name) {
		BundleFolderContext folder = new BundleFolderContext(name);
		folders.put(name, folder);
		return folder;
	}
	
	public void addFolder(String name,BundleFolderContext folder) {
		folders.put(name, folder);
	}
	
	public BundleFolderContext getFolder(String name) {
		return folders.get(name);
	}
	
	public BundleFolderContext getOrAddFolder(String name) {
		if(folders.containsKey(name)) {
			return folders.get(name);
		}
		else {
			return addFolder(name);
		}
	}
	
	public void addFile(String name,BundleFileContext file) {
		files.put(name, file);
	}
	
	public BundleFileContext getFile(String name) {
		return files.get(name);
	}
}