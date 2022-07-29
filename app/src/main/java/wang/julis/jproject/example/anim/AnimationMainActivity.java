package wang.julis.jproject.example.anim;


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

public class AnimationMainActivity extends BaseListActivity {
    @Override
    protected void initData() {
        addActivity("补间动画", AnimationActivity.class);
        addActivity("ValueAnimator", ValueAnimatorActivity.class);
        addActivity("ObjectAnimator", ObjectAnimatorActivity.class);
        addActivity("AnimatorSet", AnimatorSetActivity.class);
    }

}
