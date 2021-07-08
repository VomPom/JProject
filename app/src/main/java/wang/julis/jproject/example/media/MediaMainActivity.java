package wang.julis.jproject.example.media;

import wang.julis.jproject.example.media.codec.MediaCodecActivity;
import wang.julis.jproject.example.media.drawimage.DrawImageActivity;
import wang.julis.jproject.main.BaseListActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 13:49
 *
 * Description : https://zhuanlan.zhihu.com/p/28518637
 *
 * History   :
 *
 *******************************************************/

public class MediaMainActivity extends BaseListActivity {

    @Override
    protected void initData() {
        addActivity("MediaPlayer", MediaPlayerActivity.class);
        addActivity("MediaCodec", MediaCodecActivity.class);
        addActivity("DrawImage", DrawImageActivity.class);
        addActivity("Audio", AudioMainActivity.class);
        submitActivityList();
    }
}
