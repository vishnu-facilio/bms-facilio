package com.facilio.agentIntegration;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MultipartHttpPost {
    private static final Logger LOGGER = LogManager.getLogger(DownloadCertFile.class.getName());


    private final String boundary;
        private static final String LINE_FEED = "\r\n";
        private HttpURLConnection httpConn;
        private String charset;
        private OutputStream outputStream;
        private PrintWriter writer;

        public MultipartHttpPost(String requestURL, String charset, String authStringEnc)
                throws IOException {
            this.charset = charset;

            boundary = "===" + System.currentTimeMillis() + "===";

            URL url = new URL(requestURL);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("Authorization",  authStringEnc);
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);
            httpConn.disconnect();
        }

        public void addFile(String fieldName, String fileName, InputStream inputStream)
                throws IOException {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }

        public void addHeaderField(String name, String value) {
            writer.append(name + ": " + value).append(LINE_FEED);
            writer.flush();
        }

        public String finish() throws IOException {
            String response = "";

            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK || status == HttpURLConnection.HTTP_CREATED) {

                BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
                response = inputStreamToString(in);

                httpConn.disconnect();
            } else {
                BufferedInputStream in = new BufferedInputStream(httpConn.getInputStream());
                response = inputStreamToString(in);
                throw new IOException("Server returned non-OK status: " + status);
            }
            return response;
        }


        private String inputStreamToString(InputStream in) {
            String result = "";
            if (in == null) {
                return result;
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(in,"UTF-8"));
                StringBuilder out = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line);
                }
                result = out.toString();
                reader.close();

                return result;
            } catch (Exception e) {
                return result;
            }

        }

}