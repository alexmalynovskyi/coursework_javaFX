package sample;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

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
        connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("Host", "justin.ua");
        connection.setRequestProperty("Origin", "https://justin.ua");
    }

    private void setRequestMethod() throws ProtocolException {
        connection.setRequestMethod(method);
    }

    public Map<String, String> sendGet(String url) throws IOException {

        HttpURLConnection httpClient =
                (HttpURLConnection) new URL(url).openConnection();

        httpClient.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        httpClient.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
        httpClient.setRequestProperty("Accept-Encoding", "utf8");
        httpClient.setRequestProperty("Accept", "*/*");
        httpClient.setRequestProperty("Connection", "keep-alive");

        // optional default is GET
        httpClient.setRequestMethod("GET");

        int responseCode = httpClient.getResponseCode();
        Map<String, List<String>> headerFields = httpClient.getHeaderFields();
        List<String> cookiesHeader = headerFields.get("Set-Cookie");
        Map<String, String> responseMap = new HashMap<>();
        if(cookiesHeader != null){
            String sessionCookie = cookiesHeader.get(0);
            String csrfTokenCookie = cookiesHeader.get(1);
            responseMap.put("sessionCookie", sessionCookie);
            responseMap.put("csrfToken", csrfTokenCookie);
        }

        StringBuilder response = new StringBuilder();
        String line;

        Reader reader = null;
        if ("gzip".equals(httpClient.getContentEncoding())) {
            reader = new InputStreamReader(new GZIPInputStream(httpClient.getInputStream()));
        }
        else {
            reader = new InputStreamReader(httpClient.getInputStream());
        }

        while (true) {
            int ch = reader.read();
            if (ch==-1) {
                break;
            }
            response.append((char)ch);
        }

        responseMap.put("response", response.toString());
        return responseMap;
    }
    public String sendPost(String jsonPayload, Map<String, String> cookieHash) throws IOException {
        String  sessionCookie = cookieHash.get("sessionCookie").split(" ", 0)[0];
        String  csrfCookie = cookieHash.get("csrfToken").split(" ", 0)[0];
        String cookie = csrfCookie + " " + sessionCookie .substring(0, sessionCookie.length() - 1);

        connection.setRequestProperty("Cookie", cookie);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        try(OutputStream os = connection.getOutputStream()){
            byte[] input = jsonPayload.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int code = connection.getResponseCode();

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        return response.toString();
    }
}
