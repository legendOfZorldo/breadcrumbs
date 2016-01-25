package a00867079.tony.bcit.ca.breadcrumbs;

/**
 * Created by Asus Q502L on 11/27/2015.
 */

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class location extends AppCompatActivity implements LocationListener {

    Location location;  // the current location of ths user
    Location location2 = new Location("Event Location");    //location of the event
    TextView tv1;   //textview for displaying current latitude for testing
    TextView tv2;   //textview for displaying current longitude for testing
    TextView tv3;   //textview for displaying distance to event
    TextView tv4;
    String provider;
    String provider2;
    LocationManager locationManager;

    private GoogleMap googleMap;
    LatLng eventloc;
    String eventname;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        eventname="BCIT SW1";
        googleMap = mapFrag.getMap();
        eventloc = new LatLng(49.277990, -123.113394);

        googleMap.addMarker(new MarkerOptions().position(eventloc).title(eventname));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventloc, 15));

        //googleMap.addMarker(new MarkerOptions().position(curloc).title("Current Location"));

        tv3 = (TextView)findViewById(R.id.text_distance);
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

            location2.setLatitude(49.251017);
            location2.setLongitude(-123.003304);

            if (location != null) {
                onLocationChanged(location);
            } else {
                tv3.setText("Calculating distance");
            }
        }catch(SecurityException e){

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void onResume() {
        int mintime =  1000;
        super.onResume();

        try {
            locationManager.requestLocationUpdates(provider, mintime, 1, this);
            location = locationManager.getLastKnownLocation(provider);
            if(location==null) {
                locationManager.requestLocationUpdates(provider2, mintime, 1, this);
            }
            else {
                locationManager.requestLocationUpdates(provider, mintime, 1, this);
            }
            //curloc = new LatLng(location.getLatitude(), location.getLongitude());
        }catch (SecurityException e){

        }
    }

    protected void onPause() {
        super.onPause();
        try {
            locationManager.removeUpdates(this);
        }catch(SecurityException e){

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        double lat =  location.getLatitude();
        double lng =  location.getLongitude();
        double distance = location.distanceTo(location2);
        String stringDistance;
        if(distance > 999){
            stringDistance= "Distance: " + String.format("%.1f", distance / 1000)+ "km";
        }
        else
            stringDistance= "Distance: " + String.format("%.0f", distance) + "m";

        tv1.setText("Latitude: " + String.format("%.5f", lat));
        tv2.setText("Longitude: " + String.format("%.5f", lng));
        tv3.setText(stringDistance);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        tv3.setText("Calculating distance");
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        tv3.setText("Cannot calculate distance, location services are disabled");
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }

}
