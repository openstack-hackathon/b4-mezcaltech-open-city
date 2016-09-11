package com.misael.example.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DetailActivity extends AppCompatActivity implements LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    private final static int REQUEST_TAKE_PICTURE = 0;
    private final static int REQUEST_GALLERY_IMAGE = 2;
    private static final int REQUEST_LOCATION = 1;

    private static final long ONE_MIN = 1000 * 60;
    private static final long TWO_MIN = ONE_MIN * 2;
    private static final long FIVE_MIN = ONE_MIN * 5;
    private static final long POLLING_FREQ = 1000 * 30;
    private static final long FASTEST_UPDATE_FREQ = 1000 * 5;
    private static final float MIN_ACCURACY = 25.0f;
    private static final float MIN_LAST_READ_ACCURACY = 500.0f;

    private Button btnSend;
    private EditText editDetail;
    private ImageButton btnLocation;
    private ImageButton btnImage;
    private ImageButton btnCamera;

    private RadioGroup radioGroup;

    GoogleApiClient googleApiClient;

    LocationManager locationManager;
    LocationRequest locationRequest;
    private Location location;

    WebSocketClient webSocketClient;

    private String photoPath;
    double lat;
    double longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        setupUI();

        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //getLocation();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(POLLING_FREQ);
        locationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

        setupGoogleClient();

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickPicture();
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String description = editDetail.getText().toString().trim();
                    if(!description.equals("") && !getAction().equals("")) {
                        connetWebSocket(objectToJSON(description));
                        Log.i(DetailActivity.class.getSimpleName(), objectToJSON(description));
                    } else {
                        Toast.makeText(getApplicationContext(), "Llena los campos", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    Log.e(DetailActivity.class.getSimpleName(), e.getMessage());
                }
            }
        });
    }

    private void setupGoogleClient() {
        if(googleApiClient == null){
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(googleApiClient != null){
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void pickPicture() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(pickIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = getFile();
            startActivityForResult(pickIntent, REQUEST_GALLERY_IMAGE);
        }
    }

    private void takePicture() {

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePhotoIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = getFile();
            } catch (Exception e){
                Log.e(DetailActivity.class.getSimpleName(), e.getMessage());
            }

            if(photoFile != null){
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePhotoIntent, REQUEST_TAKE_PICTURE);
            }
        }
    }

    private File getFile() {
        File photoFile = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);
            photoPath = photoFile.getAbsolutePath();
        } catch (IOException ex) {
            Log.e(DetailActivity.class.getSimpleName(), ex.getMessage());
        }
        return photoFile;
    }

    private void getLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);
        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if(location != null) {
                lat = location.getLatitude();
                longi = location.getLongitude();
            } else {
                Toast.makeText(getApplicationContext(), "Prueba Denuevo", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }

    private String objectToJSON(String description) throws JSONException {
        JSONObject object = new JSONObject();
        object.put("lat", lat);
        object.put("long", longi);
        JSONObject emergency = new JSONObject();
        emergency.put("type", getAction());
        emergency.put("description", description);
        object.put("emergency", emergency);
        return  object.toString();
    }

    private void setupUI() {
        btnSend = (Button) findViewById(R.id.btnSend);
        editDetail = (EditText) findViewById(R.id.editDetail);
        btnLocation = (ImageButton) findViewById(R.id.btnLocation);
        btnImage = (ImageButton) findViewById(R.id.btnImage);
        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        radioGroup = (RadioGroup) findViewById(R.id.containerRadioButtons);
    }

    private void connetWebSocket(final String message){
        URI uri;
        try{
            uri = new URI("ws://172.16.11.227:1337");
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
                try {
                    Log.i("Websocket", "Error " + ex.getMessage());
                    Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        webSocketClient.connect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_TAKE_PICTURE) {
            boolean isCamera = (data == null ||
                    data.getData() == null);

            if (isCamera) {
                addPicToGallery();
                Intent intent = new Intent(DetailActivity.this, ImageActivity.class);
                intent.putExtra("image", photoPath);
                startActivity(intent);
            }
        } else if(resultCode == RESULT_OK && requestCode == REQUEST_GALLERY_IMAGE){
            boolean isCamera = (data == null ||
                    data.getData() == null);

            if(isCamera){
                photoPath = getRealPathFromURI(data.getData());
                Intent intent = new Intent(DetailActivity.this, ImageActivity.class);
                intent.putExtra("image", photoPath);
                startActivity(intent);
            }
        }
    }

    private void addPicToGallery() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result = null;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            if (contentURI.toString().contains("mediaKey")){
                cursor.close();

                try {
                    File file = File.createTempFile("tempImg", ".jpg", getCacheDir());
                    InputStream input = getContentResolver().openInputStream(contentURI);
                    OutputStream output = new FileOutputStream(file);

                    try {
                        byte[] buffer = new byte[4 * 1024];
                        int read;

                        while ((read = input.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }
                        output.flush();
                        result = file.getAbsolutePath();
                    } finally {
                        output.close();
                        input.close();
                    }

                } catch (Exception e) {
                }
            } else {
                cursor.moveToFirst();
                int dataColumn = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(dataColumn);
                cursor.close();
            }

        }
        return result;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /*location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        lat = location.getLatitude();
        longi = location.getLongitude();*/
        location = bestLastKnownLocation(MIN_LAST_READ_ACCURACY, FIVE_MIN);
        lat = location.getLatitude();
        longi = location.getLongitude();

        if (null == location
                || location.getAccuracy() > MIN_LAST_READ_ACCURACY
                || location.getTime() < System.currentTimeMillis() - TWO_MIN) {

            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

            // Schedule a runnable to unregister location listeners
            Executors.newScheduledThreadPool(1).schedule(new Runnable() {

                @Override
                public void run() {
                    LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, DetailActivity.this);
                }

            }, ONE_MIN, TimeUnit.MILLISECONDS);
        }
    }

    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(DetailActivity.class.getSimpleName(), connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        // Determine whether new location is better than current best
        // estimate
        if (null == this.location || location.getAccuracy() < this.location.getAccuracy()) {
            this.location = location;
            lat = this.location.getLatitude();
            longi = this.location.getLongitude();

            if (this.location.getAccuracy() < MIN_ACCURACY) {
                LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            }
        }
    }

    public String getAction(){

        int radioBtnSelected = radioGroup.getCheckedRadioButtonId();
        switch (radioBtnSelected){
            case R.id.radioAsalto:
                return "Asalto";
            case R.id.radioRobo:
                return "Robo";
            case R.id.radioAccidente:
                return "Accidente";
            default:
                return "";
        }
    }
}
