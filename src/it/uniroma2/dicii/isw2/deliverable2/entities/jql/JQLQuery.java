package it.uniroma2.dicii.isw2.deliverable2.entities.jql;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A JQL query, handling all the "low-level" JQL tags an details for a JIRA query.
 */
public class JQLQuery {
    // A collection of JQL tags
    private static final String JQL_PROJECT_NAME = "project";
    private static final String JQL_ISSUE_TYPE = "issueType";
    private static final String JQL_STATUS = "status";
    private static final String JQL_RESOLUTION = "resolution";
    public static final String JQL_ISSUE_TYPE_BUG = "Bug";
    public static final String JQL_STATUS_CLOSED = "closed";
    public static final String JQL_STATUS_RESOLVED = "resolved";
    public static final String JQL_STATUS_DONE = "done";
    public static final String JQL_RESOLUTION_FIXED = "fixed";
    public static final String JQL_RESOLUTION_DONE = "done";
    public static final String JQL_FIELDS = "fields";
    public static final String JQL_FIELD_KEY = "key";
    public static final String JQL_FIELD_RESOLUTIONDATE = "resolutiondate";
    public static final String JQL_FIELD_FIXVERSIONS = "fixVersions";
    public static final String JQL_FIELD_CREATED = "created";
    public static final String JQL_FIELD_VERSIONS = "versions";
    public static final String JQL_FIELD_NAME = "name";

    private Map<String, List<String>> jqlProperties = new HashMap<>();

    public Integer getJQLPropertiesCount() {
        return jqlProperties.size();
    }

    public void setProjectName(String projName) {
        this.jqlProperties.put(JQL_PROJECT_NAME, Arrays.asList(projName));
    }

    public void setIssueType(String... issueTypes) {
        this.jqlProperties.put(JQL_ISSUE_TYPE, Arrays.asList(issueTypes));
    }

    public void setStatus(String... statusList) {
        this.jqlProperties.put(JQL_STATUS, Arrays.asList(statusList));
    }

    public void setResolution(String... resolution) {
        this.jqlProperties.put(JQL_RESOLUTION, Arrays.asList(resolution));
    }

    /**
     * Compose a ready-to-use JQL query.
     *
     * @return a JQL query.
     */
    public String compose() {
        Integer i = -1;
        StringBuilder bld = new StringBuilder();
        bld.append("(");
        for (Map.Entry<String, List<String>> entry : this.jqlProperties.entrySet()) {
            i++;
            for (Integer j = 0; j < entry.getValue().size(); j++) {
                bld.append("\"" + entry.getKey() + "\"=");
                bld.append("\"" + entry.getValue().get(j) + "\"");
                if (j < entry.getValue().size() - 1) {
                    bld.append("OR");
                }
            }
            bld.append(")");
            if (i < this.jqlProperties.entrySet().size() - 1) {
                bld.append("AND(");
            }
        }
        return bld.toString();
    }
}
