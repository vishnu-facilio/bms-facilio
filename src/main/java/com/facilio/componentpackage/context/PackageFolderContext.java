package com.facilio.componentpackage.context;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class PackageFolderContext {
    String name;
    String path;

    Map<String, PackageFolderContext> folders;
    Map<String, PackageFileContext> files;

    public PackageFolderContext(String name) {
        this.name = name;
        folders = new HashMap<>();
        files = new HashMap<>();
    }

    public PackageFolderContext addFolder(String name) {
        PackageFolderContext folder = new PackageFolderContext(name);
        folders.put(name, folder);
        return folder;
    }

    public void addFolder(String name, PackageFolderContext folder) {
        folders.put(name, folder);
    }

    public PackageFolderContext getFolder(String name) {
        return folders.get(name);
    }

    public void addFile(String name, PackageFileContext file) {
        files.put(name, file);
    }

    public PackageFileContext getFile(String name) {
        return files.get(name);
    }
}
