/*
 *
 * Copyright 2013 Hewlett-Packard Development Company, L.P.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package hudson.plugins.gearman;

import hudson.model.Job;
import hudson.model.Computer;
import hudson.model.Run;
import hudson.security.ACL;

import java.io.IOException;

import jenkins.model.Jenkins;

import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.context.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains some useful utilities for this plugin
 *
 * @author Khai Do
 */
public class GearmanPluginUtil {

    private static final Logger logger = LoggerFactory
            .getLogger(Constants.PLUGIN_LOGGER_NAME);

    /*
     * This method returns the real computer name.  Master computer
     * by default has an empty string for the name.  But you
     * need to use "master" to tell jenkins to do stuff,
     * namely like schedule a build.
     *
     * @param Computer
     *      The computer to lookup
     *
     * @return
     *      "master" for the master computer or assigned name of the slave computer
     */
    public static String getRealName(Computer computer) {

        if (Jenkins.getActiveInstance().getComputer("") == computer) {
            return "master";
        } else {
            return computer.getName();
        }
    }

    /**
     * Function to finds the build with the unique build id.
     *
     * @param jobName
     *      The jenkins job or project name
     * @param buildNumber
     *      The jenkins build number
     * @return
     *      the build Run if found, otherwise return null
     */
    public static Run<?,?> findBuild(String jobName, int buildNumber) {

        SecurityContext oldContext = ACL.impersonate(ACL.SYSTEM);
        try {
            Job<?,?> project = Jenkins.getActiveInstance().getItemByFullName(jobName, Job.class);
            if (project != null){
                Run<?,?> run = project.getBuildByNumber(buildNumber);
                if (run != null) {
                    return run;
                }
            }
            return null;
        } finally {
            SecurityContextHolder.setContext(oldContext);
        }
    }

    /**
     * Sets description of the build
     *
     * @param build
     *      Build to set the description of
     * @param description
     *      New build description
     */
    public static void setBuildDescription(Run build, String description) throws IOException {
        SecurityContext oldContext = ACL.impersonate(ACL.SYSTEM);
        try {
            build.setDescription(description);
        } finally {
            SecurityContextHolder.setContext(oldContext);
        }
    }
}
