package ro.pub.cs.systems.eim.practicaltest02.network;

import android.util.Log;

import org.json.JSONArray;
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

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.practicaltest02.general.Constants;
import ro.pub.cs.systems.eim.practicaltest02.general.Utilities;
import ro.pub.cs.systems.eim.practicaltest02.model.BitcoinInformation;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (valute)");
            String valute = bufferedReader.readLine();
            if (valute == null || valute.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }
            HashMap<String, BitcoinInformation> data = serverThread.getData();
            BitcoinInformation bitcoinInformation = null;
            if (data.containsKey(valute)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                bitcoinInformation = data.get(valute);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";
                if(false) {
                    HttpPost httpPost = new HttpPost(Constants.WEB_SERVICE_ADDRESS);
                    List<NameValuePair> params = new ArrayList<>();
                    UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                    httpPost.setEntity(urlEncodedFormEntity);
                    ResponseHandler<String> responseHandler = new BasicResponseHandler();

                    pageSourceCode = httpClient.execute(httpPost, responseHandler);
                } else {
                    HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                    HttpResponse httpGetResponse = httpClient.execute(httpGet);
                    HttpEntity httpGetEntity = httpGetResponse.getEntity();
                    if (httpGetEntity != null) {
                        pageSourceCode = EntityUtils.toString(httpGetEntity);
                    }
                }

                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else
                    Log.i(Constants.TAG, pageSourceCode );

                // Updated for openweather API
                if (false) {
                    Document document = Jsoup.parse(pageSourceCode);
                    Element element = document.child(0);

                } else {
                    JSONObject content = new JSONObject(pageSourceCode);

                    JSONArray updatedArray = content.getJSONArray(Constants.UPDATED);
                    JSONObject updated;
                    String condition = "";
                    for (int i = 0; i < updatedArray.length(); i++) {
                        updated = updatedArray.getJSONObject(i);
//                        condition += updated.getString(Constants.MAIN) + " : " + updated.getString(Constants.DESCRIPTION);

                        if (i < updatedArray.length() - 1) {
                            condition += ":";
                        }
                    }

                    JSONObject time = content.getJSONObject(Constants.TIME);
                    String updatedTag  = time.getString(Constants.UPDATED);
                    String rateTag = time.getString(Constants.RATE);

//                    bitcoinInformation = new BitcoinInformation(
//                            , valute, rate
//                    );
                    serverThread.setData(valute, bitcoinInformation);
                }
            }
            if (bitcoinInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Bitcoin Information is null!");
                return;
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } catch (JSONException jsonException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + jsonException.getMessage());
            if (Constants.DEBUG) {
                jsonException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}
