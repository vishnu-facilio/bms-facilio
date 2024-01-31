package com.facilio.ocr.aws;

import java.util.List;

import software.amazon.awssdk.services.textract.model.Geometry;

public class TextractContext {

    public static class TableContext {
        List<List<TableRecordContext>> records;

        public List<List<TableRecordContext>> getRecords() {
            return records;
        }

        public void setRecords(List<List<TableRecordContext>> records) {
            this.records = records;
        }

        String name;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    public static class TableRecordContext {
        String id;
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }

        String name;
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        Geometry geomentry;
        public Geometry getGeomentry() {
            return geomentry;
        }
        public void setGeomentry(Geometry geomentry) {
            this.geomentry = geomentry;
        }
    }

    public static class FormContext {
        int page;
        public int getPage() {
            return page;
        }
        public void setPage(int page) {
            this.page = page;
        }

        List<FormValuesContext> formRules;
        public void setFormRules(List<FormValuesContext> formRules) {
            this.formRules = formRules;
        }
        public List<FormValuesContext> getFormRules() {
            return formRules;
        }

        public static class FormValuesContext {
            String keyId;
            public String getKeyId() {
                return keyId;
            }
            public void setKeyId(String keyId) {
                this.keyId = keyId;
            }

            String valueId;
            public String getValueId() {
                return valueId;
            }
            public void setValueId(String valueId) {
                this.valueId = valueId;
            }

            String key;
            public String getKey() {
                return key;
            }
            public void setKey(String key) {
                this.key = key;
            }

            String value;
            public void setValue(String value) {
                this.value = value;
            }
            public String getValue() {
                return value;
            }

            Geometry keyGeomentry;
            public Geometry getKeyGeomentry() {
                return keyGeomentry;
            }
            public void setKeyGeomentry(Geometry keyGeomentry) {
                this.keyGeomentry = keyGeomentry;
            }

            Geometry valueGeometry;
            public Geometry getValueGeometry() {
                return valueGeometry;
            }
            public void setValueGeometry(Geometry valueGeometry) {
                this.valueGeometry = valueGeometry;
            }
        }
    }
}
