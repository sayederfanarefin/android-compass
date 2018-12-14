package info.sayederfanarefin.compass.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import info.sayederfanarefin.compass.R;
import info.sayederfanarefin.compass.location.LocationHelper;
import info.sayederfanarefin.compass.sensor.SensorListener;
import info.sayederfanarefin.compass.sensor.view.AccelerometerView;
import info.sayederfanarefin.compass.sensor.view.CompassView2;
import info.sayederfanarefin.compass.utils.Utility;
import info.sayederfanarefin.compass.location.database.CompassPref;
import info.sayederfanarefin.compass.location.model.LocationData;
import info.sayederfanarefin.compass.location.model.Sunshine;

import java.util.Locale;

import static info.sayederfanarefin.compass.utils.Utility.getDirectionText;

/**
 * Created by Duy on 10/17/2017.
 */

public class CompassFragment extends BaseFragment implements SensorListener.OnValueChangedListener,
        LocationHelper.LocationDataChangeListener {
    public static final String TAG = "CompassFragment";
    private static final int REQUEST_ENABLE_GPS = 1002;
    private TextView mTxtAddress;

    private TextView mTxtPitch, mTxtRoll;
    private TextView mTxtLonLat, mTxtAltitude;


    private LocationHelper mLocationHelper;
    private CompassView2 mCompassView;
    private AccelerometerView mAccelerometerView;
    private SensorListener mSensorListener;
    private CompassPref mCompassPref;

    public static CompassFragment newInstance() {

        Bundle args = new Bundle();

        CompassFragment fragment = new CompassFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindView();

        mLocationHelper = new LocationHelper(this);
        mLocationHelper.setLocationValueListener(this);
        mLocationHelper.onCreate();

        mSensorListener = new SensorListener(getContext());
        mSensorListener.setOnValueChangedListener(this);

        if (!Utility.isNetworkAvailable(getContext())) {
            Toast.makeText(getContext(), "No internet access", Toast.LENGTH_SHORT).show();
        } else {
            LocationManager manager = (LocationManager) getContext()
                    .getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            }
        }

        onUpdateLocationData(null);
    }

    private void bindView() {
        mTxtAddress = (TextView) findViewById(R.id.txt_address);
        mTxtAddress.setSelected(true);

//        mTxtSunrise = (TextView) findViewById(R.id.txt_sunrise);
//        mTxtSunset = (TextView) findViewById(R.id.txt_sunset);

        mTxtLonLat = (TextView) findViewById(R.id.txt_lon_lat);
        mTxtAltitude = (TextView) findViewById(R.id.txt_altitude);

        mCompassView = (CompassView2) findViewById(R.id.compass_view);
        mAccelerometerView = (AccelerometerView) findViewById(R.id.accelerometer_view);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mSensorListener != null) {
            mSensorListener.start();
        }
    }

    @Override
    public void onStop() {
        if (mSensorListener != null) {
            mSensorListener.stop();
        }
        super.onStop();

    }


    @Override
    public void onRotationChanged(float azimuth, float roll, float pitch) {
        String str = ((int) azimuth) + "° " + getDirectionText(azimuth);
        mCompassView.getSensorValue().setRotation(azimuth, roll, pitch);
        mAccelerometerView.getSensorValue().setRotation(azimuth, roll, pitch);

    }

    @Override
    public void onMagneticFieldChanged(float value) {
        mCompassView.getSensorValue().setMagneticField(value);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_compass;
    }

    //https://stackoverflow.com/questions/39336461/how-can-i-enable-or-disable-the-gps-programmatically-on-android-6-x
    private void buildAlertMessageNoGps() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_ENABLE_GPS);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ENABLE_GPS:
                mLocationHelper.onCreate();
                break;
        }
    }


    @Override
    public void onUpdateLocationData(@Nullable LocationData locationData) {
        if (locationData == null) {
            try{
                locationData = mCompassPref.getLastedLocationData();
            }catch (Exception e){

            }
        }
        if (locationData != null) {

            //address
            mTxtAddress.setText("Address: " + locationData.getAddressLine());

            //location
            float longitude = (float) locationData.getLongitude();
            float latitude = (float) locationData.getLatitude();
            String lonStr = Utility.formatDms(longitude) + " " + getDirectionText(longitude);
            String latStr = Utility.formatDms(latitude) + " " + getDirectionText(latitude);
            String latlon = "Coordinate: " + String.format("%s\n%s", lonStr, latStr);
            mTxtLonLat.setText(latlon);

            //altitude
            double altitude = locationData.getAltitude();
            mTxtAltitude.setText("Altitude: " + String.format(Locale.US, "%d m", (long) altitude));
        }
    }
}
