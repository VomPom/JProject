package wang.julis.jproject.example.Anim;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.julis.distance.R;

import wang.julis.jwbase.basecompact.BaseActivity;


/**
 * Created by julis.wang on 2018/10/9 星期二.
 * Project:Intent
 * Description:
 */
public class AnimationActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setTitle("补间动画");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        tvTest = findViewById(R.id.tv_test);
        findViewById(R.id.btn_scale).setOnClickListener(this);
        findViewById(R.id.btn_alpha).setOnClickListener(this);
        findViewById(R.id.btn_rotate).setOnClickListener(this);
        findViewById(R.id.btn_trans).setOnClickListener(this);

        findViewById(R.id.btn_com).setOnClickListener(this);
        findViewById(R.id.btn_accerate_decelerate).setOnClickListener(this);
        findViewById(R.id.btn_acce).setOnClickListener(this);

        findViewById(R.id.btn_anticipate).setOnClickListener(this);
        findViewById(R.id.btn_anticipate_overshoot).setOnClickListener(this);
        findViewById(R.id.btn_bounce).setOnClickListener(this);
        findViewById(R.id.btn_cycle).setOnClickListener(this);

        findViewById(R.id.btn_decelerate).setOnClickListener(this);
        findViewById(R.id.btn_linear).setOnClickListener(this);
        findViewById(R.id.btn_over).setOnClickListener(this);

        findViewById(R.id.btn_code_scale).setOnClickListener(this);
        findViewById(R.id.btn_code_alpha).setOnClickListener(this);
        findViewById(R.id.btn_code_rotate).setOnClickListener(this);
        findViewById(R.id.btn_code_trans).setOnClickListener(this);
        findViewById(R.id.btn_code_com).setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_animation;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scale:

                Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
                tvTest.startAnimation(animation1);
                break;
            case R.id.btn_alpha:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.alpha_anim));
                break;
            case R.id.btn_rotate:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anim));
                break;
            case R.id.btn_trans:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.trans_anim));
                break;
            case R.id.btn_com:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.combian_anim));
                break;

            /** interpolator*/
            case R.id.btn_accerate_decelerate:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_accelerate_decelerate_interpolator_anim));
                break;
            case R.id.btn_acce:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_accelerate_interpolator_anim));
                break;
            case R.id.btn_anticipate:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anticipate_interpolator_anim));
                break;


            case R.id.btn_anticipate_overshoot:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_anticipate_overshoot_interpolator_anim));
                break;
            case R.id.btn_bounce:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_bounce_interpolator_anim));
                break;
            case R.id.btn_linear:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_linear_interpolator_in_anim));
                break;


            case R.id.btn_cycle:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_cycle_interpolator_anim));
                break;
            case R.id.btn_decelerate:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_decelerate_interpolator_anim));
                break;
            case R.id.btn_over:
                tvTest.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate_overshoot_interpolator_anim));
                break;
            /**通过代码实现动画*/

            case R.id.btn_code_scale:
                ScaleAnimation animation = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(3000);
                tvTest.startAnimation(animation);
                break;

            case R.id.btn_code_alpha:
                AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.1f);
                alphaAnimation.setDuration(3000);
                alphaAnimation.setFillBefore(true);
                tvTest.startAnimation(alphaAnimation);
                break;

            case R.id.btn_code_rotate:
                RotateAnimation rotateAnim = new RotateAnimation(0, -650, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnim.setDuration(3000);
                rotateAnim.setFillAfter(true);
                tvTest.startAnimation(rotateAnim);
                break;

            case R.id.btn_code_trans:
                TranslateAnimation translateAnim = new TranslateAnimation(Animation.ABSOLUTE, 0, Animation.ABSOLUTE, -80, Animation.ABSOLUTE, 0, Animation.ABSOLUTE, -80);
                translateAnim.setDuration(2000);
                translateAnim.setFillBefore(true);
                tvTest.startAnimation(translateAnim);
                break;

            case R.id.btn_code_com:
                AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.1f);
                ScaleAnimation scaleAnim = new ScaleAnimation(0.0f, 1.4f, 0.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotateAnim = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                AnimationSet setAnim = new AnimationSet(true);
                setAnim.addAnimation(alphaAnim);
                setAnim.addAnimation(scaleAnim);
                setAnim.addAnimation(rotateAnim);
                setAnim.setDuration(3000);
                setAnim.setFillAfter(true);
                tvTest.startAnimation(setAnim);
                break;
        }
    }
}












