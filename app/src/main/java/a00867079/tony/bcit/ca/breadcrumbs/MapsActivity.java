package a00867079.tony.bcit.ca.breadcrumbs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    TextView edit;
    private int seconds;
    private int minutes;
    private int hours;



    LocationManager locationManager;
    String provider;
    String provider2;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        edit = (TextView)findViewById(R.id.timer);
		startTimer(this.findViewById(android.R.id.content));






        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            criteria.setAccuracy(Criteria.ACCURACY_FINE); //gps provider
            provider = locationManager.getBestProvider(criteria, false);

            Criteria criteria2 = new Criteria();
            criteria2.setPowerRequirement(Criteria.POWER_LOW);
            criteria2.setAccuracy(Criteria.ACCURACY_COARSE); //network provider
            provider2 = locationManager.getBestProvider(criteria2, false);

            locationManager.requestLocationUpdates(provider, 10 * 60 * 1000, 1, this);
            location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            } else {
//                tv3.setText("Calculating distance");
            }
        }catch(SecurityException e){

        }






    }

    public void startTimer(View v){
        seconds = 0;
        minutes = 0;
        hours = 1; //switcvh this back to 0 later
        Intent intent = getIntent();
        if(intent!=null){
            String newString = intent.getStringExtra("msg");
            if(newString.equals("1 hour")){
                hours = 1;
            }
            if(newString.equals("2 hours")){
                hours = 2;
            }
            if(newString.equals("3 hours")){
                hours = 3;
            }
            if(newString.equals("4 hours")){
                hours = 4;
            }
            if(newString.equals("5 hours")){
                hours = 5;
            }
            if(newString.equals("30 Minutes")){
                minutes = 30;
            }
            timer = new Timer();
            initializeTimerTask();
            timer.schedule(timerTask,0,1000);
        }
    }


    public void stopTimerTask(View v){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        Intent i = new Intent(this, StartMenu.class);
        i.putExtra("message", "Mark ended");
        startActivity(i);
    }

    public void initializeTimerTask(){
        timerTask = new TimerTask(){
            public void run(){
                handler.post(new Runnable(){
                   public void run(){
                       decrease();
                   }
                });
            }
        };
    }

    public void decrease(){
        /*seconds++;
        if(seconds % 60 == 0 && seconds != 0){
            minutes++;
            seconds = 0;
        }
        if(hours % 60 == 0 && minutes != 0){
            hours++;
            minutes = 0;
        }
        String sec = String.format("%02d", seconds);
        String mins = String.format("%02d", minutes);
        String hour = String.format("%02d", hours);
        edit.setText(hour+":"+mins+":"+sec);*/
		if(hours != 0 || minutes !=0 || seconds != 0){
			if(minutes == 0  && seconds ==  0 && hours != 0){
				hours--;
				minutes = 60;
			}
			if(seconds == 0 && minutes != 0){
				minutes--;
				seconds = 60;
			}
			seconds--;
			String sec = String.format("%02d", seconds);
			String mins = String.format("%02d", minutes);
			String hour = String.format("%02d", hours);
			edit.setText(hour+":"+mins+":"+sec);
		} else {
            Intent intent = getIntent();
            String tempString = intent.getStringExtra("msg");
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle("Timer Done!");

            // Setting Dialog Message
            alertDialog.setMessage(tempString + " passed.");

            // Setting OK Button
            alertDialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to execute after dialog closed
                    Toast.makeText(getApplicationContext(), "Timer finished", Toast.LENGTH_SHORT).show();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    public void resetTimer(){
        seconds = 0;
        minutes = 0;
        hours = 0;
        Intent intent = getIntent();
        if(intent!=null){
            String newString = intent.getStringExtra("msg");
            if(newString.equals("1 hour")){
                hours = 1;
            }
            if(newString.equals("2 hours")){
                hours = 2;
            }
            if(newString.equals("3 hours")){
                hours = 3;
            }
            if(newString.equals("4 hours")){
                hours = 4;
            }
            if(newString.equals("5 hours")){
                hours = 5;
            }
            if(newString.equals("30 Minutes")){
                minutes = 30;
            }
            //startTimer(this.findViewById(android.R.id.content);
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Add a marker in Sydney, Australia, and move the camera.
        //double latitude = location.getLatitude();
        //double longitude = location.getLongitude();
       // LatLng latLng = new LatLng(latitude, longitude);
        LatLng sydney = new LatLng(-34, 151);
        map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }




    @Override
    public void onLocationChanged(Location location) {
        double lat =  location.getLatitude();
        double lng =  location.getLongitude();
        Toast.makeText(this, lat + " : " + lng, Toast.LENGTH_SHORT).show();
        double distance = location.distanceTo(location);
        String stringDistance;
        if(distance > 999){
            stringDistance= "Distance: " + String.format("%.1f", distance / 1000)+ "km";
        }
        else
            stringDistance= "Distance: " + String.format("%.0f", distance) + "m";

//        tv1.setText("Latitude: " + String.format("%.5f", lat));
//        tv2.setText("Longitude: " + String.format("%.5f", lng));
//        tv3.setText(stringDistance);
    }


    @Override
    public void onProviderEnabled(String provider) {
//        tv3.setText("Calculating distance");
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
//        tv3.setText("Cannot calculate distance, location services are disabled");
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


}
