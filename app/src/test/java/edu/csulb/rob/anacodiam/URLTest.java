package edu.csulb.rob.anacodiam;

import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class URLTest {
    public void testURL() throws Exception {
        String url = "http://192.168.99.100:8000/api/rest-auth/login/";
        try {
            URL test_URL = new URL(url);
            HttpURLConnection urlConn = (HttpURLConnection) test_URL.openConnection();
            urlConn.connect();

            assertEquals(HttpURLConnection.HTTP_OK, urlConn.getResponseCode());
        } catch (IOException e) {
            System.err.println("Error creating HTTP connection");
            e.printStackTrace();
            throw e;
        }
    }
}


