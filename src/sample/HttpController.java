package sample;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class HttpController {
    private HttpURLConnection connection;
    private URL url;
    private String method;

    HttpController(String url, String method) throws IOException {
        this.url = new URL(url);
        this.method = method.toUpperCase();
        createConnection();
        setRequestProperties();
        setRequestMethod();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(100000);
    }

    private void createConnection() throws IOException {
        connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
    }

    private void setRequestProperties() {
        connection.setRequestProperty("Content-Type", "application/json; utf-8");
        connection.setRequestProperty("Accept", "application/json");
    }

    private void setRequestMethod() throws ProtocolException {
        connection.setRequestMethod(method);
    }

    public String send(String ttn) throws IOException {
        URLConnection yc = url.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return content.toString();
    }

    public StringBuffer getResponse() throws IOException {
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();


        return content;
    }
}
