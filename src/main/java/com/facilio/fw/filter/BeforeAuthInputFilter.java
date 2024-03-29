package com.facilio.fw.filter;

import com.facilio.aws.util.FacilioProperties;
import com.facilio.security.SecurityRequestWrapper;
import com.facilio.security.requestvalidator.Executor;
import com.facilio.security.requestvalidator.NodeError;
import com.facilio.security.requestvalidator.config.Config;
import com.facilio.security.requestvalidator.config.ConfigParser;
import com.facilio.security.requestvalidator.config.RequestConfig;
import com.facilio.security.requestvalidator.context.RequestContext;
import com.facilio.security.requestvalidator.retree.Matcher;
import com.facilio.security.requestvalidator.retree.URLReTree;
import com.facilio.util.RequestUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.json.JSONObject;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;


@Log4j
public class BeforeAuthInputFilter implements Filter {
    private URLReTree urlReTree;
    private URLReTree execlutionUrlReTree;
    private Config config;
    private Set<String> rateLimitUrlSet = new HashSet<>();

    public String[] execludeFile() throws IOException {
        File file = new File(ConfigParser.class.getClassLoader().getResource("validation.txt").getFile());
        BufferedReader br
                = new BufferedReader(new FileReader(file));
        List<String> list= new ArrayList<>();
        String st;
        while ((st = br.readLine()) != null) {
            list.add(st);
        }
        String[] arr = new String[list.size()];
        list.toArray(arr);
        return arr;
    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            this.config = new ConfigParser().read("security");
            this.urlReTree = new URLReTree(this.config.getAllPaths());
            String[] s = execludeFile();
            this.execlutionUrlReTree = new URLReTree(s);

            File file = new File(ConfigParser.class.getClassLoader().getResource("rateLimitCheck.txt").getFile());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String str;
            while ((str = br.readLine()) != null) {
                if (!str.isEmpty()) {
                    rateLimitUrlSet.add(str.trim());
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        SecurityRequestWrapper securityRequestWrapper = new SecurityRequestWrapper((HttpServletRequest) servletRequest);

        Matcher matcher = this.urlReTree.matcher(httpServletRequest.getRequestURI());
        Matcher exclution = this.execlutionUrlReTree.matcher(httpServletRequest.getRequestURI());
        if (!(matcher.isMatch() && config.getRequestConfig(httpServletRequest.getMethod(),matcher.getMatchedPattern())!=null)) {
            if(!httpServletRequest.getRequestURI().startsWith("/api")) {
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            if ( !isAllowed() || exclution.isMatch()) {
                if(!exclution.isMatch()){
                    log(securityRequestWrapper,FacilioProperties.getEnvironment()+" Validation missing for : " + httpServletRequest.getRequestURI());
                }
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                log(securityRequestWrapper,"Validation missing for : " + httpServletRequest.getRequestURI());
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "Validation missing for : " + httpServletRequest.getRequestURI());
                write(errorMap, 400, servletResponse);
            }
            return;
        }

        try {
            RequestContext requestContext = new RequestContext(securityRequestWrapper, matcher.getMatchMap());
            String matchedPattern = matcher.getMatchedPattern();
            RequestConfig requestConfig = config.getRequestConfig(requestContext.getMethod(), matchedPattern);

            Executor executor = new Executor(requestConfig, requestContext);

            NodeError nodeError = executor.validatePreAuth();
            if (nodeError != null) {
                log(securityRequestWrapper, nodeError.getErrorMessage());
                if (isAllowed()) {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("message", nodeError.getErrorMessage());
                    write(errorMap, 400, servletResponse);
                    return;
                }
            }
            securityRequestWrapper.setAttribute("executor", executor);
        } catch (Exception ex) {
            if(StringUtils.isEmpty(ex.getMessage())){
                log(securityRequestWrapper, "Error validating for "+httpServletRequest.getRequestURI()+" of validating pattern: "+matcher.getMatchedPattern()+" "+ex.toString());
            }else {
                log(securityRequestWrapper, "Error validating for " + httpServletRequest.getRequestURI()+" of validating pattern: "+matcher.getMatchedPattern()+ " " + ex.getMessage());
            }
            if (isAllowed()) {
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("message", "Error validating "+ex.getMessage());
                write(errorMap, 500, servletResponse);
                return;
            }
        }
        securityRequestWrapper.reset();
        filterChain.doFilter(securityRequestWrapper, servletResponse);
    }

    private void log(SecurityRequestWrapper securityRequestWrapper, String message) throws MalformedURLException {
        String referrer = securityRequestWrapper.getHeader(HttpHeaders.REFERER);
        if (referrer != null && !"".equals(referrer.trim())) {
            URL url = new URL(referrer);
            if(url != null) {
                String deviceTye = securityRequestWrapper.getHeader("X-Device-Type");
                String OrgId = securityRequestWrapper.getHeader("X-Org-Id");
                message = message.concat(" Tab - "+securityRequestWrapper.getHeader("X-Tab-Id")+" reqURI - " +securityRequestWrapper.getRequestURI()+" referrerURI - "+url.getPath()+ " DeviceType - " +deviceTye+ " OrgId "+OrgId);
            }
        }
        LoggingEvent event = new LoggingEvent(LOGGER.getName(), LOGGER, Level.INFO, message, null);
        RequestUtil.addRequestLogEvents(securityRequestWrapper, event);
        LOGGER.callAppenders(event);
    }

    private void write(Map messageMap, int httpCode, ServletResponse servletResponse) throws IOException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setStatus(httpCode);
        httpServletResponse.setContentType("application/json");
        httpServletResponse.setCharacterEncoding("UTF-8");
        PrintWriter out = httpServletResponse.getWriter();
        out.println(new JSONObject(messageMap).toString());
        out.flush();
    }

    private boolean isAllowed() {
        return (!(FacilioProperties.isProduction() || FacilioProperties.isOnpremise() || FacilioProperties.getEnvironment().equalsIgnoreCase("stage2"))) && FacilioProperties.isCheckPrivilegeAccess();
    }
    @Override
    public void destroy() {

    }
}
