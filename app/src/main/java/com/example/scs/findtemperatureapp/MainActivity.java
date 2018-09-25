package com.example.scs.findtemperatureapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    TextView tempText, highLowTempText;
    EditText cityEditText;
    Button temperatureButton;

    public void findTemperature(View view){

        cityEditText = (EditText) findViewById(R.id.city_name);

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityEditText.getWindowToken(),0);

        String city = null;
        try {
            city = URLEncoder.encode(cityEditText.getText().toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Temperature not found!", Toast.LENGTH_SHORT).show();
        }
        cityEditText.setText("");

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute("http://api.openweathermap.org/data/2.5/weather?q=" + city +"&APPID=711bcfd6af496c112bded76020b68d5a");
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream input = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(input);

                int data = reader.read();

                while(data != -1){
                    char ch = (char) data;
                    result += ch;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                Log.i("Error","Extracting API");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject mainObject = new JSONObject(result);
                JSONObject temperatureObject = new JSONObject(mainObject.getString("main"));

                String currentTempString = "";
                String maxMinTempString;
                currentTempString = temperatureObject.getString("temp");

                int temp = temperatureObject.getInt("temp") - 273;
                int minTemp = temperatureObject.getInt("temp_max") - 273;
                int maxTemp = temperatureObject.getInt("temp_min") - 273;

                currentTempString = temp + "'";
                maxMinTempString = "High " + maxTemp + "' / Low " + minTemp + "'";

                if(currentTempString != ""){
                    tempText = (TextView) findViewById(R.id.temp);
                    highLowTempText = (TextView) findViewById(R.id.high_low_temp);

                    tempText.setText(currentTempString);
                    highLowTempText.setText(maxMinTempString);
                }
                else
                    Toast.makeText(MainActivity.this, "Temperature not found!", Toast.LENGTH_SHORT).show();
            }
            catch(Exception e){
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Temperature not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}