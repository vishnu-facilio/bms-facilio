package com.facilio.pdftemplate.util;

import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.services.filestore.PublicFileUtil;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import lombok.extern.log4j.Log4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j
public class HandleBarsHelper {

    private static HandleBarsHelper instance;
    private Handlebars handlebars;

    private HandleBarsHelper(){
        this.handlebars = new Handlebars();
        registerCustomHelpers();
    }
    public static HandleBarsHelper getInstance(){
        if(instance==null){
            instance = new HandleBarsHelper();
        }
        return instance;
    }
    public Handlebars getHandlebars() {
        return handlebars;
    }

    private void registerCustomHelpers(){
        registerGroupDataHelper();
        registerPlusOneHelper();
        registerFilterListHelper();
        registerIsNotEmptyHelper();
        registerPublicUrlHelper();
    }
    private void registerIsNotEmptyHelper(){
        handlebars.registerHelper("isNotEmpty", new Helper<List>() {
            @Override
            public Boolean apply(List list,Options options){
                return CollectionUtils.isNotEmpty(list);
            }
        });
    }
    private void registerGroupDataHelper(){
        handlebars.registerHelper("groupData", new Helper<List<Map<String,Object>>>() {
            @Override
            public Map<Object,List<Map<String,Object>>> apply(List<Map<String,Object>> list,Options options){
                String groupByField = options.param(0);
                Map<Object,List<Map<String,Object>>> result = new HashMap<>();
                if(CollectionUtils.isNotEmpty(list)) {
                    for (Map<String, Object> item : list) {
                        if (item.containsKey(groupByField) && !result.containsKey(item.get(groupByField))) {
                            List<Map<String, Object>> values = new ArrayList<>();
                            values.add(item);
                            result.put(item.get(groupByField), values);
                        } else if (result.containsKey(item.get(groupByField))) {
                            List<Map<String, Object>> values = result.get(item.get(groupByField));
                            values.add(item);
                            result.put(item.get(groupByField), values);
                        }
                    }
                }
                return result;
            }
        });
    }
    private void registerPlusOneHelper(){
        handlebars.registerHelper("plusOne", new Helper<Integer>() {
            @Override
            public Integer apply(Integer value, Options options) {
                return value + 1;
            }
        });
    }
    private void registerFilterListHelper(){
        handlebars.registerHelper("filterList", new Helper<List<Map<String,Object>>>() {
            @Override
            public List<Map<String,Object>> apply(List<Map<String,Object>> list,Options options) {
                String filterFieldName = options.param(0);
                String filterFieldValue = options.param(1);
                if(CollectionUtils.isNotEmpty(list) && filterFieldName!=null && filterFieldValue!=null){
                   return list.stream().filter(item -> item.containsKey(filterFieldName) && item.get(filterFieldName).equals(filterFieldValue)).collect(Collectors.toList());
                }
                return list;
            }
        });
    }
    private void registerPublicUrlHelper(){
        handlebars.registerHelper("publicUrl", new Helper<Object>() {
            @Override
            public String apply(Object fileIdValue,Options options) {
                try{
                    Long fileId = Long.parseLong(String.valueOf(fileIdValue));
                    if(fileId!=null && fileId>0){
                        FileStore fileStore = FacilioFactory.getFileStore();
                        return fileStore.getOrgiDownloadUrl(fileId);
                    }
                    return null;
                }catch (Exception e){
                    LOGGER.error("Error while generating public url",e);
                }
                return null;
            }
        });
    }
    public String compile(String htmlContent, Map<String, Map<String, Object>> placeholders) throws IOException {
        Template hbsTemplate = handlebars.compileInline(htmlContent);
        String renderedContent = hbsTemplate.apply(placeholders);
        return renderedContent;
    }
}
