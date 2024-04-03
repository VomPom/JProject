package wang.julis.jproject.example.anim;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.julis.wang.R;

import wang.julis.jwbase.basecompact.BaseActivity;


/**
 * Created by julis.wang on 2018/10/9 星期二.
 * Project:Intent
 * Description:
 */

public class ValueAnimatorActivity extends BaseActivity implements View.OnClickListener {
    private Button btnTest;
    private Button btnOfObject;
    private Button btnViewProperty;
    private Button btnCancel;
    private TextView tvText;
    private ValueAnimator valueAnimator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("属性动画");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        btnTest = findViewById(R.id.btn_test);
        btnTest.setOnClickListener(this);
        btnOfObject = findViewById(R.id.btn_of_object);
        btnOfObject.setOnClickListener(this);
        btnViewProperty = findViewById(R.id.view_property);
        btnViewProperty.setOnClickListener(this);
        tvText = findViewById(R.id.tv_test);
        tvText.setOnClickListener(this);
        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_value_animator;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_test:
                doAnimationByOfInt();
                break;
            case R.id.btn_of_object:
                doAnimationByOfObject();
                break;
            case R.id.tv_test:
                Toast.makeText(this,"点击了Textview",Toast.LENGTH_LONG).show();
                break;
            case R.id.view_property:
                tvText.animate()
                        .alpha(0.7f)
                        .translationX(100)
                        .translationY(50)
                        .rotation(90)
                        .scaleX(1.4f)
                        .setDuration(3000)
                        .start();
                break;
            case R.id.btn_cancel:
                Toast.makeText(this,"动画取消",Toast.LENGTH_LONG).show();
                valueAnimator.removeAllListeners();
                valueAnimator.cancel();
                break;
        }
    }

    public void doAnimationByOfInt(){
        valueAnimator = ValueAnimator.ofInt(0,400,0);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(3);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new BounceInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int curValue = (int)animation.getAnimatedValue();
                tvText.layout(curValue,curValue,curValue+tvText.getWidth(),curValue+tvText.getHeight());
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Toast.makeText(getBaseContext(),"动画开始",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Toast.makeText(getBaseContext(),"动画结束",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                Toast.makeText(getBaseContext(),"动画取消",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Toast.makeText(getBaseContext(),"动画重复",Toast.LENGTH_SHORT).show();
            }
        });
        valueAnimator.start();
    }
    public void doAnimationByOfObject(){
        valueAnimator = ValueAnimator.ofObject(new CharEvaluator(),new Character('A'),new Character('Z'));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                char c = (char)animation.getAnimatedValue();
                tvText.setText(String.valueOf(c));
            }
        });
        valueAnimator.setDuration(5000);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setRepeatCount(3);
        valueAnimator.start();
    }
}
