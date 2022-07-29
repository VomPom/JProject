package wang.julis.jproject.example.media.little;

import wang.julis.jproject.blog.ArticlePosterGeneratorActivity;
import wang.julis.jproject.blog.PosterGeneratorActivity;
import wang.julis.jwbase.basecompact.baseList.BaseListActivity;

/*******************************************************
 *
 * Created by juliswang on 2022/02/09 14:27 
 *
 * Description : 
 *
 *
 *******************************************************/

public class ToolsMainActivity extends BaseListActivity {
    @Override
    protected void initData() {
        //TODO:换了blog主题之后这里两个会崩溃，有空再修
        addActivity("文章海报生成", ArticlePosterGeneratorActivity.class);
        addActivity("博客海报生成", PosterGeneratorActivity.class);
    }
}
