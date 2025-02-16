package wang.julis.jproject.example.anim;


import com.julis.annotation.Page;

import wang.julis.jwbase.basecompact.baseList.BaseListActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 11:33
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/
@Page("animation")
public class AnimationMainActivity extends BaseListActivity {
    @Override
    protected void initData() {
        addItem("补间动画", AnimationActivity.class);
        addItem("ValueAnimator", ValueAnimatorActivity.class);
        addItem("ObjectAnimator", ObjectAnimatorActivity.class);
        addItem("AnimatorSet", AnimatorSetActivity.class);
    }

}
