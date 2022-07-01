package com.facilio.bmsconsoleV3.context.readingimportapp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V3ReadingImportAppContext {
    private static final long serialVersionUID = 1L;
    Long Id;
    Long fileId;
    Long orgId;
    String fieldMapping;
    Long status;
    String columnHeading;
    String resourceMapping;
    Long validatedFiledId;
    Long createdTime;
    Long modifiedTime;
    Long importStartTime;
    Long importEndTime;
    Long totalRows;
    Long validatedRows;
    Long createdBy;
    Long modifiedBy;
    Long fileSize;
    String name;


    public enum status {
        NONE,
        UPLOADED,
        PARSING_FAILED,
        RESOURCE_SELECTED,
        FIELD_MAPPED,
        VALIDATION_PROGRESS,
        VALIDATION_FAILED,
        VALIDATION_RESOLVED,
        IMPORT_PROGRESS,
        IMPORT_COMPLETED,
        IMPORT_FAILED;


        public int getValue() {
            return ordinal() + 1;
        }

        public static status valueOf(int value) {
            if (value > 0 && value <= values().length) {
                return values()[value - 1];
            }
            return null;
        }
    }









}
