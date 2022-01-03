package com.example.helperapp;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.apcs2.midtermmoblie.LandMark;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker mMarker;
    private RequestQueue mQueue;
    private JsonObjectRequest mJsonObjectRequest;
    private ArrayList<Marker> mMarkers;
    ImageButton logout;
    private ArrayList<LandMark> landmarks;
    LinearLayout containerLayout;
    RelativeLayout requestForm;
    LinearLayout detailView;
    TextView detailTitle;
    TextView detailDescription;
    TextView detailPhone;
    TextView detailLocation;
    EditText eTitle;
    EditText eDescription;
    EditText eLocation;
    EditText ePhone;
    Spinner sEmergency;
    CheckBox cbCurrentLocation;
    TextView tEmergency;
    LatLng currentLatLng;
    TextView detailEmergency;
    ArrayList<EditText> editFormText;
    DatabaseReference refHigh;
    DatabaseReference refModerate;
    DatabaseReference refLow;
    SearchView searchRequest;
    FirebaseAuth mFirebaseAuth;
    String userId;
    String userName;
    TextView detailDateTime;
    DatabaseReference drName;
    String nameOfUser;
    String detailCurLatLng;
    String TAG = "INFO";
    int idNotify;
    int newNearRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initComponent();
        //showNumberNewUpdateReq();
    }


    private void gotoLocation(LatLng tmpLatLng) {
        CameraPosition newCameraPosition = new CameraPosition.Builder()
                .target(tmpLatLng) // Sets the center of the map to Mountain View
                .zoom(15)                      // Sets the zoom
                .bearing(90)                   // Sets the orientation of the camera to east
                .tilt(30)                      // Sets the tilt of the camera to 30 degrees
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition));
    }

    LatLng getDeviceLocation() {

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        // Getting Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return new LatLng(10.763166, 106.682225);
        }
        mMap.setMyLocationEnabled(true);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            // Getting latitude of the current location
            double latitude = location.getLatitude();
            // Getting longitude of the current location
            double longitude = location.getLongitude();
            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);
            Log.d(TAG, "Line 176: current latitute " + String.valueOf(latitude));
            return latLng;
        }
        return new LatLng(10.763166, 106.682225);
    }

    private void initComponent() {
        landmarks = new ArrayList<>();
        mMarkers = new ArrayList<>();
        detailView = findViewById(R.id.detail_view);
        detailTitle = findViewById(R.id.detail_title);
        detailDescription = findViewById(R.id.detail_description);
        detailPhone = findViewById(R.id.detail_phone);
        detailLocation = findViewById(R.id.detail_location);
        containerLayout = findViewById(R.id.container);
        requestForm = findViewById(R.id.request_from);
        eTitle = findViewById(R.id.eTitle);
        eDescription = findViewById(R.id.eDescription);
        eLocation = findViewById(R.id.eLocation);
        ePhone = findViewById(R.id.ePhone);

        sEmergency = findViewById(R.id.s_emergency);
        tEmergency = findViewById(R.id.t_emergency);
        cbCurrentLocation = findViewById(R.id.current_location);
        detailEmergency = findViewById(R.id.detail_emergency);
        ArrayAdapter<CharSequence> sEmergencyAdapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.emergency_level, android.R.layout.simple_spinner_item);
        sEmergencyAdapter.setDropDownViewResource(R.layout.emergency_level_spinner);
        sEmergency.setAdapter(sEmergencyAdapter);
        sEmergency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String) adapterView.getItemAtPosition(i);
                tEmergency.setText("Emergency Level: " + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        logout = (ImageButton) findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        refHigh = FirebaseDatabase.getInstance().getReference("3");
        refModerate = FirebaseDatabase.getInstance().getReference("2");
        refLow = FirebaseDatabase.getInstance().getReference("1");

        fireBaseAddEventListener(refHigh);
        fireBaseAddEventListener(refModerate);
        fireBaseAddEventListener(refLow);
        searchRequest = findViewById(R.id.sr);
        setSearchOnSearchListener();
        mFirebaseAuth = FirebaseAuth.getInstance();
        userId = mFirebaseAuth.getCurrentUser().getUid();
        detailDateTime = findViewById(R.id.detail_dateTime);
        nameOfUser = mFirebaseAuth.getCurrentUser().getEmail();
        Log.d(TAG + "username: ", nameOfUser);
        TextView textNameUser = findViewById(R.id.name_of_user);
        String nameOfUserArr[] = nameOfUser.split(getString(R.string.split_sign_email));
        textNameUser.setText("Welcome " + nameOfUserArr[0] + "!");
        idNotify = 0;
        newNearRequest = 0;
    }

    private void setSearchOnSearchListener() {
        searchRequest.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchLandMark(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public void searchLandMark(String _stringSearch) {//tri viet
        int max_danger = 0;
        boolean isFind = false;
        boolean isOk = false;
        LandMark result = null;
        Log.e(TAG, "landmark size: " + String.valueOf(landmarks.size()));
        Log.e(TAG, "querry: " + _stringSearch);
        if (landmarks.size() != 0) {
            for (int i = 0; i < landmarks.size(); i++) {
                isFind = landmarks.get(i).getName().contains(_stringSearch);
                int level = Integer.valueOf(landmarks.get(i).get_emergencyLevel());
                if (isFind == true && level > max_danger) {
                    isOk = true;
                    result = landmarks.get(i);
                    max_danger = level;

                }
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_string),
                    Toast.LENGTH_SHORT
            ).show();
        }
        if (isOk) {
            gotoLocation(result.getLatLng());
        } else {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.no_address),
                    Toast.LENGTH_SHORT
            ).show();
        }
    }


    private void fireBaseAddEventListener(DatabaseReference drName) {
        drName.addChildEventListener(new ChildEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                handleItemAdded(snapshot);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                handleItemRemoved(snapshot);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void handleItemRemoved(@NonNull DataSnapshot snapshot) {
        Double newLat = snapshot.child("latLng").child("latitude").getValue(Double.class);
        Double newLong = snapshot.child("latLng").child("longitude").getValue(Double.class);
        String name = snapshot.child("name").getValue(String.class);
        String location = String.valueOf(newLat) + "," + String.valueOf(newLong);
        deleteMarker(location, name);
    }

    public double calculateDistance(LatLng laLg1, LatLng laLg2) {
        double lat1 = laLg1.latitude;
        double lat2 = laLg2.latitude;
        double lon1 = laLg1.longitude;
        double lon2 = laLg2.longitude;
        double p = 0.017453292519943295;    // Math.PI / 180
        double a = 0.5 - Math.cos((lat2 - lat1) * p) / 2 +
                Math.cos(lat1 * p) * Math.cos(lat2 * p) *
                        (1 - Math.cos((lon2 - lon1) * p)) / 2;
        return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void handleItemAdded(DataSnapshot child) {
        Double newLatitude = child.child("latLng").child("latitude").getValue(Double.class);
        Double newLongitude = child.child("latLng").child("longitude").getValue(Double.class);
//        if (isMarkerExist(new LatLng(lat, lLong)))
//            return;
        String currentDateTime = child.child("_startDate").getValue(String.class);
        String curUserId = child.child("userId").getValue(String.class);
        String emLevel = child.child("_emergencyLevel").getValue(String.class);
        String locationId = child.child("_locationID").getValue(String.class);
        String phone = child.child("_phone").getValue(String.class);
        String description = child.child("description").getValue(String.class);
        String name = child.child("name").getValue(String.class);
        String address = child.child("address").getValue(String.class);
        makeNotification(newLatitude, newLongitude, description, name, Integer.valueOf(emLevel));
        LandMark landMark = new LandMark(name, description, phone, new LatLng(newLatitude, newLongitude), emLevel, new ArrayList<Polyline>(), curUserId, currentDateTime, address);
        landMark.set_locationID(locationId);
        drawMarker(landMark);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeNotification(Double lat, Double lLong, String description, String name, int emergencyLevel) {
        // check if new Request near 5km
        if (calculateDistance(currentLatLng, new LatLng(lat, lLong)) < 5) {
            showNotif(description, name, emergencyLevel);
            newNearRequest++;
        }
    }


    public int autoIncrementId(int id) {
        return ++id;
    }

    public void showNumberNewUpdateReq() {
        try {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(newNearRequest > 0)
                    Toast.makeText(getApplicationContext(), String.valueOf(newNearRequest) + " " + getString(R.string.number_new_req), Toast.LENGTH_SHORT).show();

                }
            }, 1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void showNotif(String description, String name, int emergencyLevel) {
        int priorityLevel = getPriorityLevelOnEmergencyLevel(emergencyLevel);
        int importantLevel = getImportantLevelOnEmergencyLevel(emergencyLevel);
        String channelId = getString(R.string.app_name);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.warning)
                .setContentTitle(name)
                .setContentText(description)
                .setPriority(priorityLevel);
        NotificationChannel channel = new NotificationChannel(channelId, description, importantLevel);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
        notificationManager.notify(autoIncrementId(idNotify), mBuilder.build());
    }

    public int getPriorityLevelOnEmergencyLevel(int emergencyLevel) {
        if (emergencyLevel == 3) {
            return NotificationCompat.PRIORITY_HIGH;
        }
        if (emergencyLevel == 2) {
            return NotificationCompat.PRIORITY_DEFAULT;
        }
        return NotificationCompat.PRIORITY_LOW;

    }

    public int getImportantLevelOnEmergencyLevel(int emergencyLevel) {
        if (emergencyLevel == 3) {
            return NotificationManager.IMPORTANCE_HIGH;
        }
        if (emergencyLevel == 2) {
            return NotificationManager.IMPORTANCE_DEFAULT;
        }
        return NotificationManager.IMPORTANCE_LOW;

    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkPermissionLocationAccess();
        currentLatLng = getDeviceLocation();
        gotoLocation(currentLatLng);
        setMarkerOnClickListener();
    }

    public boolean checkPermissionLocationAccess() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            return true;
        }
        return false;
    }

    public void setMarkerOnClickListener() {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                containerLayout = findViewById(R.id.container);
                detailView = findViewById(R.id.detail_view);
                detailView.setVisibility(View.VISIBLE);
                containerLayout.setGravity(Gravity.BOTTOM);
                detailTitle.setText(marker.getTitle());
                String spitSign = "~";
                detailCurLatLng = String.valueOf(marker.getPosition().latitude) + "," + String.valueOf(marker.getPosition().longitude);
                String[] splitStr = marker.getSnippet().split(spitSign);
                detailDescription.setText("Description: " + splitStr[0]);
                detailPhone.setText("Phone: " + splitStr[1]);
                detailEmergency.setText(splitStr[2]);
                detailDateTime.setText(splitStr[3]);
                CardView compleRequest = findViewById(R.id.cardView_complete);
                if (checkUserPermission(splitStr[4])) {
                    compleRequest.setVisibility(View.VISIBLE);
                } else
                    compleRequest.setVisibility(View.GONE);
                detailLocation.setText("Location: " + splitStr[5]);

                return true;
            }
        });
    }

    private boolean checkUserPermission(String curUid) {
        if (userId.equals(curUid))
            return true;
        return false;
    }


    /////////////Utils onclick
    public void startDial(View view) {
        String phoneNumber = (String) detailPhone.getText().subSequence(7, detailPhone.getText().length());
        call(phoneNumber);
    }

    public void call(String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + phoneNumber)));
    }

    public void sendSMS(String number) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null)));
    }

    public void startMessage(View view) {
        sendSMS((String) detailPhone.getText());
    }

    public void createForm(View view) {
        //  Toast.makeText(this, "Tong so landmark la: " + String.valueOf(landmarks.size()), Toast.LENGTH_SHORT).show();
        ImageButton button = findViewById(R.id.swithButton);
        button.setVisibility(View.GONE);
        searchRequest.setVisibility(View.GONE);
        containerLayout.setGravity(Gravity.CENTER);
        requestForm.setVisibility(View.VISIBLE);
        eTitle.setText("");
        eDescription.setText("");
        eLocation.setText("");
        ePhone.setText("");
        findViewById(R.id.footer).setVisibility(View.GONE);
    }

    public void close_detail_form(View view) {
        detailView.setVisibility(View.GONE);
    }

    public void completeRequest(View view) {

        String[] extractedStr = extractDetailForm();
        deleteMarker(extractedStr[0], extractedStr[1]);
        close_detail_form(new View(this));
    }

    private void deleteMarker(String location, String title) {
        int position = find(location, title);
        if (position != -1) {
            LandMark landMark = landmarks.get(position);
            removeOnDataBase(landMark);
            removeAllPolylineExceptAtPostion(-1);
            removeAMarker(position);
            //       Toast.makeText(this, "Xoa landmark thanh cong, Tong so landmark la: " + String.valueOf(landmarks.size()), Toast.LENGTH_SHORT).show();

            //-1 = clear all
        }
    }

    private void removeOnDataBase(LandMark landMark) {
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference().getRoot().child(landMark.get_emergencyLevel()).child(landMark.get_locationID());
        dref.removeValue();
    }

    private void removeAMarker(int position) {
        mMarkers.get(position).remove();
        mMarkers.remove(position);
        landmarks.remove(position);
    }

    public void directToCurrentPosition(View view) {
        removeAllPolylineExceptAtPostion(-1);

        //  LatLng tmpLng = new LatLng(curLat, curLong);
        String[] extractedStr = extractDetailForm();
        int position = find(extractedStr[0], extractedStr[1]);
        LandMark landmark = landmarks.get(position);
        String url = createDirectionUri(currentLatLng, landmark.getLatLng());
        requestDirection(url, position);
        // check if get position success
        if (currentLatLng.latitude != 0 && currentLatLng.longitude != 0) {
            gotoLocation(currentLatLng);
            close_detail_form(new View(this));
        }
    }

    public int find(String location, String title) {
        for (int i = 0; i < landmarks.size(); i++) {
            LandMark landmark = landmarks.get(i);
            String tmpLatLg = String.valueOf(landmark.getLatLng().latitude) + "," + String.valueOf(landmark.getLatLng().longitude);
            if (landmark.getName().equals(title) && location.equals(tmpLatLg)) {
                return i;
            }
        }
        return -1;
    }

    public String createDirectionUri(LatLng startPosition, LatLng desPositon) {
        String start = String.valueOf(startPosition.longitude) + ',' + String.valueOf(startPosition.latitude);
        String des = String.valueOf(desPositon.longitude) + ',' + String.valueOf(desPositon.latitude);
        return getString(R.string.MAPBOX_URL) + start + ';' + des + getString(R.string.ACCESS_TOKEN);
    }

    private String[] extractDetailForm() {
        String curTitle = (String) detailTitle.getText();
        return new String[]{detailCurLatLng, curTitle};
    }

    private void removeAllPolylineExceptAtPostion(int position) {
        for (int i = 0; i < landmarks.size(); i++) {
            if (position != i) {
                ArrayList<Polyline> tmpPolylines = landmarks.get(i).getPolyLines();

                for (int j = 0; j < tmpPolylines.size(); j++) {
                    tmpPolylines.get(j).remove();
                }

            }
        }
    }

    private void requestDirection(String url, final int postion) {
        mQueue = Volley.newRequestQueue(this);
        mJsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject route = response
                                    .getJSONArray(getString(R.string.route))
                                    .getJSONObject(0);
                            ArrayList<LatLng> listPointRoute = decodePoly(route
                                    .getString
                                            (getString
                                                    (R.string.geometry)));
                            drawPolyline(listPointRoute,
                                    getString(R.string.colorPolyLine),
                                    10, postion);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.v(TAG, getString(R.string.on_respone));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, getString(R.string.request_url_err));
            }
        });
        mQueue.add(mJsonObjectRequest);
    }

    public ArrayList<LatLng> decodePoly(String encoded) {
        ArrayList<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void drawPolyline(ArrayList<LatLng> listPointRoute, String color, int width, int position) {
        ArrayList<Polyline> tmpArr = new ArrayList<>();

        for (int i = 0; i < listPointRoute.size() - 1; i++) {
            LatLng src = listPointRoute.get(i),
                    des = listPointRoute.get(i + 1);
            Polyline singleLine = mMap.addPolyline(
                    new PolylineOptions().add(
                            new LatLng(src.latitude, src.longitude),
                            new LatLng(des.latitude, des.longitude)
                    )
                            .color(Color.parseColor(color))
                            .geodesic(true)
                            .width(width)
            );
            tmpArr.add(singleLine);
        }
        landmarks.get(position).setPolyLines(tmpArr);

    }

    public void close_form(View view) {
        TextView error = findViewById(R.id.error_from);
        error.setText("");
        clearForm();

    }

    private void clearForm() {
        containerLayout.setGravity(Gravity.BOTTOM);
        requestForm.setVisibility(View.GONE);
        eTitle.setText("");
        eDescription.setText("");
        eLocation.setText("");
        ePhone.setText("");
        findViewById(R.id.footer).setVisibility(View.VISIBLE);
        findViewById(R.id.swithButton).setVisibility(View.VISIBLE);
        searchRequest.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getCurrentDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveMarker(View view) {
        String title = String.valueOf(eTitle.getText());
        String description = String.valueOf(eDescription.getText());
        String phone = String.valueOf(ePhone.getText());
//        if (!isPhoneNumber(phone)) {
//            Toast.makeText(this, "This phone number doesn't valid", Toast.LENGTH_SHORT).show();
//            //   return;
//        }
        String level = (String) tEmergency.getText();
        String eLevel;
        if (!level.equals("")) {
            if (level.contains("High")) {
                eLevel = "3";
            } else if (level.contains("Moderate")) {
                eLevel = "2";
            } else {
                eLevel = "1";
            }
            try {
                Address curAddress = null;
                LatLng tmpLatLng = null;
                String addressStr;
                findViewById(R.id.footer).setVisibility(View.GONE);
                if (cbCurrentLocation.isChecked()) {
                    currentLatLng = getDeviceLocation();
                    tmpLatLng = currentLatLng;
                    addressStr = String.valueOf(tmpLatLng.latitude) + "N, " + String.valueOf(tmpLatLng.longitude) + "E";
                    //   detailLocation.setText("Location: " + String.valueOf(tmpLatLng.latitude) + "N, " + String.valueOf(tmpLatLng.longitude) + "E");

                } else {
                    curAddress = getLocationOfForm(String.valueOf(eLocation.getText()));
                    if (curAddress == null) {
                        Toast.makeText(this, "Address doesn't exist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    tmpLatLng = new LatLng(curAddress.getLatitude(), curAddress.getLongitude());
                    addressStr = curAddress.getAddressLine(0);

                }
                String currentDateTime = "Can't estimating because SDK version is lesser than 26...";
                try {
                    currentDateTime = getCurrentDateTime();

                } catch (DateTimeException e) {
                    e.printStackTrace();
                    Log.d(TAG, getString(R.string.cur_date_err));
                }
                LandMark temp_lm = new LandMark(title, description, phone, tmpLatLng, eLevel, new ArrayList<Polyline>(), userId, currentDateTime, addressStr);
                if (temp_lm.getName() != "" && temp_lm.getDescription() != "" && temp_lm.get_phone() != "") {

                    if (isMarkerExist(temp_lm.getLatLng())) {
                        Toast.makeText(this, getString(R.string.capturedLocation), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    pushLmToDB(temp_lm);
                    clearForm();
                    gotoLocation(temp_lm.getLatLng());


                }
            } catch (Exception e) {
                logout.setVisibility(View.VISIBLE);
                e.printStackTrace();
                throwErrorWarning();
            }
        }
    }

    private boolean isPhoneNumber(String phoneNum) {
        if (phoneNum.length() > 12 || phoneNum.length() < 10)
            return false;
        Log.d(TAG, "Line 797: phone: " + phoneNum.substring(1, 2));
        if (phoneNum.substring(1, 2).equals("0") || !phoneNum.startsWith("0")) {
            return false;
        }
        return true;
    }

    private boolean isMarkerExist(LatLng curLatLng) {
        for (int i = 0; i < landmarks.size(); i++) {
            LatLng laLng = landmarks.get(i).getLatLng();
            if (curLatLng.latitude == laLng.latitude && curLatLng.longitude == laLng.longitude) {
                return true;
            }
        }
        return false;
    }

    private void throwErrorWarning() {

    }

    private void drawMarker(LandMark landMark) {
        LatLng base = new LatLng(0, 0);
        String description = landMark.getDescription();
        String title = landMark.getName();
        String phone = landMark.get_phone();
        String level = landMark.get_emergencyLevel();
        String curDateTime = landMark.get_startDate();
        LatLng latLng = landMark.getLatLng();
        String uId = landMark.getUserId();
        String address = landMark.getAddress();
        BitmapDescriptor bitmapDescriptor = null;
        int color;
        if (landMark.get_emergencyLevel().equals("1")) {
            color = Color.BLACK;
        } else if (landMark.get_emergencyLevel().equals("2")) {
            color = Color.GREEN;
        } else {
            color = Color.RED;
        }

        bitmapDescriptor = vectorToBitmap(R.drawable.warning, color);
        if (!latLng.equals(base) && !description.equals("") && !title.equals("") && !phone.equals("")) {
            String spitSign = "~";
            String containerStr = description + spitSign + phone + spitSign + level + spitSign + curDateTime + spitSign + uId + spitSign + address;
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(bitmapDescriptor)
                    .title(title)
                    .snippet(containerStr));
            mMarkers.add(mMarker);
            landmarks.add(landMark);
        }
    }

    private BitmapDescriptor vectorToBitmap(@DrawableRes int id, @ColorInt int color) {
        Drawable vectorDrawable = ResourcesCompat.getDrawable(getResources(), id, null);
        assert vectorDrawable != null;
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        DrawableCompat.setTint(vectorDrawable, color);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void pushLmToDB(LandMark temp_lm) {
        String locationID;
        switch (temp_lm.get_emergencyLevel()) {
            case "1":
                locationID = refLow.push().getKey();
                assert locationID != null;
                temp_lm.set_locationID(locationID);
                refLow.child(locationID).setValue(temp_lm);
                break;
            case "2":
                locationID = refModerate.push().getKey();
                assert locationID != null;
                temp_lm.set_locationID(locationID);
                refModerate.child(locationID).setValue(temp_lm);
                break;
            default:
                locationID = refHigh.push().getKey();
                assert locationID != null;
                temp_lm.set_locationID(locationID);
                refHigh.child(locationID).setValue(temp_lm);
        }
        Toast.makeText(this, "Cầu cứu thành công", Toast.LENGTH_LONG).show();

    }

    private Address getLocationOfForm(String location) {
        //   String location = String.valueOf(eLocation.getText());
        LatLng tmpLatLng = null;
        List<Address> addresses = null;


        Geocoder geocoder = new Geocoder(this);

        try {
            addresses = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {

            Log.d(TAG, getString(R.string.ADRESS_NOT_FOUND));
            e.printStackTrace();
        }

        Address address = null;

        if (addresses != null && addresses.size() > 0) {
            address = addresses.get(0);
            return address;
        }
        return null;

    }

    public void choose_current_location(View view) {
        if (cbCurrentLocation.isChecked()) {
            hideLocationText(View.GONE);
        } else {
            hideLocationText(View.VISIBLE);
        }
    }

    private void hideLocationText(int state) {
        eLocation.setVisibility(state);
    }
}