package softtrack.product.alarm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UrlFetcher {

    public static String getText(String url, byte[] newAvatar, String contactId, String requestMethod) throws Exception {
        URL website = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) website.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty( "charset", "utf-8");
        connection.setDoOutput(true);

        StringBuilder postData = new StringBuilder();
        postData.append("?myFile=");
        postData.append(newAvatar);
        postData.append("&contactid=" + contactId);
        postData.append("&path=abc");
        String urlParameters = postData.toString();
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(urlParameters);
        writer.flush();


        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }

}
