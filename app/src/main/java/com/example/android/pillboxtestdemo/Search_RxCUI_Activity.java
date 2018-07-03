package com.example.android.pillboxtestdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;

public class Search_RxCUI_Activity extends AppCompatActivity {

    //change this to keyword
    private EditText keywordInput;
    private TextView statusMessage;
    private TextView drugInfoResult;
    private TextView searchError;
    private ProgressBar searchQueryLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__rxcui);

        keywordInput = findViewById(R.id.keyword_input);
        statusMessage = findViewById(R.id.status_message);
        drugInfoResult = findViewById(R.id.drug_info_result);
        searchError = findViewById(R.id.search_error);
        searchQueryLoadingIndicator = findViewById(R.id.rxcui_query_loading_indicator);
    }

    public void executeRxcuiQuery() {

        new RxcuiQuery().execute(keywordInput.getText().toString());
    }

    public void onMakeSearchVisibilities() {

        //status update
        statusMessage.setText("Status: Making search, please wait...");

        //clear the past results
        drugInfoResult.setText("");

        //make sure error isnt showing, and show loading icon
        searchError.setVisibility(View.INVISIBLE);
        searchQueryLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public void afterSearchVisibilities() {

        //status update
        statusMessage.setText("Status: Finished!");
        searchQueryLoadingIndicator.setVisibility(View.INVISIBLE);
    }

    public void showSearchError(String errorMessage) {
        searchError.setText(errorMessage);
        searchError.setVisibility(View.VISIBLE);
    }



    public class RxcuiQuery extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            onMakeSearchVisibilities();
        }

        @Override
        protected String doInBackground(String... strings) {
            String keyword = strings[0];

            String result = null;
            try {

                String rxcui = Utils.getRxcuiByKeyword(keyword.trim()); //remove whitespace
                result = Utils.getDrugInfoResultByRxcui(rxcui);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            afterSearchVisibilities();

            if (result != null && !result.equals("") && keywordInput != null) {

                try {
                    drugInfoResult.setText(Utils.sortJsonData(result));
                } catch (JSONException e) {
                    e.printStackTrace();
                    showSearchError("Error in sorting data: \n\n" + e.getMessage()); //placeholder message for now
                }
            } else {
                showSearchError(getString(R.string.search_error));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_rxcui, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        int idOfItemThatWasClicked = menuItem.getItemId();

        if (idOfItemThatWasClicked == R.id.action_search) {
            executeRxcuiQuery();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
