package com.facilio.fields.fieldBuilder;

import com.facilio.modules.FieldType;
import org.apache.commons.chain.Command;

import java.util.List;

    public interface FieldListBuilder {

        /**
         * @param fieldTypesToSkip these fieldTypes are skipped while fetching the fields list
         * @return  FieldListBuilder
         */
        FieldListBuilder fieldTypesToSkip(List<FieldType> fieldTypesToSkip);

        /**
         * other than development environment, addFields are given priority rather than that of skip fields
         * @param fieldNames list of fieldNames to be added while fetching the fields list
         * @return FieldListBuilder
         * @throws Exception if tried to add and skip fieldName at the same time for a fieldListType (only in development)
         */
        FieldListBuilder add(List<String> fieldNames);

        /**
         * other than development environment, skipFields are not considered if addFields are configured for the fieldListType
         * @param fieldNames  list of fieldNames to be skipped while fetching the fields list
         * @return FieldListBuilder
         * @throws Exception if tried to add and skip fieldName at the same time for a fieldListType (only in development)
         */
        FieldListBuilder skip(List<String> fieldNames);

        /**
         * This is used to add typeSpecificFields which can be obtained from the context
         * @param name key with which the specified value is to be associated
         * @param fieldNames list of fieldNames to be associated with the specified key
         * @return FieldListBuilder
         */
//        FieldListBuilder addConfigSpecificFields(String name, List<String> fieldNames);

        /**
         * @param afterFetchCommand commands to be executed after the fields are fetched
         * @return FieldListBuilder
         */
        FieldListBuilder afterFetch(Command... afterFetchCommand);

        FieldConfig done();
    }
