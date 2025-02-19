package wang.julis.jproject.example.media.screenrecord;

/*******************************************************
 *
 * Created by juliswang on 2021/08/26 11:24 
 *
 * Description : 
 *
 *
 *******************************************************/

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.julis.wang.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MediaProjectionDemoActivity extends Activity {
    private static final String TAG = "MediaProjectionDemo";
    private static final int PERMISSION_CODE = 1;
    private static final List<Resolution> RESOLUTIONS = new ArrayList<Resolution>() {{
        add(new Resolution(640, 360));
        add(new Resolution(960, 540));
        add(new Resolution(1366, 768));
        add(new Resolution(1600, 900));
    }};
    private int mScreenDensity;
    private MediaProjectionManager mProjectionManager;
    private int mDisplayWidth;
    private int mDisplayHeight;
    private boolean mScreenSharing;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private Surface mSurface;
    private SurfaceView mSurfaceView;
    private ToggleButton mToggle;
    private TextView tvTime;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            int data = bundle.getInt("data");
            tvTime.setText(String.valueOf(data));
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_projection);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);
        mSurface = mSurfaceView.getHolder().getSurface();
        tvTime = findViewById(R.id.tv_time);
        mProjectionManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        ArrayAdapter<Resolution> arrayAdapter = new ArrayAdapter<Resolution>(
                this, android.R.layout.simple_list_item_1, RESOLUTIONS);
        Spinner s = (Spinner) findViewById(R.id.spinner);
        s.setAdapter(arrayAdapter);
        s.setOnItemSelectedListener(new ResolutionSelector());
        s.setSelection(0);
        mToggle = (ToggleButton) findViewById(R.id.screen_sharing_toggle);
        mToggle.setSaveEnabled(false);
        mToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            onToggleScreenShare(buttonView);
        });
        schedule();
    }

    private int count = 1;

    private void schedule() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message message = mHandler.obtainMessage(1);
                Bundle bundle = new Bundle();
                bundle.putInt("data", count++);
                message.setData(bundle);
                mHandler.sendMessage(message);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    @Override
    protected void onStop() {
        stopScreenSharing();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PERMISSION_CODE) {
            Log.e(TAG, "Unknown request code: " + requestCode);
            return;
        }
        if (resultCode != RESULT_OK) {
            Toast.makeText(this,
                    "User denied screen sharing permission", Toast.LENGTH_SHORT).show();
            return;
        }
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
        mMediaProjection.registerCallback(new MediaProjectionCallback(), null);
        mVirtualDisplay = createVirtualDisplay();
    }

    public void onToggleScreenShare(View view) {
        if (((ToggleButton) view).isChecked()) {
            shareScreen();
        } else {
            stopScreenSharing();
        }
    }

    private void shareScreen() {
        mScreenSharing = true;
        if (mSurface == null) {
            return;
        }
        if (mMediaProjection == null) {
            startActivityForResult(mProjectionManager.createScreenCaptureIntent(),
                    PERMISSION_CODE);
            return;
        }
        mVirtualDisplay = createVirtualDisplay();
    }

    private void stopScreenSharing() {
        if (mToggle.isChecked()) {
            mToggle.setChecked(false);
        }
        mScreenSharing = false;
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
    }

    private VirtualDisplay createVirtualDisplay() {
        return mMediaProjection.createVirtualDisplay("ScreenSharingDemo",
                mDisplayWidth, mDisplayHeight, mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mSurface, null /*Callbacks*/, null /*Handler*/);
    }

    private void resizeVirtualDisplay() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.resize(mDisplayWidth, mDisplayHeight, mScreenDensity);
    }

    private class ResolutionSelector implements Spinner.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
            Resolution r = (Resolution) parent.getItemAtPosition(pos);
            ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();
            if (getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                mDisplayHeight = r.y;
                mDisplayWidth = r.x;
            } else {
                mDisplayHeight = r.x;
                mDisplayWidth = r.y;
            }
            lp.height = mDisplayHeight;
            lp.width = mDisplayWidth;
            mSurfaceView.setLayoutParams(lp);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) { /* Ignore */ }
    }

    private class MediaProjectionCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            mMediaProjection = null;
            stopScreenSharing();
        }
    }

    private class SurfaceCallbacks implements SurfaceHolder.Callback {
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mDisplayWidth = width;
            mDisplayHeight = height;
            resizeVirtualDisplay();
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurface = holder.getSurface();
            if (mScreenSharing) {
                shareScreen();
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!mScreenSharing) {
                stopScreenSharing();
            }
        }
    }

    private static class Resolution {
        int x;
        int y;

        public Resolution(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return x + "x" + y;
        }
    }
}