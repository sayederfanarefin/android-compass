package info.sayederfanarefin.compass;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import info.sayederfanarefin.compass.fragments.CompassFragment;
import info.sayederfanarefin.compass.sensor.SensorUtil;
import info.sayederfanarefin.compass.view.CustomViewPager;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        if (hasSensor()) {
            createView();
        } else {
            showDialogUnsupported();
        }
    }

    private void createView() {
        CustomViewPager viewPager = (CustomViewPager) findViewById(R.id.view_pager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new CompassFragment();
                }
                return null;
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }

    private boolean hasSensor() {
        return SensorUtil.hasAccelerometer(this) && SensorUtil.hasMagnetometer(this);
    }

    private void showDialogUnsupported() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your device unsupported Accelerometer sensor and Magnetometer");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).create().show();
    }
}
