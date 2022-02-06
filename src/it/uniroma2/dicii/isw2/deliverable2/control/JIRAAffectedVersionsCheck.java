package it.uniroma2.dicii.isw2.deliverable2.control;

/**
 * Every possible result among those considered for the analysis of the AVs
 * reported by JIRA.
 */
public enum JIRAAffectedVersionsCheck {

    /**
     * No AVs are reported in JIRA. An estimate will be performed in order to
     * determinate the AVs.
     */
    NOT_REPORTED,

    /**
     * AVs reported by JIRA are consistent and without any error.
     */
    OK,

    /**
     * The first AV reported by JIRA (to be considered as IV) is subsequent to the
     * OV, which is computed based on the ticket opening date.
     */
    IV_AFTER_OV,

    /**
     * At least one of the AVs reported by JIRA is subsequent to the FV, which is
     * computed based on the ticket resolution date.
     */
    AV_AFTER_FV,

    /**
     * The FV, computed based on the ticket resolution date, is included into the
     * AVs reported by JIRA, which is in contrast with the AVs formal definition.
     */
    FV_AS_AV,

    /**
     * The AVs reported by JIRA are not subsequent between the IV and the FV
     * (excluded by formal definition).
     */
    AVS_NOT_CONSISTENT
}
