package com.facilio.tasker.config;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.tasker.job.InstantJob;

@XmlRootElement(name="instantjob")
public class InstantJobConf {

    private static final Logger logger = LogManager.getLogger(SchedulerJobConf.class.getName());
    private static final String JOB_NAME_KEY = "INSTANT_JOB";
    private static final String ACCOUNT_KEY = "INSTANT_JOB_ACCOUNT";
    private static final String INSTANT_JOB_QUEUE = "InstantJob";

    public static String getJobNameKey() {
        return JOB_NAME_KEY;
    }

    public static String getAccountKey() {
        return ACCOUNT_KEY;
    }

    public static String getInstantJobQueue() {
        return INSTANT_JOB_QUEUE;
    }

    private List<Job> jobs;

        public InstantJobConf() {

        }

        @XmlElement(name="job")
        public List<Job> getJobs() {
            return jobs;
        }
        public void setJobs(List<Job> jobs) {
            this.jobs = jobs;
        }

        @Override
        public String toString() {
            return String.valueOf(jobs.toString());
        }

        public final static class Job {
            private String name, className;
            private int transactionTimeout;
            private Class<? extends InstantJob> classObject = null;

            public Job() {

            }

            @XmlAttribute(name="name")
            public String getName() {
                return name;
            }
            public void setName(String name) {
                this.name = name;
            }

            @XmlAttribute(name="classname")
            public String getClassName() {
                return className;
            }
            public void setClassName(String className) {
                this.className = className;
            }

            @XmlAttribute(name="transactionTimeout")
            public int getTransactionTimeout() {
                return transactionTimeout;
            }

            public void setTransactionTimeout(int transactionTimeout) {
                this.transactionTimeout = transactionTimeout;
            }


            public Class<? extends InstantJob> getClassObject() {
                if(classObject != null) {
                    return classObject;
                }
                try {
                    if(className != null && !className.isEmpty()) {
                        classObject = (Class<? extends InstantJob>) Class.forName(className);
                    }
                } catch (ClassNotFoundException e) {
                    logger.log(Level.FATAL, "The folowing error occurred while parsing job name : "+name, e);
                }
                return classObject;
            }
            @Override
            public String toString() {
                return "(name, classname, transactionTimeout )=("+name+", "+className+", "+transactionTimeout+")";
            }
        }
    }

