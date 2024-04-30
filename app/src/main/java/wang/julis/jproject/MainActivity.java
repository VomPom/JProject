package wang.julis.jproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.julis.annotation.Page;

import julis.wang.kotlinlearn.KotlinMainActivity;
import julis.wang.kotlinlearn.feature.FuncActivity;
import julis.wang.learnopengl.opengl.OpenGLNDKListActivity;
import wang.julis.jproject.example.anim.AnimationMainActivity;
import wang.julis.jproject.example.little.LittleMainActivity;
import wang.julis.jproject.example.little.ToolsMainActivity;
import wang.julis.jproject.example.media.MediaMainActivity;
import wang.julis.jproject.example.media.pag.PAGMainActivity;
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
@Page("main")
public class MainActivity extends BaseListActivity {
    public static final String HOST = "main";
    private final boolean quickJump = false;

    private final Class<?> debugClass = FuncActivity.class;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (quickJump) {
            quickOpenActivity();
        }
    }

    @Override
    protected void initData() {
        addActivity("动画相关", AnimationMainActivity.class);
        addActivity("音视频", MediaMainActivity.class);
        addActivity("PAG", PAGMainActivity.class);
        addActivity("Kotlin", KotlinMainActivity.class);
        addActivity("Cpp", CppMainActivity.class);
        addActivity("OpenGL ES", OpenGLNDKListActivity.class);
        addActivity("小测试", LittleMainActivity.class);
        addActivity("小工具", ToolsMainActivity.class);
    }

    private void quickOpenActivity() {
        if (debugClass == this.getClass()) {
            return;
        }
        startActivity(new Intent(this, debugClass));
    }


}

