package wang.julis.jproject.example.media;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.julis.wang.R;

import java.io.IOException;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/06/22 19:42
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class MediaPlayerActivity extends BaseActivity implements View.OnClickListener {
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mPlayer;
    private View viewBg;

    private static final String NET_VIDEO_PATH = "https://media.w3.org/2010/05/sintel/trailer.mp4";

    @Override
    protected void initView() {
        SurfaceView surfaceView = findViewById(R.id.sv_video);
        viewBg = findViewById(R.id.view_bg);
        findViewById(R.id.btn_recording_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
        findViewById(R.id.btn_pause).setOnClickListener(this);

        surfaceHolder = surfaceView.getHolder();

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource(NET_VIDEO_PATH);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mPlayer.setDisplay(surfaceHolder);
                mPlayer.prepareAsync();
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                        viewBg.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_media;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_recording_start:
                mPlayer.start();
                break;
            case R.id.btn_pause:
                mPlayer.pause();
                break;
            case R.id.btn_stop:
                mPlayer.stop();
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        mPlayer.release();
    }
}
