package com.facilio.fsm.integrations;

import com.amazonaws.util.IOUtils;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.service.FacilioHttpUtilsFW;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleMapsAPI {

    private static final Logger LOGGER = LogManager.getLogger(GoogleMapsAPI.class.getName());
    private static final String REVERSE_GEOCODING_API = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String COMPUTE_ROUTES_API = "https://routes.googleapis.com/directions/v2:computeRoutes";
    private static final String STATIC_MAP_API = "https://maps.googleapis.com/maps/api/staticmap";

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();

        String formattedAddress = reverseGeocode("12.992445432980453,80.19022862245085");
        System.out.println("formattedAddress: "+formattedAddress);

        JSONArray coordinates = (JSONArray) new JSONParser().parse("[{\"lat\":12.992445432980453,\"lng\":80.19022862245085},{\"lat\":13.012516636639866,\"lng\":80.20984093370302}]");
        TripDistance tripDistance = calculateTripDistance(coordinates, -1, false);
        System.out.println("tripDistance: "+tripDistance);

        generateTripMapPreview(tripDistance.getOrigin(), tripDistance.getDestination(), tripDistance.getEncodedPolyline(), null);
        System.out.println("timetaken: "+(System.currentTimeMillis()-startTime));
    }

    private static String getAPIKey() {
        return FacilioProperties.getConfig("google.maps.key");
    }

    private static boolean isGoogleMapsEnabled() {
        String apiKey = getAPIKey();
        return StringUtils.isNotEmpty(apiKey);
    }

    /**
     * @param latLng lat & lng values comma separated
     * @return formatted address
     */
    public static String reverseGeocode(String latLng) {
        if (!isGoogleMapsEnabled()) {
            LOGGER.warn("Google Maps is disabled, so reverseGeocode is skipped.");
            return null;
        }

        try {
            Map<String, String> params = new HashMap<>();
            params.put("latlng", latLng);
            params.put("key", getAPIKey());

            String response = FacilioHttpUtilsFW.doHttpGet(REVERSE_GEOCODING_API, null, params);
            JSONObject geocodingResponse = (JSONObject) new JSONParser().parse(response);
            JSONArray results = (JSONArray) geocodingResponse.get("results");
            JSONObject result = (JSONObject) results.get(0);
            String formattedAddress = (String) result.get("formatted_address");
            return formattedAddress;
        } catch (Exception e) {
            LOGGER.error("Exception while reverseGeocode. ", e);
        }
        return null;
    }

    /**
     * @param coordinates list of coordinates to calculate the trip distance & duration
     * @param estimatedStartTime estimated start time. Default: -1
     * @param highRes if highRes: calculate the distance for each individual coordinate else: 25 intermediate points. Default: false
     * @return distance, duration, encodedPolyline
     */
    public static TripDistance calculateTripDistance(List<String> coordinates, long estimatedStartTime, boolean highRes) {
        if (!isGoogleMapsEnabled()) {
            LOGGER.warn("Google Maps is disabled, so calculateTripDistance is skipped.");
            return null;
        }
        if (coordinates == null || coordinates.size() < 2) {
            LOGGER.warn("At least two coordinates required to calculateTripDistance.");
            return null;
        }

        try {
            String origin = coordinates.get(0);
            String destination = coordinates.get(coordinates.size() - 1);

            Map<String, String> headers = new HashMap<>();
            headers.put("X-Goog-Api-Key", getAPIKey());
            headers.put("Content-Type", "application/json");
            headers.put("X-Goog-FieldMask", "routes.duration,routes.distanceMeters,routes.polyline.encodedPolyline");

            Map<String, String> params = new HashMap<>();

            EasyJson requestJson = new EasyJson();
            requestJson.put("origin", new EasyJson().put("location", new EasyJson().put("latLng", new EasyJson().put("latitude", origin.split(",")[0]).put("longitude", origin.split(",")[1]))));
            requestJson.put("destination", new EasyJson().put("location", new EasyJson().put("latLng", new EasyJson().put("latitude", destination.split(",")[0]).put("longitude", destination.split(",")[1]))));

            JSONArray intermediates = new JSONArray();
            if (coordinates.size() > 25) {
                int interval = coordinates.size() / 25;
                int loopIndex = interval;
                for (int i = 0; i < 25; i++) {
                    if (coordinates.size() > loopIndex) {
                        String latLng = (String) coordinates.get(loopIndex);
                        intermediates.add(new EasyJson().put("via", true).put("location", new EasyJson().put("latLng", new EasyJson().put("latitude", latLng.split(",")[0]).put("longitude", latLng.split(",")[1]))));
                    }
                    loopIndex += interval;
                }
            }
            else {
                for (int i = 0; i < coordinates.size(); i++) {
                    String latLng = (String) coordinates.get(i);
                    intermediates.add(new EasyJson().put("via", true).put("location", new EasyJson().put("latLng", new EasyJson().put("latitude", latLng.split(",")[0]).put("longitude", latLng.split(",")[1]))));
                }
            }
            requestJson.put("intermediates", intermediates);
            requestJson.put("travelMode", "DRIVE");
            if (estimatedStartTime> 0) {
                requestJson.put("departureTime", Instant.ofEpochMilli(estimatedStartTime).toString());
            }

            String response = FacilioHttpUtilsFW.doHttpPost(COMPUTE_ROUTES_API, headers, params, requestJson.toJSONString(), null,-1);
            JSONObject routesResponse = (JSONObject) new JSONParser().parse(response);
            JSONArray routes = (JSONArray) routesResponse.get("routes");
            JSONObject route = (JSONObject) routes.get(0);

            long distanceMeters = Long.parseLong(route.get("distanceMeters").toString());
            long duration = Long.parseLong(route.get("duration").toString().replace("s", ""));
            String encodedPolyline = (String) ((JSONObject) route.get("polyline")).get("encodedPolyline");

            TripDistance tripDistance = new TripDistance();
            tripDistance.setOrigin(origin);
            tripDistance.setDestination(destination);
            tripDistance.setDistance(distanceMeters);
            tripDistance.setDuration(duration);
            tripDistance.setEncodedPolyline(encodedPolyline);
            return tripDistance;
        } catch (Exception e) {
            LOGGER.error("Exception while calculateTripDistance. ", e);
        }
        return null;
    }

    /**
     * @param startLatLng lat & lng values comma separated
     * @param endLatLng lat & lng values comma separated
     * @param encodedPolyline encoded polyline returned by the calculate trip distance method
     * @param styles allowed styles [ width, height, scale, color, showMarkers, markerSize:tiny, med, large ]
     * @return fileId
     */
    public static long generateTripMapPreview(String startLatLng, String endLatLng, String encodedPolyline, JSONObject styles) {
        styles = styles == null ? new JSONObject() : styles;
        int scale = (Integer) styles.getOrDefault("scale", 2);
        int width = (Integer) styles.getOrDefault("width", 800);
        int height = (Integer) styles.getOrDefault("height", 400);
        String color = (String) styles.getOrDefault("color", "#7D63DC");
        boolean showMarkers = (Boolean) styles.getOrDefault("showMarkers", true);
        String markerSize = (String) styles.getOrDefault("markerSize", "tiny");

        if (!isGoogleMapsEnabled()) {
            LOGGER.warn("Google Maps is disabled, so generateStaticMap is skipped.");
            return -1;
        }

        if (encodedPolyline == null || encodedPolyline.trim().isEmpty()) {
            LOGGER.warn("encodedPolyline should not be empty.");
            return -1;
        }

        try {
            String gmapColor = "0x" + color.replace("#", "") + "FF";
            Map<String, String> params = new HashMap<>();
            params.put("key", getAPIKey());
            params.put("scale", String.valueOf(scale));
            params.put("size", width + "x" + height);
            params.put("path", "weight:2|color:" + gmapColor + "|enc:" + encodedPolyline);
            params.put("style", "feature:poi|visibility:off");

            List<String> markers = new ArrayList<>();
            if (showMarkers) {
                markers.add("size:"+markerSize+"|color:"+gmapColor+"|label:S|"+startLatLng);
                markers.add("size:"+markerSize+"|color:"+gmapColor+"|label:E|"+endLatLng);
            }

            File staticMapFile = downloadStaticMap(STATIC_MAP_API, null, params, markers);

            FileStore fs = FacilioFactory.getFileStore();
            long fileId = fs.addFile(staticMapFile.getName(), staticMapFile, "image/png");

            try {
                staticMapFile.delete();
            } catch (Exception e) { e.printStackTrace(); }
            return fileId;
        } catch (Exception e) {
            LOGGER.error("Exception while generateTripMapPreview. ", e);
        }
        return -1;
    }

    private static File downloadStaticMap(String url, Map<String, String> headers, Map<String, String> params, List<String> markers) {
        CloseableHttpClient client = com.facilio.aws.util.AwsUtil.getHttpClient(-1);
        try  {
            URIBuilder builder = new URIBuilder(url);
            if (params != null) {
                for (String key : params.keySet()) {
                    String value = params.get(key);
                    builder.setParameter(key, value);
                }
            }
            if (markers != null) {
                for (String marker : markers) {
                    builder.addParameter("markers", marker);
                }
            }

            HttpGet get = new HttpGet(builder.build());
            if (headers != null) {
                for (String key : headers.keySet()) {
                    String value = headers.get(key);
                    get.setHeader(key, value);
                }
            }

            CloseableHttpResponse response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();

            LOGGER.info("\nSending 'GET' request to URL : " + url);
            LOGGER.info("get parameters : " + params);
            LOGGER.info("Response Code : " + status);

            File tempFile = File.createTempFile("staticmap-" + System.currentTimeMillis(), "png");
            try (OutputStream outputStream = new FileOutputStream(tempFile)) {
                IOUtils.copy(response.getEntity().getContent(), outputStream);
            }
            return tempFile;
        } catch (Exception e) {
            LOGGER.info("Executing downloadStaticMap ::::url:::" + url, e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Setter
    @Getter
    public static class TripDistance {
        private String origin;
        private String destination;
        private long distance;
        private long duration;
        private String encodedPolyline;

        public String toString() {
            return "distance: " + distance + " | duration: " + duration + " encodedPolyline: " + encodedPolyline;
        }
    }

    public static class EasyJson extends JSONObject {
        public EasyJson() {
            super();
        }

        @Override
        public EasyJson put(Object key, Object value) {
            super.put(key, value);
            return this;
        }
    }
}
