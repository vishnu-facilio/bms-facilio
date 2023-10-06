package com.facilio.componentpackage.context;

import com.facilio.bundle.utils.BundleConstants;
import com.facilio.xml.builder.XMLBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Setter
@Getter
@AllArgsConstructor
public class PackageFileContext {

    String name;
    String extension;
    XMLBuilder xmlContent;
    String fileContent;

    File csvContent;

    public PackageFileContext(String name, String extension, XMLBuilder xmlContent) {
        this.name = name;
        this.extension = extension;
        this.xmlContent = xmlContent;
    }

    public PackageFileContext(String name, String extension, File csvContent){
        this.name = name;
        this.extension = extension;
        this.csvContent = csvContent;
    }

    public PackageFileContext(String name,String extension,String fileContent,File file) throws Exception {
        this.name = name;
        this.extension = extension;
        if(extension.equals(BundleConstants.XML_FILE_EXTN)) {
            xmlContent = XMLBuilder.parse(fileContent);
        }
        else if(extension.equals(BundleConstants.CSV_FILE_EXTN)){
            csvContent = file;
        }
        else {
            this.fileContent = fileContent;
        }
    }
}
