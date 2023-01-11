package com.twp.unitconvertor;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.twp.unitconvertor.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    String base_currency = null;
    String to_currency = null;
    JSONObject rates = null;
    double curr_rate = Double.parseDouble(String.valueOf(0));
    Boolean feteched = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //CALL ON APP OPEN


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        List<String> currency = new ArrayList<>();
        // BASE CURRENCY DROP DOWN
        Spinner bsc = findViewById(R.id.basecurrency);
        // TO CURRENCY DROP DOWN
        Spinner toc = findViewById(R.id.tocurrency);
        //PUT CURRENCY AFTER CONVERTION
        EditText result = findViewById(R.id.result);
        //GET CURRENCY AMOUNT TO CONVERT
        EditText etc = findViewById(R.id.etc);
        call_rates(etc, result);
        // CONVERT BUTTON

        Button convertb = findViewById(R.id.convert);

        convertb.setOnClickListener(view -> {

            setSupportActionBar(binding.toolbar);
            base_currency = bsc.getSelectedItem().toString();
            to_currency = toc.getSelectedItem().toString();
            call_rates(etc, result);


        });

        currency.add("INR");
        currency.add("EUR");
        currency.add("USD");
        currency.add("JPY");


        ArrayAdapter currAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, currency);

        bsc.setAdapter(currAdapter);
        toc.setAdapter(currAdapter);


    }


    public void call_rates(EditText etc, EditText result) {
        feteched = false;
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        Context ctx = getApplicationContext();
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url;
        if (to_currency == null || base_currency == null) {
            to_currency = "USD";
            base_currency = "INR";
            url = "https://api.freecurrencyapi.com/v1/latest?apikey=YOUR_API_KEY&currencies=INR&base_currency=USD";
            Toast.makeText(ctx, "Please Select Both Currencies", Toast.LENGTH_LONG);
        } else {
            url = "https://api.freecurrencyapi.com/v1/latest?apikey=YOUR-API-KEY&currencies=" + to_currency + "&base_currency=" + base_currency + "";

        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // Display the first 500 characters of the response string.
                Log.d(TAG, "onResponse: " + response.toString());
                try {
                    rates = new JSONObject(response.toString());
                    JSONObject data = rates.getJSONObject("data");
                    double cr = data.getDouble(to_currency);
                    curr_rate = cr;
                    progress.dismiss();
                    //SET DATA TO EDIT TEXT
                    double total = Double.parseDouble(etc.getText().toString()) * curr_rate;
                    result.setText(String.valueOf(total));
                } catch (JSONException e) {
                    e.printStackTrace();
                    progress.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onResponse: " + error.toString());
            }
        });


        queue.add(stringRequest);
    }


}