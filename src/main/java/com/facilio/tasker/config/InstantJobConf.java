package com.facilio.tasker.config;

import com.facilio.tasker.job.InstantJob;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="instantjob")
public class InstantJobConf {

        private static final Logger logger = LogManager.getLogger(SchedulerJobConf.class.getName());
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
                return "(name, classname)=("+name+","+className+")";
            }
        }
    }

