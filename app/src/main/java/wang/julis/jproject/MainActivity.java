package wang.julis.jproject;

import wang.julis.jproject.blog.ArticlePosterGeneratorActivity;
import wang.julis.jproject.blog.PosterGeneratorActivity;
import wang.julis.jproject.example.anim.AnimationMainActivity;
import wang.julis.jproject.example.binder.client.BinderTestActivity;
import wang.julis.jproject.example.media.MediaMainActivity;
import wang.julis.jproject.example.media.little.JsonTestActivity;
import wang.julis.jproject.main.BaseListActivity;

/*******************************************************
 *
 * Created by julis.wang on 2019/09/24 14:12
 *
 * Description :
 * History   :
 *
 *******************************************************/

public class MainActivity extends BaseListActivity {
    @Override
    protected void initData() {
        addActivity("文章海报生成", ArticlePosterGeneratorActivity.class);
        addActivity("博客海报生成", PosterGeneratorActivity.class);
        addActivity("动画相关", AnimationMainActivity.class);
        addActivity("BinderExample", BinderTestActivity.class);
        addActivity("音视频", MediaMainActivity.class);
        addActivity("JsonTest", JsonTestActivity.class);
        submitActivityList();
    }


}

