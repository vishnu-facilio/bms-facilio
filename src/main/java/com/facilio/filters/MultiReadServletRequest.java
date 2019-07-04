package com.facilio.filters;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

import com.facilio.aws.util.AwsUtil;
import com.opensymphony.xwork2.ActionContext;

public class MultiReadServletRequest extends HttpServletRequestWrapper {

	private static MessageDigest md;
	private ByteArrayOutputStream cachedRequest;
    private String jsonContentType = "application/json";

	public MultiReadServletRequest(HttpServletRequest request) throws IOException {
		super(request);
		if (shouldCacheRequest(request)) {
			BufferedReader bufferReader = new BufferedReader(request.getReader());
	        String line;
	        StringBuilder buffer = new StringBuilder();
            while ((line = bufferReader.readLine()) != null) {
                buffer.append(line);
            }
            cachedRequest = new ByteArrayOutputStream();
            cachedRequest.write(buffer.toString().getBytes());
		}
	}
	
	private boolean shouldCacheRequest(HttpServletRequest request) {
//		if (AwsUtil.isProduction()) {
//			return false;
//		}
		Parameter parameter = ActionContext.getContext().getParameters().get("cacheUrl");
		boolean cacheUrl = false;
		if (parameter != null) {
			cacheUrl = Boolean.valueOf(parameter.getValue());
		}
		if (!cacheUrl) {
			return false;
		}
		return jsonContentType.equalsIgnoreCase(readContentType(request));
	}

	public boolean isCachedRequest() {
		return cachedRequest != null;
	}
	
	public String getContentHash() {
		if (isCachedRequest()) {
			String string = new String(cachedRequest.toByteArray());
			return cryptWithMD5(string);
		}
		throw new IllegalAccessError("Should be cached request to get content hash");
	}
	
	private static String cryptWithMD5(String word) {
		if (StringUtils.isNotEmpty(word)) {
	        try {
	        	md = MessageDigest.getInstance("MD5");
	            byte[] passBytes = word.getBytes();
	            md.reset();
	            byte[] digested = md.digest(passBytes);
	            StringBuilder sb = new StringBuilder();
	            for (byte aDigested : digested) {
	                sb.append(Integer.toHexString(0xff & aDigested));
	            }
	            return sb.toString();
	        } catch (NoSuchAlgorithmException ex) {
	        }
		}
        return null;
    }
	
	protected String readContentType(HttpServletRequest request) {
        String contentType = request.getHeader("Content-Type");

        if (contentType != null && contentType.contains(";")) {
            contentType = contentType.substring(0, contentType.indexOf(";")).trim();
        }
        return contentType;
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		if (cachedRequest != null) {
			return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(cachedRequest.toByteArray())));
		}
		return super.getReader();
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		if (cachedRequest != null) {
			return new ContentCacheServletInputStream(cachedRequest.toByteArray());
		}
		return super.getInputStream();
	}

	private class ContentCacheServletInputStream extends ServletInputStream {

		private byte[] b;
		private int position = 0;
		
		public ContentCacheServletInputStream(byte[] b) {
			this.b = b;
		}
		
		@Override
		public boolean isFinished() {
			return position == (b.length - 1);
		}

		@Override
		public boolean isReady() {
			return !isFinished();
		}

		@Override
		public void setReadListener(ReadListener readListener) {
		}
		
		@Override
		public synchronized void reset() throws IOException {
			position = 0;
		}

		@Override
		public int read() throws IOException {
			if (b == null) {
				throw new IOException("Data cannot be null");
			}
			if (position < b.length) {
				return b[position ++];
			}
			return -1;
		}
		
	}
}
