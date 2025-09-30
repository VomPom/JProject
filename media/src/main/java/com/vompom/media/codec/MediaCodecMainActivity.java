package com.vompom.media.codec;

import com.vompom.media.codec.v1.muxer.MuxerActivity;
import com.vompom.media.codec.v1.player.PlayWithoutAudioActivity;
import com.vompom.media.codec.v1.player.PlayerV1Activity;
import com.vompom.media.codec.v2.PlayerRawActivity;
import com.vompom.media.codec.v2.PlayerV2Activity;
import com.vompom.media.utils.ResUtils;

import wang.julis.jwbase.basecompact.baseList.BaseListActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/13 10:49
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class MediaCodecMainActivity extends BaseListActivity {

    @Override
    protected void initData() {
        addItem("合成新视频", MuxerActivity.class);
        addItem("H.264硬解码", H264Activity.class);
        addItem("无音频播放视频", PlayWithoutAudioActivity.class);
        addItem("播放器v1", PlayerV1Activity.class);

        addItem("播放器v2-原始", PlayerRawActivity.class);
        addItem("播放器v2-Player包装", PlayerV2Activity.class);
        ResUtils.INSTANCE.init(this);
    }


}
