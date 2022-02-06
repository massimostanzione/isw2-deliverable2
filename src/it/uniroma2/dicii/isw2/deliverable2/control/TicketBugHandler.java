package it.uniroma2.dicii.isw2.deliverable2.control;

import it.uniroma2.dicii.isw2.deliverable2.entities.*;
import it.uniroma2.dicii.isw2.deliverable2.entities.jql.JIRAQuery;
import it.uniroma2.dicii.isw2.deliverable2.entities.jql.JIRAQueryType;
import it.uniroma2.dicii.isw2.deliverable2.entities.jql.JQLQuery;
import it.uniroma2.dicii.isw2.deliverable2.enumerations.LabelingMethod;
import it.uniroma2.dicii.isw2.deliverable2.exceptions.VersionException;
import it.uniroma2.dicii.isw2.deliverable2.io.CSVExporterPrinter;
import it.uniroma2.dicii.isw2.deliverable2.utils.CollectionSorter;
import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Class for ticket and bug handling, mainly fetching them from projects.
 */
public class TicketBugHandler {
    private static final Logger log = LoggerInst.getSingletonInstance();

    public static List<Bug> fetchProjectBugs(String projName, List<Commit> commitList, List<Version> versionList,
                                             LabelingMethod avPredMethod) {
        log.info(() -> "Initializing ticket (and related commits) list...");
        Integer i = 0;
        Integer j = 0;
        Integer total = 1;
        Double matchedTickets = 0.0;
        Integer n = 0;
        Double proportionAvgNum = 1.0;
        ArrayList<Ticket> tickets = new ArrayList<>();
        List<Bug> bugList = new ArrayList<>();
        do {
            j = i + 1000;
            // Build a JIRA Query to found all the tickets related to the project
            JIRAQuery q = new JIRAQuery(JIRAQueryType.JIRA_QUERY_TYPE_SEARCH);
            q.getJqlQuery().setProjectName(projName);
            q.getJqlQuery().setIssueType(JQLQuery.JQL_ISSUE_TYPE_BUG);
            q.getJqlQuery().setStatus(JQLQuery.JQL_STATUS_CLOSED, JQLQuery.JQL_STATUS_RESOLVED, JQLQuery.JQL_STATUS_DONE);
            q.getJqlQuery().setResolution(JQLQuery.JQL_RESOLUTION_FIXED, JQLQuery.JQL_RESOLUTION_DONE);
            q.setFields(JQLQuery.JQL_FIELD_KEY, JQLQuery.JQL_FIELD_RESOLUTIONDATE, JQLQuery.JQL_FIELD_VERSIONS, JQLQuery.JQL_FIELD_FIXVERSIONS, JQLQuery.JQL_FIELD_CREATED);
            q.setStartAt(i);
            q.setMaxResults(j);
            String url = q.compose().toString();
            try {
                JSONObject json = JSONHandler.readJsonFromUrl(url);
                JSONArray issues = json.getJSONArray("issues");
                total = json.getInt("total");
                for (; i < total && i < j; i++) {
                    // Iterate through each ticket
                    String key = issues.getJSONObject(i % 1000).get(JQLQuery.JQL_FIELD_KEY).toString();
                    String creat = issues.getJSONObject(i % 1000).getJSONObject(JQLQuery.JQL_FIELDS).get(JQLQuery.JQL_FIELD_CREATED).toString();
                    String res = issues.getJSONObject(i % 1000).getJSONObject(JQLQuery.JQL_FIELDS).get(JQLQuery.JQL_FIELD_RESOLUTIONDATE).toString();
                    JSONArray versions = issues.getJSONObject(i % 1000).getJSONObject(JQLQuery.JQL_FIELDS).getJSONArray(JQLQuery.JQL_FIELD_VERSIONS);

                    Ticket iteratedTicket = new Ticket(key, creat, res);
                    List<Version> convertedVersions = new ArrayList<>();
                    // Convert versions
                    for (Integer l = 0; l < versions.length(); l++) {
                        String versionName = versions.getJSONObject(l).getString(JQLQuery.JQL_FIELD_NAME);
                        Version v = null;
                        try {
                            v = VersionHandler.getVersionByName(versionName, versionList);
                        } catch (VersionException e) {
                            e.printStackTrace();
                        }
                        convertedVersions.add(v);
                    }
                    // Sort tversions based on ID
                    try {
                        CollectionSorter.sort(convertedVersions, Version.class.getDeclaredMethod("getSortedID"));
                    } catch (NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                    }

                    // Search for commits related to ticket
                    List<Commit> iteratedCommitList = CommitHandler.fetchCommitsRelatedToTicket(iteratedTicket, commitList);
                    log.finest("Ticket " + iteratedTicket.getID() + ": " + iteratedCommitList.size() + " commit(s) found.");

                    // Fully instance a bug object related to the ticket
                    if (iteratedCommitList.size() > 0) {
                        matchedTickets++;
                        iteratedTicket.setCommitList(iteratedCommitList);
                        Bug b = instanceBug(iteratedTicket, proportionAvgNum, n, convertedVersions, versionList, avPredMethod);
                        if (b == null) break;
                        if (b.getBugLifecycle() != null) {
                            bugList.add(b);
                            proportionAvgNum += b.getBugLifecycle().getProportionContribute();
                            n += b.getBugLifecycle().isIVPredicionNeeded() ? 0 : 1;
                        }
                    }
                    tickets.add(iteratedTicket);
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } while (i < total);

        Double finalMatchedTickets = matchedTickets;
        log.info(() -> "- Linkage probability = " + (finalMatchedTickets / tickets.size()));

        // Sort tickets by their creation date
        try {
            CollectionSorter.sort(tickets, Ticket.class.getDeclaredMethod("getCreationTimestamp"));
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        CSVExporterPrinter.getSingletonInstance().convertAndExport(tickets, "/output/" + projName + "/inspection/tickets.csv");
        log.info(() -> "- " + tickets.size() + " tickets found. ");

        // Sort bugs by ticket creation date
        try {
            CollectionSorter.sort(bugList, Bug.class.getDeclaredMethod("getCreationTimestamp"));
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        CSVExporterPrinter.getSingletonInstance().convertAndExport(bugList, "/output/" + projName + "/inspection/bugs.csv");
        printToGraphicVisualizer(projName, bugList, versionList);
        return bugList;
    }

    /**
     * Print to the Graphic Bug Visualizer
     *
     * @param projName    project name
     * @param bugList     bug list
     * @param versionList version list
     */
    private static void printToGraphicVisualizer(String projName, List<Bug> bugList, List<Version> versionList) {
        List<GraphicBugLifecycleVisualizerRecord> ret = new ArrayList<>();
        for (Bug b : bugList) {
            GraphicBugLifecycleVisualizerRecord record = new GraphicBugLifecycleVisualizerRecord(versionList, bugList);
            record.setID(b.getTicket().getID());
            for (Version v : versionList) {
                String val = "";
                if (b.getBugLifecycle().getIV().equals(v)) {
                    val += "I";
                }
                if (b.getBugLifecycle().getOV().equals(v)) {
                    val += "O";
                }
                if ((b.getBugLifecycle().getAVs().contains(v))) {
                    val += "A";
                }
                if (b.getBugLifecycle().getFV().equals(v)) {
                    val += "F";
                }
                record.appendLabel(val != "" ? val : "-");
            }
            List<JIRAAffectedVersionsCheck> errors = new ArrayList<>();
            errors.addAll(b.getBugLifecycle().getJIRACheck());
            record.setJIRACheck(errors);
            ret.add(record);
        }
        CSVExporterPrinter.getSingletonInstance().convertAndExport(ret, "/output/" + projName + "/inspection/graphicBugLifecycleVisualizer.csv");
    }

    /**
     * Fully instance a <code>Bug</code> object
     *
     * @param iteratedTicket   ticket related to the bug
     * @param proportionAvgNum for the computing of proportion
     * @param n                for the computing of proportion
     * @param JIRAVersions     list of versions to be searched in
     * @param versionList      all the project's versions
     * @param avPredMethod     labeling method
     * @return the <code>Bug</code> object related to the <code>iteratedTicket</code> ticket.
     */
    private static Bug instanceBug(Ticket iteratedTicket, Double proportionAvgNum, Integer n, List<Version> JIRAVersions, List<Version> versionList, LabelingMethod avPredMethod) {
        Bug b = new Bug();
        b.setTicket(iteratedTicket);
        try {
            b.setBugLifecycle(BugLifecycleFixer.computeBugLifecycle(proportionAvgNum, n, JIRAVersions,
                    iteratedTicket,
                    versionList, avPredMethod));
        } catch (VersionException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }
}