package com.centrica.maraudersmap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,SeekBar.OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private GoogleMap mMap;
    private ImageButton imgMenu;
    private EditText editSearchCus;
    private ClusterManager<MyItem> mClusterManager;
    Dialog dialog;

    private PieChart mChart;

    protected String[] mParties = new String[] {"Normal", "Meter Read Due", "Payment Due"};
    protected Typeface mTfRegular;
    protected Typeface mTfLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_maps);

        mTfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        mTfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");

        initialView();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        showDialog();
    }
private void showDialog(){

    // Create custom dialog object
     dialog = new Dialog(MapsActivity.this);
    // Include dialog.xml file
    dialog.setContentView(R.layout.activity_health);
    // Set dialog title
    dialog.setTitle("Health Status");

    // set values for custom dialog components - text, image and button
   /* TextView text = (TextView) dialog.findViewById(R.id.textView);
    text.setText("Custom dialog Android example.");*/



    mChart = (PieChart) dialog.findViewById(R.id.chart1);
    mChart.setUsePercentValues(true);
    mChart.setDescription("");
    mChart.setExtraOffsets(5, 10, 5, 5);

    mChart.setDragDecelerationFrictionCoef(0.95f);

    mChart.setCenterTextTypeface(mTfLight);
    mChart.setCenterText(generateCenterSpannableText());

    mChart.setDrawHoleEnabled(true);
    mChart.setHoleColor(Color.WHITE);

    mChart.setTransparentCircleColor(Color.WHITE);
    mChart.setTransparentCircleAlpha(110);

    mChart.setHoleRadius(58f);
    mChart.setTransparentCircleRadius(61f);

    mChart.setDrawCenterText(true);

    mChart.setRotationAngle(0);
    // enable rotation of the chart by touch
    mChart.setRotationEnabled(true);
    mChart.setHighlightPerTapEnabled(true);

    // mChart.setUnit(" €");
    // mChart.setDrawUnitsInChart(true);

    // add a selection listener
    mChart.setOnChartValueSelectedListener(this);

    setData(3, 35);

    mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
    // mChart.spin(2000, 0, 360);



    Legend l = mChart.getLegend();
    l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
    l.setXEntrySpace(7f);
    l.setYEntrySpace(0f);
    l.setYOffset(0f);

    // entry label styling
    mChart.setEntryLabelColor(Color.WHITE);
    mChart.setEntryLabelTypeface(mTfRegular);
    mChart.setEntryLabelTextSize(12f);


    dialog.show();

    /*Button declineButton = (Button) dialog.findViewById(R.id.button2);
    // if decline button is clicked, close the custom dialog
    declineButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // Close dialog
            dialog.dismiss();
        }
    });*/
}

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("Welcome John!\nTotal Customers: 35");
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 13, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 13, s.length() - 18, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), s.length() - 19, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(.8f), s.length() - 19, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 19, s.length(), 0);
        return s;
    }

    private void setData(int count, float range) {

        float mult = range;

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        entries.add(new PieEntry(20f,"Normal"));
        entries.add(new PieEntry(7f,"Meter Read Due"));
        entries.add(new PieEntry(8f,"Payment Due"));



        PieDataSet dataSet = new PieDataSet(entries, "Health Status");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(rgb("#437DBF"));
        colors.add(rgb("#E71224"));
        colors.add(rgb("#7D7D7D"));



        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        //dataSet.setSelectionShift(0f);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        data.setValueTypeface(mTfLight);
        mChart.setData(data);

        // undo all highlights
        mChart.highlightValues(null);

        mChart.invalidate();
    }

    private void initialView() {
        imgMenu = (ImageButton) findViewById(R.id.imgmenu);
        imgMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showFilterPopup(view);
            }
        });
        editSearchCus = (EditText) findViewById(R.id.sercustomer);
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
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);

        // Add a marker in Sydney and move the camera
        LatLng home1 = new LatLng(51.493201, -0.158860);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.home_icon))
                .position(home1));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home1, 15f));


        LatLng home2 = new LatLng(51.483201, -0.156860);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.red_home))
                .position(home2));
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home2, 15f));


        LatLng home3 = new LatLng(51.383201, -0.166860);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pay_home))
                .position(home3));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home3, 15f));


        LatLng home4 = new LatLng(51.491597, -0.150766);
        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.user_icon))
                .position(home4));

        // Setting a custom info window adapter for the google map
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            // Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.map_infowindow, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                TextView tvLat = (TextView) v.findViewById(R.id.txtreaddate);

                // Getting reference to the TextView to set longitude
                TextView tvLng = (TextView) v.findViewById(R.id.txtmeterunits);

                // Setting the latitude
                tvLat.setText("01/08/2016");

                // Setting the longitude
                tvLng.setText("2153");

                // Returning the view containing InfoWindow contents
                return v;

            }
        });

        // Adding and showing marker while touching the GoogleMap
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
              /*  // Clears any existing markers from the GoogleMap
               // googleMap.clear();

                // Creating an instance of MarkerOptions to set position
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting position on the MarkerOptions
                markerOptions.position(arg0);

                // Animating to the currently touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(arg0));

                // Adding marker on the GoogleMap
                Marker marker = mMap.addMarker(markerOptions);

                // Showing InfoWindow on the GoogleMap
                marker.showInfoWindow();*/

            }
        });


    }

    // Display anchored popup menu based on view selected
    private void showFilterPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        // Inflate the menu from xml
        popup.getMenuInflater().inflate(R.menu.menu_list, popup.getMenu());

        setForceShowIcon(popup);

        final View temp =v;

        // Setup menu item selection
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.meter_read_dues:
                        launchActivity(MapsActivity.this,MeterReadDueActivity.class);
                        return true;
                    case R.id.payment_dues:
                        launchActivity(MapsActivity.this,PaymentDueActivity.class);
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
                        launchActivity(MapsActivity.this,SuperActivity.class);
                        return true;
                    case R.id.send_sms_all:
                        composeSmsMessage("Kindly submit your meter reading for August month! Thanks,British Gas Team.","+74253335;+745222222;+7423655");
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

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
        if(h.getX()==0){
            dialog.dismiss();
        }else if(h.getX()==1){
            Intent launchActivity = new Intent(this, MeterReadDueActivity.class);
            launchActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launchActivity);
        }else if(h.getX()==2){
            Intent launchActivity = new Intent(this, PaymentDueActivity.class);
            launchActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(launchActivity);

        }
    }

    @Override
    public void onNothingSelected() {

    }
}
