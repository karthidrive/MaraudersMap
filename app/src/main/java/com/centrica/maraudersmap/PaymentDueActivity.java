package com.centrica.maraudersmap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PaymentDueActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    ImageButton imgMenu;
    private ClusterManager<MyItem> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_due);

        imgMenu = (ImageButton) findViewById(R.id.imgmenu);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        imgMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showFilterPopup(view);
            }
        });


    }
    // Display anchored popup menu based on view selected
    private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.pay_menu_list, popup.getMenu());

        setForceShowIcon(popup);

        final View temp =v;

        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.home_view:
                        finish();
                        return true;
                    case R.id.meter_read_dues:
                        launchActivity(PaymentDueActivity.this,MeterReadDueActivity.class);
                        return true;
                    case R.id.map_types:
                        showMapTypePopup(temp);
                        return true;
                    case R.id.show_legends:
                        showLegendPopup(temp);
                        return true;
                    case R.id.cluster:
                        //setUpClusterer();
                        return true;
                    case R.id.about:
                        launchActivity(PaymentDueActivity.this,SuperActivity.class);
                        return true;
                    case R.id.send_sms_all:
                        composeSmsMessage("Dear Customer, Please submit your meter reading for August month! Thanks,British Gas Team.","+74253335;+745222222;+7423655");
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

    private void showLegendPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.legend_list, popup.getMenu());

        setForceShowIcon(popup);

        final View temp =v;

        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.normal_home:

                        return true;
                    case R.id.meter_due_home:

                        return true;
                    case R.id.pay_due_home:
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }
    private void showMapTypePopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.maptype_list, popup.getMenu());

        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.map_normal:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        return true;
                    case R.id.map_satellite:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        return true;
                    case R.id.map_hybrid:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        return true;
                    case R.id.map_terrain:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

    private void launchActivity(Context packageContext, Class<?> cls) {
        Intent launchActivity = new Intent(packageContext, cls);
        startActivity(launchActivity);
    }

    public static void setForceShowIcon(PopupMenu popupMenu) {
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private void setUpClusterer() {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.493201, -0.158860), 10));
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        addClusterMarkers(mClusterManager);
    }

    private void addClusterMarkers(ClusterManager<MyItem> mClusterManager) {

        // Set some lat/lng coordinates to start with.
        double latitude = 51.493201;
        double longitude = -0.158860;

        // Add ten cluster items in close proximity, for purposes of this example.
        for (int i = 0; i < 10; i++) {
            double offset = i / 60d;
            latitude = latitude + offset;
            longitude = longitude + offset;
            MyItem offsetItem = new MyItem(latitude, longitude);
            mClusterManager.addItem(offsetItem);
        }
    }

    public void composeSmsMessage(String message, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:"+phoneNumber)); // This ensures only SMS apps respond
        intent.putExtra("sms_body", message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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

        // Add a marker in Sydney and move the camera
        LatLng home3 = new LatLng(51.383201, -0.166860);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_home))
                .position(home3));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home3, 15f));


        LatLng home31 = new LatLng(51.487537, -0.158158);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_home))
                .position(home31));


        LatLng home32 = new LatLng(51.492224, -0.168532);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_home))
                .position(home32));


        LatLng home34 = new LatLng(51.503552, -0.218829);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_home))
                .position(home34));

        LatLng home35 = new LatLng(51.577141, -0.294424);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_home))
                .position(home35));

        LatLng home36 = new LatLng(51.577141, -0.294424);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_home))
                .position(home36));

        LatLng home37 = new LatLng(51.577778, -0.349005);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_home))
                .position(home37));

        LatLng home4 = new LatLng(51.491597, -0.150766);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon))
                .position(home4));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home4, 15f));

        LatLng home41 = new LatLng(51.513258, -0.221554);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon))
                .position(home41));

    }
}
