package wang.julis.jproject.example.media.opengl;

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

public class OpenGLMainActivity extends BaseListActivity {

    @Override
    protected void initData() {
        addActivity("OpenGL Base", OpenGLTriangleActivity.class);
        addActivity("OpenGL 显示一张图片", OpenGLImageActivity.class);
        submitActivityList();
    }


}
