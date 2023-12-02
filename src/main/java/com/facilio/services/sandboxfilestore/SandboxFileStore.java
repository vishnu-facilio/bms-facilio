package com.facilio.services.sandboxfilestore;

import com.facilio.util.FacilioUtil;
import lombok.extern.log4j.Log4j;
import lombok.Getter;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j @Getter
public abstract class SandboxFileStore {
    @Getter
    public static class NamespaceConfig {
        private NamespaceConfig(String name) {
            Objects.requireNonNull(name, "Name cannot be null in namespace");
            this.name = name;
        }

        private String name;
    }

    private static Map<String, NamespaceConfig> initNamespaces() {
        Map<String, NamespaceConfig> namespaces = new HashMap<>();
        NamespaceConfig namespaceConfig = new NamespaceConfig("package");
        namespaces.put(namespaceConfig.getName(), namespaceConfig);

        return namespaces;
    }

    public static SandboxFileStore.NamespaceConfig getNamespace(String namespace) {
        return NAMESPACES.get(namespace);
    }

    public static Set<String> getAllNamespaces() {
        return NAMESPACES.keySet();
    }

    private long orgId;
    private long userId;
    public static final String DEFAULT_NAMESPACE = "package";
    private static final Map<String, SandboxFileStore.NamespaceConfig> NAMESPACES = Collections.unmodifiableMap(initNamespaces());

    public SandboxFileStore(long orgId, long userId) {
        this.orgId = orgId;
        this.userId = userId;
    }

    public abstract String getRootPath(String namespace);

    public abstract String addFileAndGetURL(File file, String fileName) throws Exception;

    public abstract String addDirectoryAndGetURL(File file) throws Exception;

    public abstract InputStream readFile(String fileURL) throws Exception;
}
