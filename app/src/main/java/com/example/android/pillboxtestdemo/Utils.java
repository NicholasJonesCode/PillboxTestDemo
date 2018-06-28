package com.example.android.pillboxtestdemo;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Utils {

    public final static String PARAM_RXCUI = "rxcui";
    public final static String BASE_URI = "https://rxnav.nlm.nih.gov/REST/interaction/interaction.json";

    public static String getDrugInfoResult(String rxcuiNumber) throws IOException {

        Uri builtUri = Uri.parse(BASE_URI).
                buildUpon().
                appendQueryParameter(PARAM_RXCUI, rxcuiNumber).
                build();
        URL finalUrl = new URL(builtUri.toString());

        HttpsURLConnection connection = (HttpsURLConnection) finalUrl.openConnection();
        // optional default is GET
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + finalUrl.toString());
        System.out.println("Response Code : " + responseCode);

        BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuffer response = new StringBuffer();

        while ((inputLine = inputBufferedReader.readLine()) != null) {
            response.append(inputLine);
        }

        inputBufferedReader.close();
        connection.disconnect();

        return response.toString();
    }

}
