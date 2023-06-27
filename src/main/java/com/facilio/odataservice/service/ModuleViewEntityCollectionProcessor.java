package com.facilio.odataservice.service;

import com.facilio.odataservice.data.Storage;
import com.facilio.odataservice.util.ODATAUtil;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
        ODATAUtil.setModuleName(moduleName);
        ODataModuleViewsUtil.setModuleName(moduleName);
        EntityCollection entityCollection;

        try {
            ODataModuleViewsUtil.viewAction(moduleName,edmEntitySet.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            entityCollection = storage.getViewRecords(moduleName);
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
        ODATAUtil.setModuleName(moduleName);
        ODataModuleViewsUtil.setModuleName(moduleName);
        EntityCollection entityCollection ;
        String viewName;
        try {
            viewName = ODataModuleViewsUtil.getViewName(edmEntitySet.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            ODataModuleViewsUtil.viewAction(moduleName,viewName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            entityCollection = storage.getViewRecords(moduleName);
        } catch (Exception e) {
            LOGGER.error("Exception in ModuleEntityCollection Processor in accessing data => " + e);
            throw new RuntimeException(e);
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
        opts = EntityCollectionSerializerOptions.with().contextURL(contextUrl).id(id).build();
        ODataSerializer serializer = oData.createSerializer(responseFormat);
        SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entityCollection, opts);
        //configure the response object: set the body, headers and status code
        response.setContent(serializerResult.getContent());
        response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        response.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());
    }

    @Override
    public void init(OData oData, ServiceMetadata serviceMetadata) {
            this.oData = oData;
            this.serviceMetadata = serviceMetadata;
    }
}
