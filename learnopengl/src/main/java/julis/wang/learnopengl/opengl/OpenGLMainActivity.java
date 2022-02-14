package julis.wang.learnopengl.opengl;

import julis.wang.learnopengl.opengl.basefunc.OpenGLImageActivity;
import julis.wang.learnopengl.opengl.basefunc.OpenGLTriangleActivity;
import julis.wang.learnopengl.opengl.main.BaseListActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 13:49
 *
 * Description : https://zhuanlan.zhihu.com/p/28518637
 *
 * History   :
 *
 *******************************************************/

public class OpenGLMainActivity extends BaseListActivity {

    @Override
    protected void initData() {
        addActivity("绘制一个三角形", OpenGLTriangleActivity.class);
        addActivity("OpenGL 显示一张图片", OpenGLImageActivity.class);
        addActivity("NDK OpenGL", OpenGLNDKListActivity.class);
    }
}
