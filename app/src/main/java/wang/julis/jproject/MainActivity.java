package wang.julis.jproject;

import julis.wang.kotlinlearn.KotlinMainActivity;
import julis.wang.learnopengl.opengl.OpenGLNDKListActivity;
import wang.julis.jproject.example.anim.AnimationMainActivity;
import wang.julis.jproject.example.media.MediaMainActivity;
import wang.julis.jproject.example.media.little.LittleMainActivity;
import wang.julis.jproject.example.media.little.ToolsMainActivity;
import wang.julis.jwbase.basecompact.baseList.BaseListActivity;
import wang.julis.learncpp.CppMainActivity;

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
        addActivity("动画相关", AnimationMainActivity.class);
        addActivity("音视频", MediaMainActivity.class);
        addActivity("Kotlin", KotlinMainActivity.class);
        addActivity("Cpp", CppMainActivity.class);
        addActivity("OpenGL ES", OpenGLNDKListActivity.class);
        addActivity("小测试", LittleMainActivity.class);
        addActivity("小工具", ToolsMainActivity.class);


    }


}

