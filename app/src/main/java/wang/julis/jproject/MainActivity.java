package wang.julis.jproject;

import julis.wang.kotlinlearn.KotlinMainActivity;
import wang.julis.jproject.blog.ArticlePosterGeneratorActivity;
import wang.julis.jproject.blog.PosterGeneratorActivity;
import wang.julis.jproject.example.anim.AnimationMainActivity;
import wang.julis.jproject.example.media.MediaMainActivity;
import wang.julis.jproject.example.media.little.LittleMainActivity;
import julis.wang.learnopengl.opengl.OpenGLMainActivity;
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

        //TODO:换了blog主题之后这里两个会崩溃，有空再修
        addActivity("文章海报生成", ArticlePosterGeneratorActivity.class);
        addActivity("博客海报生成", PosterGeneratorActivity.class);

        addActivity("动画相关", AnimationMainActivity.class);
        addActivity("小测试", LittleMainActivity.class);
        addActivity("音视频", MediaMainActivity.class);
        addActivity("Kotlin", KotlinMainActivity.class);
        addActivity("OpenGL ES", OpenGLMainActivity.class);
    }


}

