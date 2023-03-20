package com.swipecare.payments;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class CallRestApiForMoneyTransfer extends AsyncTask<String,String,String> {


    HttpURLConnection urlConnection;
    @Override
    protected String doInBackground(String... strings) {

        StringBuilder result = new StringBuilder();

        try {
            Log.e("url", strings[0]);
            URL url = new URL(strings[0]);
            String biometricData = strings[1];
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Accept", "application/xml");
            urlConnection.setRequestProperty("Content-Type","application/xml");
            urlConnection.setDoOutput(true);

            // Write XML
            OutputStream outputStream = urlConnection.getOutputStream();
            byte[] b = biometricData.getBytes(StandardCharsets.UTF_8);
            outputStream.write(b);
            outputStream.flush();
            outputStream.close();

            // Read XML
            InputStream inputStream = urlConnection.getInputStream();
            byte[] res = new byte[2048];
            int i = 0;
            StringBuilder response = new StringBuilder();
            while ((i = inputStream.read(res)) != -1) {
                response.append(new String(res, 0, i));
            }
            inputStream.close();
            System.out.println("Response= " + response);

//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                result.append(line);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return result.toString();
    }
}
