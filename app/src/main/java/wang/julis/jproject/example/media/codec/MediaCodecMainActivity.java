package wang.julis.jproject.example.media.codec;

import wang.julis.jproject.main.BaseListActivity;

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
        addActivity("合成新视频", MuxerActivity.class);
        addActivity("H.264硬解码/软解码", H264Activity.class);
        submitActivityList();
    }


}
