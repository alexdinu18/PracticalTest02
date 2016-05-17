package ro.pub.cs.systems.pdsd.practicaltest02.network;

import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ro.pub.cs.systems.pdsd.practicaltest02.general.Constants;
import ro.pub.cs.systems.pdsd.practicaltest02.general.Utilities;
//import ro.pub.cs.systems.pdsd.practicaltest02.model.WeatherForecastInformation;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket != null) {
            try {
                BufferedReader bufferedReader = Utilities.getReader(socket);
                PrintWriter printWriter = Utilities.getWriter(socket);
                if (bufferedReader != null && printWriter != null) {
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)!");
                    String word = bufferedReader.readLine();
//                    String informationType = bufferedReader.readLine();
//                    HashMap<String, WeatherForecastInformation> data = serverThread.getData();
//                    WeatherForecastInformation weatherForecastInformation = null;
                    String wordDefinition = null;
                    if (word != null && !word.isEmpty()) {
//                        if (data.containsKey(city)) {
//                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
//                            weatherForecastInformation = data.get(city);
//                        } else {
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                            HttpClient httpClient = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] " + Constants.WEB_SERVICE_ADDRESS);
                            List<NameValuePair> params = new ArrayList<NameValuePair>();
                            params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, word));
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] " + Constants.QUERY_ATTRIBUTE);
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] " + word);
                            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                            httpPost.setEntity(urlEncodedFormEntity);
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            String pageSourceCode = httpClient.execute(httpPost, responseHandler);
                            Log.i(Constants.TAG, "[COMMUNICATION THREAD] script data |||" + pageSourceCode);
                            serverThread.setData(pageSourceCode);
                            if (pageSourceCode != null) {
                                Document document = Jsoup.parse(pageSourceCode);
                                Element element = document.child(0);
                                Elements wordDefs = element.getElementsByTag(Constants.SCRIPT_TAG);
                                int i = 0;
                                for (Element wordDef : wordDefs) {
//                                Element wordDef = wordDefs.; // luam al doilea tag
                                    String scriptData = wordDef.data();
                                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] script data |||" + scriptData);
                                    i++;
                                    if (i == 2) {//scriptData.contains(Constants.SEARCH_KEY)) {
//                                        int position = scriptData.indexOf(Constants.SEARCH_KEY) + Constants.SEARCH_KEY.length();
//                                        scriptData = scriptData.substring(position);
//
//                                        JSONObject content = new JSONObject(scriptData);
//
//                                        JSONObject currentObservation = content.getJSONObject(Constants.CURRENT_OBSERVATION);
//                                        String temperature = currentObservation.getString(Constants.TEMPERATURE);
//                                        String windSpeed = currentObservation.getString(Constants.WIND_SPEED);
//                                        String condition = currentObservation.getString(Constants.CONDITION);
//                                        String pressure = currentObservation.getString(Constants.PRESSURE);
//                                        String humidity = currentObservation.getString(Constants.HUMIDITY);
//
//                                        weatherForecastInformation = new WeatherForecastInformation(
//                                                temperature,
//                                                windSpeed,
//                                                condition,
//                                                pressure,
//                                                humidity);
//
//                                        serverThread.setData(city, weatherForecastInformation);
//                                        serverThread.setData(scriptData);
                                        break;
                                    }
                                }
                            } else {
                                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                            }
                            
                            String result = pageSourceCode;
                            printWriter.println(result);
                          printWriter.flush();
//                        }

//                        if (weatherForecastInformation != null) {
//                            String result = null;
//                            if (Constants.ALL.equals(informationType)) {
//                                result = weatherForecastInformation.toString();
//                            } else if (Constants.TEMPERATURE.equals(informationType)) {
//                                result = weatherForecastInformation.getTemperature();
//                            } else if (Constants.WIND_SPEED.equals(informationType)) {
//                                result = weatherForecastInformation.getWindSpeed();
//                            } else if (Constants.CONDITION.equals(informationType)) {
//                                result = weatherForecastInformation.getCondition();
//                            } else if (Constants.HUMIDITY.equals(informationType)) {
//                                result = weatherForecastInformation.getHumidity();
//                            } else if (Constants.PRESSURE.equals(informationType)) {
//                                result = weatherForecastInformation.getPressure();
//                            } else {
//                                result = "Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
//                            }
//                            printWriter.println(result);
//                            printWriter.flush();
//                        } else {
//                            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast information is null!");
//                        }

                    } else {
                        Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!");
                    }
                } else {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] BufferedReader / PrintWriter are null!");
                }
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
        } else {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
        }
    }

}