package com.facilio.componentpackage.context;

import com.facilio.bundle.utils.BundleConstants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class PackageFileContext {

    String name;
    String extension;
    XMLBuilder xmlContent;
    String fileContent;

    public PackageFileContext(String name, String extension, XMLBuilder xmlContent) {
        this.name = name;
        this.extension = extension;
        this.xmlContent = xmlContent;
    }

    public PackageFileContext(String name,String extension,String fileContent) throws Exception {
        this.name = name;
        this.extension = extension;
        if(extension.equals(BundleConstants.XML_FILE_EXTN)) {
            xmlContent = XMLBuilder.parse(fileContent);
        }
        else {
            this.fileContent = fileContent;
        }
    }
}
