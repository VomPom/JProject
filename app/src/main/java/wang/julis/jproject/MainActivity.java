package wang.julis.jproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.julis.annotation.Page;
import com.vompom.media.MediaMainActivity;
import com.vompom.media.codec.MediaCodecMainActivity;
import com.vompom.media.pag.PAGMainActivity;

import julis.wang.kotlinlearn.KotlinMainActivity;
import julis.wang.learnopengl.opengl.OpenGLNDKListActivity;
import wang.julis.jproject.example.anim.AnimationMainActivity;
import wang.julis.jproject.example.little.LittleMainActivity;
import wang.julis.jproject.example.little.ToolsMainActivity;
import wang.julis.jproject.example.source.SourceMainActivity;
import wang.julis.jproject.example.thread.ThreadMainActivity;
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
    private final boolean quickJump = true;

    private final Class<?> debugClass = MediaCodecMainActivity.class;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (quickJump) {
            quickOpenActivity();
        }
    }

    @Override
    protected void initData() {
        addItem("动画相关", AnimationMainActivity.class);
        addItem("音视频", MediaMainActivity.class);
        addItem("多线程", ThreadMainActivity.class);
        addItem("PAG", PAGMainActivity.class);
        addItem("Kotlin", KotlinMainActivity.class);
        addItem("Cpp", CppMainActivity.class);
        addItem("OpenGL ES", OpenGLNDKListActivity.class);
        addItem("小测试", LittleMainActivity.class);
        addItem("小工具", ToolsMainActivity.class);
        addItem("源码解析", SourceMainActivity.class);
    }

    private void quickOpenActivity() {
        if (debugClass == this.getClass()) {
            return;
        }
        startActivity(new Intent(this, debugClass));
    }


}

