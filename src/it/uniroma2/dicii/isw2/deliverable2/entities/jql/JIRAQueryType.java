package it.uniroma2.dicii.isw2.deliverable2.entities.jql;

/**
 * JIRA Query types.
 */
public enum JIRAQueryType {
    JIRA_QUERY_TYPE_SEARCH("search"),
    JIRA_QUERY_TYPE_PROJECT("project");

    public final String label;

    private JIRAQueryType(String label) {
        this.label = label;
    }

}
