package wang.julis.jproject.example.media;

import wang.julis.jproject.example.media.audio.AudioRecordActivity;
import wang.julis.jproject.example.media.audio.AudioTrackActivity;
import wang.julis.jproject.main.BaseListActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 16:11
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

public class AudioMainActivity extends BaseListActivity {

    @Override
    protected void initData() {
        addActivity(" AudioRecord", AudioRecordActivity.class);
        addActivity(" AudioTrack", AudioTrackActivity.class);

        submitActivityList();
    }
}
