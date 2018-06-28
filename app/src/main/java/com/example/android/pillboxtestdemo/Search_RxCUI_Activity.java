package com.example.android.pillboxtestdemo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class Search_RxCUI_Activity extends AppCompatActivity {

    private EditText rxcuiInput;
    private TextView statusMessage;
    private TextView drugInfoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__rxcui);

        rxcuiInput = findViewById(R.id.rxcui_input);
        statusMessage = findViewById(R.id.status_message);
        drugInfoResult = findViewById(R.id.drug_info_result);
    }

    public void executeRxcuiQuery() {

        new RxcuiQuery().execute(rxcuiInput.getText().toString());
    }


    public class RxcuiQuery extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            //status update
            statusMessage.setText("Status: Making search, please wait...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String rxcui = strings[0];

            String result = null;
            try {
                result = Utils.getDrugInfoResult(rxcui);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.equals("")) {
                drugInfoResult.setText(result);
                //status update
                statusMessage.setText("Status: Finished!");
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
