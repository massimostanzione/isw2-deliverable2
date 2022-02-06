package it.uniroma2.dicii.isw2.deliverable2.control;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to manage JIRA REST API responses in JSON format.
 */
public class JSONHandler {

    private JSONHandler() {
    }

    /**
     * Given an <code>url</code>, handle JSON response.
     *
     * @param url the target URL
     * @return a <code>JSONObject</code> with the response
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = null;
        is = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String jsonText = readAll(rd);
        is.close();
        return new JSONObject(jsonText);
    }

    /**
     * Read the content of a <code>Reader</code>.
     *
     * @param rd reader to be read
     * @return what is read in <code>rd</code>
     * @throws IOException
     */
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
