package edu.csulb.rob.anacodiam;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public void testURL() throws Exception {
        String strUrl = "http://stackoverflow.com/about";

        try {
        URL url = new URL(strUrl);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.connect();

        assertEquals(HttpURLConnection.HTTP_OK, urlConn.getResponseCode());
        } catch (IOException e) {
        System.err.println("Error creating HTTP connection");
        e.printStackTrace();
        throw e;
        }
        }