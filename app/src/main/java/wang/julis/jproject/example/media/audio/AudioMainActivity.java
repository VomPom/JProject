package wang.julis.jproject.example.media.audio;

import wang.julis.jproject.main.BaseListActivity;

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
        addActivity("AudioRecord/Track", AudioRecordTrackActivity.class);
        submitActivityList();
    }
}
