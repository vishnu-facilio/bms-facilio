package com.facilio.odataservice.service;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.odataservice.data.ODataFilterContext;
import com.facilio.odataservice.data.Storage;
import com.facilio.odataservice.util.ODataModuleViewsUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.CountEntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.queryoption.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.facilio.odataservice.util.ODATAUtil.getFilterContext;

public class ModuleViewEntityCollectionProcessor implements CountEntityCollectionProcessor {

    public ModuleViewEntityCollectionProcessor(String moduleName){
        this.moduleName = moduleName;
    }

    private static final Logger LOGGER = LogManager.getLogger(ModuleViewEntityCollectionProcessor.class.getName());
    private  OData oData;
    private String moduleName;
    private  ServiceMetadata serviceMetadata;
    private Storage storage = new Storage();
    @Override
    public void countEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo) throws ODataApplicationException, ODataLibraryException {
        ContentType responseFormat  = ContentType.TEXT_PLAIN;
        // 1st retrieve the requested EntitySet from the uriInfo (representation of the parsed URI)
        List<UriResource> resourcePaths = uriInfo.getUriResourceParts();

        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        EntityCollection entityCollection;
        FacilioView view = new FacilioView();
        try {
            view = ODataModuleViewsUtil.getViewDetail(moduleName,edmEntitySet.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            entityCollection = storage.getViewRecords(moduleName, -1,"",-1,"",false, view, new ArrayList<>());
        } catch (Exception e) {
            LOGGER.error("Exception in ModuleEntityCollection Processor in accessing data => " + e);
            throw new RuntimeException(e);
        }
        List<Entity> entityList = entityCollection.getEntities();
        EntityCollection returnEntityCollection = new EntityCollection();
        returnEntityCollection.setCount(entityList.size());
        byte[] count = returnEntityCollection.getCount().toString().getBytes();
        InputStream content = new ByteArrayInputStream(count);
        response.setContent(content);
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    @Override
    public void readEntityCollection(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {
        // retrieve the requested EntitySet from the uriInfo (representation of the parsed URI)
        List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
        FilterOption filterOption = uriInfo.getFilterOption();
        SelectOption selectOption = uriInfo.getSelectOption();
        TopOption topOption = uriInfo.getTopOption();
        SkipOption skipOption = uriInfo.getSkipOption();
        OrderByOption orderByOption = uriInfo.getOrderByOption();
        int limit = -1,skip=-1;
        String field = "",orderByField = "";
        boolean isDescending = false;
        if(topOption != null){
            limit = topOption.getValue();
        }
        if(skipOption != null){
            skip = skipOption.getValue();
        }
        if(selectOption != null){
            if(!selectOption.getSelectItems().isEmpty()) {
                field = selectOption.getSelectItems().get(0).getResourcePath().getUriResourceParts().get(0).toString();
            }
        }
        if(orderByOption != null){
            if(!orderByOption.getOrders().isEmpty()) {
                orderByField = orderByOption.getOrders().get(0).getExpression().toString().replace("[", "").replace("]", "");
                isDescending = orderByOption.getOrders().get(0).isDescending();
            }
        }
        List<ODataFilterContext> filterContext = new ArrayList<>();
        if(filterOption != null){
            if(filterOption.getExpression()!=null) {
                filterContext = getFilterContext(filterOption,moduleName);
            }
        }
        EntityCollection entityCollection ;
        String viewName;
        try {
            viewName = ODataModuleViewsUtil.getViewName(edmEntitySet.getName(), moduleName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        FacilioView view;
        try {
            view = ODataModuleViewsUtil.getViewDetail(moduleName,viewName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            entityCollection = storage.getViewRecords(moduleName,limit,field,skip,orderByField,isDescending,view,filterContext);
            LOGGER.info("Processing entity collection "+System.currentTimeMillis());
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching view records "+e);
        }

        // serialize
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();
        ContextURL contextUrl;
        final String id = request.getRawBaseUri() + "/" + edmEntitySet.getName();
        EntityCollectionSerializerOptions opts;
        URI serviceRoot = null;
        try {
            serviceRoot = new URI(request.getRawRequestUri());
        } catch (URISyntaxException e) {
            LOGGER.error("URI syntax exception in creating service root"+e);
        }
        contextUrl = ContextURL.with() .serviceRoot(serviceRoot).entitySet(edmEntitySet).build();
        opts = EntityCollectionSerializerOptions.with().contextURL(contextUrl).select(selectOption).id(id).build();
        ODataSerializer serializer = oData.createSerializer(responseFormat);
        SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entityCollection, opts);
        //configure the response object: set the body, headers and status code
        response.setContent(serializerResult.getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
        LOGGER.info("Processed entity collection successfully "+System.currentTimeMillis());

    }

    @Override
    public void init(OData oData, ServiceMetadata serviceMetadata) {
            this.oData = oData;
            this.serviceMetadata = serviceMetadata;
    }
}
