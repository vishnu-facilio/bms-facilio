package com.facilio.odataservice.service;

import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.etag.ETagHelper;
import org.apache.olingo.server.api.etag.ServiceMetadataETagSupport;
import org.apache.olingo.server.api.processor.ServiceDocumentProcessor;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.uri.UriInfo;

public class ReadingsServiceDocumentProcessor implements ServiceDocumentProcessor {
    private  OData oData;
    private  ServiceMetadata serviceMetadata;
    @Override
    public void readServiceDocument(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {
        boolean isNotModified = false;
        ServiceMetadataETagSupport eTagSupport = this.serviceMetadata.getServiceMetadataETagSupport();
        if (eTagSupport != null && eTagSupport.getServiceDocumentETag() != null) {
            response.setHeader("ETag", eTagSupport.getServiceDocumentETag());
            ETagHelper eTagHelper = oData.createETagHelper();
            isNotModified = eTagHelper.checkReadPreconditions(eTagSupport.getServiceDocumentETag(), request.getHeaders("If-Match"), request.getHeaders("If-None-Match"));
        }
        if (isNotModified) {
            response.setStatusCode(HttpStatusCode.NOT_MODIFIED.getStatusCode());
        } else if (HttpMethod.HEAD == request.getMethod()) {
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
        } else {
            String serviceRoot = request.getRawRequestUri();
            ODataSerializer serializer = oData.createSerializer(contentType);
            response.setContent(serializer.serviceDocument(this.serviceMetadata,serviceRoot).getContent());
            response.setStatusCode(HttpStatusCode.OK.getStatusCode());
            response.setHeader("Content-Type", contentType.toContentTypeString());
        }
    }
    @Override
    public void init(OData oData, ServiceMetadata serviceMetadata) {
        this.oData = oData;
        this.serviceMetadata = serviceMetadata;
    }
}
