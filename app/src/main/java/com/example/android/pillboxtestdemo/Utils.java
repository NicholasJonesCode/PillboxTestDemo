package com.example.android.pillboxtestdemo;

import android.net.Uri;

import org.apache.commons.text.WordUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Utils {

    public static String getJsonResponseFromUrl(URL url) throws IOException {

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        // optional default is GET
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url.toString());
        System.out.println("Response Code : " + responseCode);

        BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;

        StringBuffer response = new StringBuffer();

        while ((inputLine = inputBufferedReader.readLine()) != null) {
            response.append(inputLine);
        }

        inputBufferedReader.close();
        connection.disconnect();

        return response.toString(); //pure json string
    }

    public static String getDrugInfoResultByRxcui(String number) throws IOException {

        final String PARAM_RXCUI = "rxcui";
        final String PARAM_SOURCES = "sources";
        final String BASE_URI = "https://rxnav.nlm.nih.gov/REST/interaction/interaction.json";

        Uri builtUri = Uri.parse(BASE_URI).
                buildUpon().
                appendQueryParameter(PARAM_RXCUI, number).
                appendQueryParameter(PARAM_SOURCES, "DrugBank"). //can be something else later
                build();
        URL finalUrl = new URL(builtUri.toString());

        return getJsonResponseFromUrl(finalUrl); //unsorted result
    }

    public static String getRxcuiByKeyword(String keyword) throws IOException, JSONException {

        final String BASE_URI = "https://rxnav.nlm.nih.gov/REST/rxcui.json";
        final String PARAM_NAME = "name";

        Uri builtUri = Uri.parse(BASE_URI).
                buildUpon().
                appendQueryParameter(PARAM_NAME, keyword).
                build();
        URL finalUrl = new URL(builtUri.toString());

        JSONObject jsonResponse = new JSONObject(getJsonResponseFromUrl(finalUrl));

        return jsonResponse.getJSONObject("idGroup").getJSONArray("rxnormId").getString(0);
    }

    //obviously subjected to whatever we want to display
    public static String sortJsonData(String jsonString) throws JSONException {

        //sorts the data found at this address:
        //https://rxnav.nlm.nih.gov/REST/interaction/interaction.json

        //this is the array of interactions (usually have like 50 or more elements):
        //jsonResponse.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0).getJSONArray("interactionPair")

        StringBuilder finalSortedData = new StringBuilder();
        JSONObject jsonResponse = new JSONObject(jsonString);

        String rxcuiNumber = jsonResponse.getJSONObject("userInput").getString("rxcui");
        String sourceOfInfo = jsonResponse.getJSONArray("interactionTypeGroup").getJSONObject(0).getString("sourceName");
        String sourceDisclaimer = jsonResponse.getJSONArray("interactionTypeGroup").getJSONObject(0).getString("sourceDisclaimer");
        String drugName = jsonResponse.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0).getJSONObject("minConceptItem").getString("name");
        String drugBankInteractionsUrl = jsonResponse.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0).
                getJSONArray("interactionPair").getJSONObject(0).getJSONArray("interactionConcept").getJSONObject(0).getJSONObject("sourceConceptItem").getString("url");

        String interaction0_description = jsonResponse.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0)
                .getJSONArray("interactionPair").getJSONObject(0).getString("description");
        String interaction1_description = jsonResponse.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0)
                .getJSONArray("interactionPair").getJSONObject(1).getString("description");
        String interaction2_description = jsonResponse.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0)
                .getJSONArray("interactionPair").getJSONObject(2).getString("description");

        finalSortedData.append("Data for the drug you searched: \n");
        finalSortedData.append("\n" + "RxCUI you inputted: " + rxcuiNumber);
        finalSortedData.append("\n" + "Drug Name: " + WordUtils.capitalize(drugName));
        finalSortedData.append("\n" + "Source: " + sourceOfInfo);
        finalSortedData.append("\n\n" + "Source Disclaimer: " + sourceDisclaimer);

        finalSortedData.append("\n\n\n  Interactions with \"" + drugName + "\": ");
        finalSortedData.append("\n\n --" + interaction0_description);
        finalSortedData.append("\n\n --" + interaction1_description);
        finalSortedData.append("\n\n --" + interaction2_description);

        finalSortedData.append("\n\n\nURL: " + drugBankInteractionsUrl);

        return finalSortedData.toString();
    }

}
