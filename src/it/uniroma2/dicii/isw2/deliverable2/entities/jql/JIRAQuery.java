package it.uniroma2.dicii.isw2.deliverable2.entities.jql;

import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import org.apache.http.client.utils.URIBuilder;
import org.eclipse.jgit.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A JIRA Query, as an intermediate layer for handling the "high-level" parameters
 * with respect to the "low-level" JQL syntax.
 */
public class JIRAQuery {
    private static Logger log = LoggerInst.getSingletonInstance();

    private static final String JIRA_QUERY_PREFIX = "https://issues.apache.org/jira/rest/api/2/";
    private static final String JQL_QUERY_START = "jql";
    private static final String JIRA_PROJECT = "project";
    private static final String JIRA_FIELDS = "fields";
    private static final String JIRA_START_AT = "startAt";
    private static final String JIRA_MAX_RESULTS = "maxResults";

    private JIRAQueryType queryType;
    private JQLQuery jqlQuery = new JQLQuery();
    private Map<String, List<String>> urlParameters = new HashMap<>();
    private String projName;

    public JIRAQuery(JIRAQueryType queryType) {
        this.queryType = queryType;
    }

    public void setFields(String... fields) {
        this.urlParameters.put(JIRA_FIELDS, Arrays.asList(fields));
    }

    public void setStartAt(Integer startAt) {
        this.urlParameters.put(JIRA_START_AT, Arrays.asList(startAt.toString()));
    }

    public void setMaxResults(Integer maxResults) {
        this.urlParameters.put(JIRA_MAX_RESULTS, Arrays.asList(maxResults.toString()));
    }

    public JQLQuery getJqlQuery() {
        return jqlQuery;
    }

    /**
     * Compose the JQL Query and the final static URL that will be used for the JIRA JQL query.
     *
     * @return the final static URL for the JQL query
     */
    public URL compose() {
        URIBuilder b = null;
        URL url = null;
        try {
            if (this.queryType.equals(JIRAQueryType.JIRA_QUERY_TYPE_PROJECT)) {
                b = new URIBuilder(JIRA_QUERY_PREFIX + this.queryType.label + "/" + this.projName);
            } else {
                b = new URIBuilder(JIRA_QUERY_PREFIX + this.queryType.label + "?");
            }
            if (this.jqlQuery.getJQLPropertiesCount() > 0) {
                b.addParameter(JQL_QUERY_START, this.jqlQuery.compose());
            }
            for (Map.Entry<String, List<String>> entry : this.urlParameters.entrySet()) {
                if (!entry.getKey().equals(JIRA_PROJECT)) {
                    b.addParameter(entry.getKey(), StringUtils.join(entry.getValue(), ","));
                }
            }
            url = b.build().toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            log.severe(e.getMessage());
        }
        return url;
    }

    public void setProject(String projName) {
        this.projName = projName;
    }
}
