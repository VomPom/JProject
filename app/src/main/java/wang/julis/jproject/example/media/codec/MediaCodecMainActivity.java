package wang.julis.jproject.example.media.codec;


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
        addItem("H.264硬解码/软解码", H264Activity.class);
        addItem("test", MediaExtractorActivity.class);
    }


}
