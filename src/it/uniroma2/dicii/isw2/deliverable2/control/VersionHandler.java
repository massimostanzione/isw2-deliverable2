package it.uniroma2.dicii.isw2.deliverable2.control;

import it.uniroma2.dicii.isw2.deliverable2.entities.GitWorkingCopy;
import it.uniroma2.dicii.isw2.deliverable2.entities.Version;
import it.uniroma2.dicii.isw2.deliverable2.entities.jql.JIRAQuery;
import it.uniroma2.dicii.isw2.deliverable2.entities.jql.JIRAQueryType;
import it.uniroma2.dicii.isw2.deliverable2.exceptions.VersionException;
import it.uniroma2.dicii.isw2.deliverable2.io.CSVExporterPrinter;
import it.uniroma2.dicii.isw2.deliverable2.utils.CollectionSorter;
import it.uniroma2.dicii.isw2.deliverable2.utils.LoggerInst;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Version handling and related utilities methods.
 */
public class VersionHandler {

    private static List<Version> versionList = new ArrayList<>();
    private static Logger log = LoggerInst.getSingletonInstance();

    private VersionHandler() {
    }

    public static List<Version> getVersionList() {
        return versionList;
    }

    /**
     * Uses a JIRA Query to fetch all the versions of the project.
     * Versions discarded:
     * (a) versions without release date;
     * (b) versions in a "not final" status, like <i>beta</i> versions;
     * (c) versions not matched in the GitHub repository.
     *
     * @param projName project name
     * @param wc       working copy
     * @return list of all the fetched version
     * @see JIRAQuery
     */
    public static List<Version> fetchProjectVersions(String projName, GitWorkingCopy wc) {
        log.info(() -> "Initializing version list...");
        // Build JIRA Query to find all the versions of the project
        JIRAQuery query = new JIRAQuery(JIRAQueryType.JIRA_QUERY_TYPE_PROJECT);
        query.setProject(projName);
        String url = query.compose().toString();
        JSONObject json;
        try {
            json = JSONHandler.readJsonFromUrl(url);
            JSONArray versions = json.getJSONArray("versions");
            versionList = filterVersions(versions, wc);
        } catch (IOException | JSONException | ParseException e) {
            e.printStackTrace();
        }
        try {
            // Sort versions by their release date
            CollectionSorter.sort(versionList, Version.class.getDeclaredMethod("getVersionDate"));
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        for (Integer j = 0; j < versionList.size(); j++) {
            versionList.get(j).setSortedID(j + 1);
        }
        CSVExporterPrinter.getSingletonInstance().convertAndExport(versionList, "/output/" + projName + "/inspection/versions.csv");
        log.info(() -> "- " + versionList.size() + " versions found.");
        return versionList;
    }

    private static List<Version> filterVersions(JSONArray versions, GitWorkingCopy wc) throws JSONException, ParseException {
        List<Version> filteredVersionList = new ArrayList<>();
        for (int i = 0; i < versions.length(); i++) {
            String name = "";
            // Discard versions: (a)
            if (versions.getJSONObject(i).has("releaseDate")) {
                if (versions.getJSONObject(i).has("name"))
                    name = versions.getJSONObject(i).get("name").toString();
                String finalName = name;
                // Discard versions: (b)
                if (!name.contains("beta")) {
                    String date = versions.getJSONObject(i).get("releaseDate").toString();
                    Version v = new Version();
                    v.setName(name);
                    v.setVersionDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
                    // Discard versions: (c)
                    if (GitHubMiddleware.findGitHubHashID(name, wc) != null) {
                        v.setHashID(GitHubMiddleware.findGitHubHashID(name, wc));
                        filteredVersionList.add(v);
                    } else {
                        log.fine(() -> "- Skipping version " + finalName + ", not matched in GitHub working copy.");
                    }
                } else
                    log.fine(() -> "- Skipping version " + finalName + ", because of its \"not final\" status.");
            } else {
                String finalName = name;
                log.fine(() -> "- Skipping version " + finalName + ", because no release date associated.");
            }
        }
        return filteredVersionList;
    }

    /**
     * Given a <code>versionList</code>, return the version among them identified by <code>versionName</code>
     *
     * @param versionName name of the version to be found
     * @param versionList list of versions to be searched in
     * @return searched version, or VersionException thrown if not found
     * @throws VersionException
     */
    public static Version getVersionByName(String versionName, List<Version> versionList) throws VersionException {
        for (Version v : versionList) {
            if (v.getName().equals(versionName))
                return v;
        }
        throw new VersionException("Version not found for name: " + versionName);
    }

    /**
     * Given a <code>date</code>, fetch the version in <code>versionList</code> that refers to it.
     *
     * @param date        date to which search for related version
     * @param versionList list of versions to be searched in
     * @return the searched version, or <code>null</code> if not found
     * @throws VersionException
     */
    public static Version getCurrentVersionByDate(Date date, List<Version> versionList) {
        return getVersionByDateExecutive(date, versionList, 0);
    }

    /**
     * Given a <code>date</code>, fetch the version in <code>versionList</code> that refers to the subsequent of it.
     *
     * @param date        date to which search for subsequent version
     * @param versionList list of versions to be searched in
     * @return the subsequent version, or <code>null</code> if not found
     * @throws VersionException
     */
    public static Version getNextVersionByDate(Date date, List<Version> versionList) {
        return getVersionByDateExecutive(date, versionList, 1);
    }

    /**
     * Given a date and an increment, find <code>incr</code> versions subsequent to it
     *
     * @param date        date to which search for version
     * @param versionList version list in which search for the version
     * @param incr        how much increment from current version
     * @return the searched version, or <code>null</code> if not found
     */
    private static Version getVersionByDateExecutive(Date date, List<Version> versionList, Integer incr) {
        for (Integer i = 0; i < versionList.size(); i++) {
            Version v = versionList.get(i);
            if (v.getVersionDate().after(date)) {
                return (i - 1 + incr) < 0 ? versionList.get(0) : versionList.get(i - 1 + incr);
            }
        }
        return null;
    }

    /**
     * Given two versions, return all the versions in between of them.
     *
     * @param left         first version
     * @param right        last version
     * @param versionList2 list of versions to be searched in
     * @return list of versions between <code>left</code> and <code>right</code>, or null if not found
     */
    public static List<Version> getVersionsBetween(Version left, Version right, List<Version> versionList2) {
        List<Version> ret = new ArrayList<>();
        if (left.equals(right)) {
            ret.add(left);
            return ret;
        }
        // NOTICE: right is excluded (e.g. FV)
        Integer incr = left.getSortedID() == 0 ? 1 : 0;
        for (Integer i = left.getSortedID(); i < right.getSortedID(); i++) {
            ret.add(versionList2.get(i - 1 + incr));
        }
        return ret;
    }

    /**
     * Given a version ID, search for the version object matching it
     *
     * @param id version ID
     * @return a <code>Version</code> object matching the <code>id</code>
     * @throws VersionException
     */
    public static Version getVersionBySortedID(Integer id) throws VersionException {
        for (Version v : versionList) {
            if (v.getSortedID().equals(id)) {
                return v;
            }
        }
        throw new VersionException("Version not found for SortedID: " + id);
    }
}