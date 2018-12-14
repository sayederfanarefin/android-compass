package info.sayederfanarefin.compass.sensor.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import info.sayederfanarefin.compass.R;
import info.sayederfanarefin.compass.sensor.model.SensorValue;
import info.sayederfanarefin.compass.utils.DLog;

/**
 * Created by Sayed Erfan Arefin on 10/20/2018.
 */

public class AccelerometerDrawer {
    private static final String TAG = "AccelerometerCompassHel";
    private final Paint mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final SensorValue mSensorValue = new SensorValue();
    private final Path mPath = new Path();
    private boolean mIsPaintCreated = false;
    /*Color*/
    @ColorInt
    private int mForegroundColor;
    @ColorInt
    private int mBackgroundColor;
    @ColorInt
    private int mPrimaryTextColor;
    @ColorInt
    private int mSecondaryTextColor;
    @ColorInt
    private int mAccentColor;
    @NonNull
    private Context mContext;
    private float mPixelScale;
    private Point mCenter;
    private float mUnitPadding;

    public AccelerometerDrawer(@NonNull Context context) {
        this.mContext = context;
    }

    public void draw(Canvas canvas) {
        mPixelScale = ((float) Math.min(canvas.getWidth(), canvas.getHeight())) / 1000.0f;
        mCenter = new Point(canvas.getWidth() / 2, canvas.getHeight() / 2);
        mUnitPadding = realPx(5);
        initPaint();

        //drawSunTime(canvas);
        drawPitchRoll(canvas);
    }

    private float realPx(float width) {
        return width * mPixelScale;
    }

    private void initPaint() {
        //no need setup
        if (mIsPaintCreated) {
            return;
        }


        mForegroundColor = ContextCompat.getColor(mContext, R.color.compass_foreground_color);
        mBackgroundColor = ContextCompat.getColor(mContext, R.color.compass_background_color);
        mPrimaryTextColor = ContextCompat.getColor(mContext, R.color.compass_text_primary_color);
        mSecondaryTextColor = ContextCompat.getColor(mContext, R.color.compass_text_secondary_color);
        mAccentColor = ContextCompat.getColor(mContext, R.color.compass_accent_color);

        mPathPaint.setStrokeCap(Paint.Cap.ROUND);

        mIsPaintCreated = true;
    }

    public SensorValue getSensorValue() {
        return mSensorValue;
    }

    private void drawPitchRoll(Canvas canvas) {
        int length = 470;
        float maxRadius = realPx(length);
        float radius = maxRadius;

        mPathPaint.setColor(mBackgroundColor);
        mPathPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCenter.x, mCenter.y, radius, mPathPaint);

        mPathPaint.setColor(mPrimaryTextColor);
        mPathPaint.setStyle(Paint.Style.FILL);
        float roll = mSensorValue.getRoll();
        float pitch = mSensorValue.getPitch();

        float cosP = (float) Math.cos(Math.toRadians(90 - pitch));
        float x = realPx(370) * cosP;
        float cosR = (float) Math.cos(Math.toRadians(90 - roll));
        float y = realPx(370) * cosR;
        canvas.drawCircle(mCenter.x - x, mCenter.y + y, realPx(100), mPathPaint);

        radius = maxRadius;
        mPath.reset();
        mPath.moveTo(mCenter.x - radius, mCenter.y);
        mPath.lineTo(mCenter.x + radius, mCenter.y);
        mPath.moveTo(mCenter.x, mCenter.y - radius);
        mPath.lineTo(mCenter.x, mCenter.y + radius);
        mPath.addCircle(mCenter.x, mCenter.y, radius, Path.Direction.CCW);

        mPathPaint.setShadowLayer(realPx(3), 0, 0, Color.BLACK);
        mPathPaint.setColor(mSecondaryTextColor);
        mPathPaint.setStrokeWidth(realPx(5));
        mPathPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPathPaint);

    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        DLog.d(TAG, "onSizeChanged() called with: w = [" + w + "], h = [" + h + "], oldw = [" + oldw + "], oldh = [" + oldh + "]");
        mIsPaintCreated = false;
    }
}
