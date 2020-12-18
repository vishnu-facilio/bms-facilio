package com.facilio.tasker;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.chain.FacilioContext;
import com.facilio.db.util.DBConf;
import com.facilio.queue.FacilioObjectQueue;
import com.facilio.tasker.config.InstantJobConf;
import com.facilio.tasker.executor.FacilioInstantJobExecutor;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FacilioInstantJobScheduler {
    private static final Logger LOGGER = LogManager.getLogger(FacilioScheduler.class.getName());
    private static final Map<String, InstantJobConf.Job> INSTANT_JOBS_MAP = new HashMap<>();
    private static final String INSTANT_JOB_FILE = "conf/instantJobs.xml";
    private static final String INSTANT_JOB_EXECUTOR_FILE = "conf/instantjobexecutors.yml";
    private static final Map<String, FacilioInstantJobExecutor> executors = new HashMap<>();

    public static void init() throws Exception {
        getInstanceJobFromConf();
        LOGGER.info("Instant Jobs : ");
        LOGGER.info(INSTANT_JOBS_MAP);
        startExecutors();
    }

    private static List<Long> parseOrgIds (List<? extends Object> orgs) {
        if (CollectionUtils.isNotEmpty(orgs)) {
            return orgs.stream()
                        .map(FacilioUtil::parseLong)
                        .collect(Collectors.toList());
        }
        return null;
    }

    private static void startExecutors() throws IOException {
        Map<String, Object> executorsConf = null;
        try {
            executorsConf = FacilioUtil.loadYaml(INSTANT_JOB_EXECUTOR_FILE);
        } catch (IOException e) {
            LOGGER.error("Error occurred while parsing "+INSTANT_JOB_EXECUTOR_FILE, e);
            throw e;
        }

        boolean isInstantJobServer = Boolean.parseBoolean(FacilioProperties.getConfig("instantJobServer"));
        List<Long> globalIncludedOrgs = null, globalExcludedOrgs = null;
        Map<String, Object> globalParams = (Map<String, Object>) executorsConf.get("global");
        if (MapUtils.isNotEmpty(globalParams)) {
            globalIncludedOrgs = parseOrgIds((List<? extends Object>) globalParams.get("include"));
            globalExcludedOrgs =  parseOrgIds((List<? extends Object>) globalParams.get("exclude"));
        }

        List<Map<String, Object>> executorList = (List<Map<String, Object>>) executorsConf.get("executors");
        if (CollectionUtils.isNotEmpty(executorList)) {
            for (Map<String, Object> ex : executorList) {
                String name = (String) ex.get("name");
                String tableName = (String) ex.get("tableName");
                int maxThreads = (int) ex.getOrDefault("maxThreads", -1);
                int queueSize = (int) ex.getOrDefault("queueSize", -1);
            	int dataRetention = (int) ex.get("dataRetention");
                int pollingFrequency = (int) ex.get("pollingFrequency");
                List<Long> include = parseOrgIds((List<String>) ex.get("include"));
                List<Long> exclude = parseOrgIds((List<String>) ex.get("exclude"));
                FacilioInstantJobExecutor executor = new FacilioInstantJobExecutor(name,
                                                                                    tableName,
                                                                                    maxThreads,
                                                                                    queueSize,
                                                                                    dataRetention,
                                                                                    pollingFrequency,
                                                                                    CollectionUtils.isEmpty(include) ? globalIncludedOrgs : include,
                                                                                    CollectionUtils.isEmpty(exclude) ? globalExcludedOrgs : exclude
                                                                                );
                executors.put(name, executor);
                if (isInstantJobServer) {
                    executor.startExecutor();
                }
            }
        }
    }

    public static void stopExecutors() {
        for (FacilioInstantJobExecutor executor : executors.values()) {
            executor.stopExecutor();
        }
    }

    public static void addInstantJob(String executorName, String jobName, FacilioContext context) throws Exception {
        if (StringUtils.isEmpty(executorName)) {
            throw new IllegalArgumentException("Executor name cannot be null while adding instant job");
        }
        FacilioInstantJobExecutor queue = executors.get(executorName);
        if (queue == null) {
            throw new IllegalArgumentException("No such Instant job executor with name : "+executorName);
        }
        if (StringUtils.isEmpty(jobName)) {
            throw new IllegalArgumentException("Job name cannot be null while adding instant job");
        }
        if (getInstantJob(jobName) == null) {
            throw new IllegalArgumentException("No such Instant job with name : "+jobName);
        }
        context.put(InstantJobConf.getJobNameKey(), jobName);
        context.put(InstantJobConf.getAccountKey(), AccountUtil.getCurrentAccount());
        queue.addJob(jobName, context);
    }

    private static void addInstantJobToMap(String jobName, InstantJobConf.Job job){
        INSTANT_JOBS_MAP.put(jobName, job);
    }

    public static InstantJobConf.Job getInstantJob(String jobName) {
        return INSTANT_JOBS_MAP.get(jobName);
    }

    private static void getInstanceJobFromConf() throws JAXBException {
        ClassLoader classLoader = FacilioScheduler.class.getClassLoader();
        File instantJobXml = new File(classLoader.getResource(INSTANT_JOB_FILE).getFile());

        JAXBContext jaxbContext = JAXBContext.newInstance(InstantJobConf.class);
        InstantJobConf instantJobConf = (InstantJobConf) jaxbContext.createUnmarshaller().unmarshal(instantJobXml);

        LOGGER.info(instantJobConf);

        if(instantJobConf.getJobs() != null) {
            for(InstantJobConf.Job jobConf : instantJobConf.getJobs()) {
                String name = jobConf.getName();
                if(name != null && !name.isEmpty() && jobConf.getClassObject() != null) {
                    try {
                        addInstantJobToMap(name, jobConf);
                    }
                    catch(Exception e) {
                        LOGGER.error("The folowing error occurred while parsing job name : "+name, e);
                    }
                }
                else {
                    LOGGER.error("Invalid job configuration : "+jobConf);
                }
            }
        }
    }
    
    public static void deleteExecutorsInstantJobQueueTable() throws Exception {
    	for (FacilioInstantJobExecutor executor : executors.values()) {
    		executor.deleteInstantJobQueueTable();
        }
    }
}
