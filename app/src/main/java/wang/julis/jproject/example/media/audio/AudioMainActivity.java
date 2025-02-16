package wang.julis.jproject.example.media.audio;


import wang.julis.jwbase.basecompact.baseList.BaseListActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 16:11
 *
 * Description :
 * History   :
 *
 *******************************************************/

public class AudioMainActivity extends BaseListActivity {

    @Override
    protected void initData() {
        addItem("AudioRecord/Track", AudioRecordTrackActivity.class);
    }
}
