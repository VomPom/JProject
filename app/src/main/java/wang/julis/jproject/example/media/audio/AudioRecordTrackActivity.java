package wang.julis.jproject.example.media.audio;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.julis.distance.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 16:22
 *
 * Description :
 *            AudioRecord 音频录制步骤：
 *                 1、构建 AudioRecord 实例：AudioRecord(音源，采样频率(kHz)，声道，采样深度，bufferSize)
 *                 2、AudioRecord.start() 开始录音
 *                 3、AudioRecord.read() 拉取 PCM 数据
 *                 4、AudioRecord.stop() 停止录音
 *                 5、AudioRecord.release() 释放资源
 *
 *            AudioTrack 音频播放步骤
 *                 1、构建 AudioTrack 实例
 *                 2、调用 AudioTrack.play() 开始播放音频
 *                 3、调用 AudioTrack.write() 写入 PCM 数据
 *                 4、调用 AudioTrack.stop() 停止音频播放
 *                 5、调用 AudioTrack.release() 释放资源
 *
 * History   :
 *
 *******************************************************/

public class AudioRecordTrackActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AudioRecordActivity";
    private static final String PCM_NAME = "test.pcm";

    private static final int SAMPLE_RATE = 44100;
    //声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    //返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private File SAVE_PATH;
    private boolean isRecording = true;
    private byte[] audioData = null;
    private AudioRecord audioRecord;

    private TextView tvTips, tvPcm2WavTips, tvErrorTips;
    private final int minBufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
    );
    private final byte[] byteData = new byte[minBufferSize];
    private Thread recordingThread;

    @Override
    protected void initView() {
        findViewById(R.id.btn_recording_start).setOnClickListener(this);
        findViewById(R.id.btn_recording_stop).setOnClickListener(this);
        findViewById(R.id.btn_play_in_stream).setOnClickListener(this);
        findViewById(R.id.btn_play_in_static).setOnClickListener(this);
        findViewById(R.id.btn_pcm_to_wav).setOnClickListener(this);

        tvTips = findViewById(R.id.tv_tips);
        tvErrorTips = findViewById(R.id.tv_error_tips);
        tvPcm2WavTips = findViewById(R.id.tv_pcm_to_wav_tips);
    }

    @Override
    protected void initData() {
        SAVE_PATH = getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                minBufferSize);
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
            case R.id.btn_play_in_stream:
                playInModeStream();
                break;
            case R.id.btn_play_in_static:
                playInModeStatic();
                break;
            case R.id.btn_pcm_to_wav:
                pcmToWav();
                break;
            default:
                break;
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
    private void pcmToWav() {
        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        String sourcePath = SAVE_PATH + "/" + PCM_NAME;
        String outPath = SAVE_PATH + "/test.wav";
        tvPcm2WavTips.setText("录音文件保存在：" + outPath);
        pcmToWavUtil.pcmToWav(sourcePath, outPath);
    }

    @SuppressLint("SetTextI18n")
    private void startRecoding() {
        audioRecord.startRecording();
        isRecording = true;
        final File file = new File(SAVE_PATH, PCM_NAME);
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

    /**
     * 在Streaming模式下，应用程序使用其中一种write()方法将连续的数据流写入AudioTrack 。
     * 当数据从Java层传输到native层并排队等待播放时，它们会阻塞并返回。
     * 在播放音频数据块时，流模式非常有用：
     * 1、由于声音播放的持续时间太长而不能装入内存，
     * 2、由于音频数据的特性（高采样率，每个样本的位数……）而不能装入内存
     * 3、在先前排队的音频正在播放时接收或生成。
     */
    private void playInModeStream() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        AudioFormat audioFormat = new AudioFormat.Builder().setSampleRate(SAMPLE_RATE)
                .setEncoding(AUDIO_FORMAT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build();

        AudioTrack audioTrack = new AudioTrack(
                audioAttributes,
                audioFormat,
                minBufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.play();
        File file = new File(SAVE_PATH, PCM_NAME);
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            new Thread(() -> {
                try {
                    while (fileInputStream.available() > 0) {
                        int readCount = fileInputStream.read(byteData);
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                                readCount == AudioTrack.ERROR_BAD_VALUE) {
                            continue;
                        }
                        if (readCount != 0 && readCount != -1) {
                            audioTrack.write(byteData, 0, readCount);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
            tvErrorTips.setText(e.toString());
        }
    }


    /**
     * 播放，使用static模式
     */
    @SuppressLint("StaticFieldLeak")
    private void playInModeStatic() {
        // static模式，需要将音频数据一次性write到AudioTrack的内部缓冲区
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InputStream in = getResources().openRawResource(R.raw.going_to_rest);
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        for (int b; (b = in.read()) != -1; ) {
                            out.write(b);
                        }
                        Log.d(TAG, "Got the data");
                        audioData = out.toByteArray();
                    } finally {
                        in.close();
                    }
                } catch (IOException e) {
                    Log.wtf(TAG, "Failed to read", e);
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void v) {
                Log.i(TAG, "Creating track...audioData.length = " + audioData.length);

                // R.raw.going_to_rest铃声文件的相关属性为 16000, 8-bit, Mono
                AudioTrack audioTrack = new AudioTrack(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build(),
                        new AudioFormat.Builder().setSampleRate(16000)
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build(),
                        audioData.length,
                        AudioTrack.MODE_STATIC,
                        AudioManager.AUDIO_SESSION_ID_GENERATE);
                Log.d(TAG, "Writing audio data...");
                audioTrack.write(audioData, 0, audioData.length);
                Log.d(TAG, "Starting playback");
                audioTrack.play();
                Log.d(TAG, "Playing");
            }
        }.execute();
    }


}
