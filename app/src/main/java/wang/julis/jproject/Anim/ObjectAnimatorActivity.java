package wang.julis.jproject.Anim;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.julis.distance.R;

import wang.julis.jwbase.basecompact.BaseActivity;


/**
 * Created by julis.wang on 2018/10/9 星期二.
 * Project:Intent
 * Description:
 */
public class ObjectAnimatorActivity extends BaseActivity implements View.OnClickListener {
    private Button btnStart;

    private Button btnRotationX;
    private Button btnRotationY;
    private Button btnTranslateX;
    private Button btnTranslateY;
    private Button btnScaleX;
    private Button btnScaleY;
    private Button btnPropertyHolder;
    private Button btnKeyFrame;


    private Button btnCancel;
    private MyTextView textView;
    private ObjectAnimator objectAnimator;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("ObjectAnimator");
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initView() {
        btnStart = findViewById(R.id.btn_rotation);
        btnStart.setOnClickListener(this);

        btnRotationX = findViewById(R.id.btn_rotationX);
        btnRotationX.setOnClickListener(this);
        btnRotationY = findViewById(R.id.btn_rotationY);
        btnRotationY.setOnClickListener(this);

        btnTranslateX = findViewById(R.id.btn_translationX);
        btnTranslateX.setOnClickListener(this);
        btnTranslateY = findViewById(R.id.btn_translationY);
        btnTranslateY.setOnClickListener(this);

        btnScaleX = findViewById(R.id.btn_scaleX);
        btnScaleX.setOnClickListener(this);
        btnScaleY = findViewById(R.id.btn_scaleY);
        btnScaleY.setOnClickListener(this);

        btnPropertyHolder = findViewById(R.id.btn_PropertyValuesHolder);
        btnPropertyHolder.setOnClickListener(this);

        btnKeyFrame = findViewById(R.id.btn_keyframe);
        btnKeyFrame.setOnClickListener(this);

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        textView = findViewById(R.id.tv_test);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_object_animator;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_rotation:
                doObjectAnimator("rotation");
                break;
            case R.id.btn_rotationX:
                doObjectAnimator("rotationX");
                break;
            case R.id.btn_rotationY:
                doObjectAnimator("rotationY");
                break;
            case R.id.btn_translationX:
                doObjectAnimator("translationX");
                break;
            case R.id.btn_translationY:
                doObjectAnimator("translationY");
                break;
            case R.id.btn_scaleX:
                doObjectAnimator("scaleX");
                break;
            case R.id.btn_scaleY:
                doObjectAnimator("scaleY");
                break;
            case R.id.btn_PropertyValuesHolder:
                doObjectAnimator("");
                break;
            case R.id.btn_keyframe:
                doObjectAnimator("keyframe");
                break;

            case R.id.btn_cancel:
                objectAnimator.removeAllUpdateListeners();
                objectAnimator.cancel();
                break;
        }
    }
    public void doObjectAnimator(String propertyName){
        if("scaleX".equals(propertyName) || "scaleY".equals(propertyName)){
            objectAnimator = ObjectAnimator.ofFloat(textView,propertyName,1,3,1);
        }else if("".equals(propertyName)){
            PropertyValuesHolder rotationHolder = PropertyValuesHolder.ofFloat("Rotation", 60f, -60f, 40f, -40f, -20f, 20f, 10f, -10f, 0f);
            PropertyValuesHolder colorHolder = PropertyValuesHolder.ofInt("BackgroundColor", 0xffffffff, 0xffff00ff, 0xffffff00, 0xffffffff);
            PropertyValuesHolder charHolder = PropertyValuesHolder.ofObject("CharText",new CharEvaluator(),new Character('A'),new Character('Z'));
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(textView,rotationHolder,colorHolder,charHolder);
        }else if("keyframe".equals(propertyName)){
            Keyframe frame0 = Keyframe.ofFloat(0f, 0);
            Keyframe frame1 = Keyframe.ofFloat(0.1f, -20f);
            Keyframe frame2 = Keyframe.ofFloat(0.2f, 20f);
            Keyframe frame3 = Keyframe.ofFloat(0.3f, -20f);
            Keyframe frame4 = Keyframe.ofFloat(0.4f, 20f);
            Keyframe frame5 = Keyframe.ofFloat(0.5f, -20f);
            Keyframe frame6 = Keyframe.ofFloat(0.6f, 20f);
            Keyframe frame7 = Keyframe.ofFloat(0.7f, -20f);
            Keyframe frame8 = Keyframe.ofFloat(0.8f, 20f);
            Keyframe frame9 = Keyframe.ofFloat(0.9f, -20f);
            Keyframe frame10 = Keyframe.ofFloat(1, 0);
            PropertyValuesHolder frameHolder = PropertyValuesHolder.ofKeyframe("rotation",frame0,frame1,frame2,frame3,frame4,frame5,frame6,frame7,frame8,frame9,frame10);
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(textView,frameHolder);
        } else{
            objectAnimator = ObjectAnimator.ofFloat(textView,propertyName,0,180,0);

        }

        objectAnimator.setDuration(3000);
        objectAnimator.start();
    }
}
