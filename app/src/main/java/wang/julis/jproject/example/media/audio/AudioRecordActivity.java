package wang.julis.jproject.example.media.audio;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.julis.distance.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 16:22
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class AudioRecordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AudioRecordActivity";

    private boolean isRecording = true;

    private static final int SAMPLE_RATE = 44100;
    //声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    //返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
    private static final int ENCODING_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecord;
    private TextView tvTips;
    private final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, ENCODING_FORMAT);
    private final byte[] byteData = new byte[minBufferSize];
    private Thread recordingThread;

    @Override
    protected void initView() {
        findViewById(R.id.btn_recording_start).setOnClickListener(this);
        findViewById(R.id.btn_recording_stop).setOnClickListener(this);
        findViewById(R.id.btn_play_start).setOnClickListener(this);
        findViewById(R.id.btn_play_stop).setOnClickListener(this);
        findViewById(R.id.btn_pcm_to_wav).setOnClickListener(this);
        tvTips = findViewById(R.id.tv_tips);
    }

    @Override
    protected void initData() {
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                CHANNEL_CONFIG, ENCODING_FORMAT, minBufferSize);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_audio_recorder;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        switch (viewId) {
            case R.id.btn_recording_start:
                startRecoding();
                break;
            case R.id.btn_recording_stop:
                stopRecording();
                break;
            case R.id.btn_play_start:
                break;
            case R.id.btn_play_stop:
                break;
            default:
        }
    }

    private void stopRecording() {
        isRecording = false;
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            recordingThread = null;
        }

    }

    @SuppressLint("SetTextI18n")
    private void startRecoding() {
        audioRecord.startRecording();
        isRecording = true;
        final File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm");
        tvTips.setText("录音文件保存在：" + file.getAbsolutePath());

        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        if (file.exists()) {
            file.delete();
        }
        recordingThread = new Thread(() -> {
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            if (null != os) {
                while (isRecording) {
                    int read = audioRecord.read(byteData, 0, minBufferSize);
                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            os.write(byteData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    Log.i(TAG, "run: close file output stream !");
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        recordingThread.start();
    }


}
