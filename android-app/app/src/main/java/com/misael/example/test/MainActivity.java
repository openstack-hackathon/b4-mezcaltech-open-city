package com.misael.example.test;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.misael.example.test.api.ApiClient;
import com.misael.example.test.api.BeaconService;
import com.misael.example.test.models.Identificador;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.Collection;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    ImageButton btnAlert;
    EditText txtMessage;
    WebSocketClient webSocketClient;
    Thread runOnUiThread;
    private static final double MAX_DIST = 0.5;
    BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAlert = (ImageButton) findViewById(R.id.btnAlert);
        txtMessage = (EditText) findViewById(R.id.txtMessage);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = txtMessage.getText().toString().trim();
                //connetWebSocket(message);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    private void connetWebSocket(final String message){
        URI uri;
        try{
            uri = new URI("ws://10.43.28.114:1337");
        } catch (Exception e){
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i("Websocket", "Opened");
                webSocketClient.send(message);
            }

            @Override
            public void onMessage(final String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i("Websocket", "Closed " + reason);
                Toast.makeText(getApplicationContext(), "Closed: " + reason, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception ex) {
                Log.i("Websocket", "Error " + ex.getMessage());
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        webSocketClient.connect();
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                if(collection.size() > 0){
                    int number = collection.size();
                    Log.d(MainActivity.class.getSimpleName(), "Se encontraron: " + number + "Beacons");
                    for(Iterator i = collection.iterator(); i.hasNext();){
                        Beacon beacon = (Beacon) i.next();
                        Log.d(MainActivity.class.getSimpleName(), beacon.toString());

                        Identifier identifier = beacon.getId3();
                        Log.d(MainActivity.class.getSimpleName(), "Un beacon a:  " + beacon.getDistance() + " metros " + beacon.getBluetoothAddress());
                        if(beacon.getDistance() < MAX_DIST){
                            //Toast.makeText(getApplicationContext(), identifier.toString(), Toast.LENGTH_LONG).show();
                            try {
                                Log.d(MainActivity.class.getSimpleName(), "DENTROooo");
                                getBeaconInfo(identifier.toString());
                                break;
                            } catch (Exception e){
                                Log.e(MainActivity.class.getSimpleName(), e.getMessage());
                            }
                        } else {
                            //Log.e(MainActivity.class.getSimpleName(), "ERROOOOOR");
                        }
                    }
                }
            }
        });

        try{
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (Exception e){
            Log.e(MainActivity.class.getSimpleName(), e.getMessage());
        }
    }

    public void getBeaconInfo(String id) throws JSONException {
        Log.d("lalallala: ", id);
        beaconManager.unbind(this);

        Identificador identi = new Identificador();
        identi.setId(id);
        Call<com.misael.example.test.models.Beacon> call = new ApiClient().getBeaconService().getBeaconInfo(identi);

        Callback<com.misael.example.test.models.Beacon> callback = new Callback<com.misael.example.test.models.Beacon>() {
            @Override
            public void onResponse(Call<com.misael.example.test.models.Beacon> call, Response<com.misael.example.test.models.Beacon> response) {
                if(response.isSuccessful()){
                    com.misael.example.test.models.Beacon beacon = response.body();
                    Toast.makeText(MainActivity.this, beacon.getData().getArea(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<com.misael.example.test.models.Beacon> call, Throwable t) {
                if(t != null){
                    Log.e(MainActivity.class.getSimpleName(), t.getMessage());
                }
            }
        };

        call.enqueue(callback);
    }
}
