package wang.julis.jproject.example.Anim;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.julis.distance.R;

import wang.julis.jwbase.basecompact.BaseActivity;


/**
 * Created by julis.wang on 2018/10/10 星期三.
 * Project:Intent
 * Description:
 */
public class AnimatorSetActivity extends BaseActivity implements View.OnClickListener {

    private Button mMenuButton;
    private Button mItemButton1;
    private Button mItemButton2;
    private Button mItemButton3;
    private Button mItemButton4;
    private Button mItemButton5;
    private boolean openFlag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTitle("联合动画效果");
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        mMenuButton = (Button) findViewById(R.id.menu);
        mMenuButton.setOnClickListener(this);

        mItemButton1 = (Button) findViewById(R.id.item1);
        mItemButton1.setOnClickListener(this);

        mItemButton2 = (Button) findViewById(R.id.item2);
        mItemButton2.setOnClickListener(this);

        mItemButton3 = (Button) findViewById(R.id.item3);
        mItemButton3.setOnClickListener(this);

        mItemButton4 = (Button) findViewById(R.id.item4);
        mItemButton4.setOnClickListener(this);

        mItemButton5 = (Button) findViewById(R.id.item5);
        mItemButton5.setOnClickListener(this);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_animator_set;
    }

    @Override
    public void onClick(View v) {
        if(v==mMenuButton){
            if(!openFlag){
                openFlag = true;
                menuDoAnimation(-90);
                doAnimation(mItemButton1,0);
                doAnimation(mItemButton2,1);
                doAnimation(mItemButton3,2);
                doAnimation(mItemButton4,3);
                doAnimation(mItemButton5,4);
            }else{
                openFlag = false;
                menuDoAnimation(90);
                doCloseAnimation(mItemButton1,0);
                doCloseAnimation(mItemButton2,1);
                doCloseAnimation(mItemButton3,2);
                doCloseAnimation(mItemButton4,3);
                doCloseAnimation(mItemButton5,4);
            }

        }

    }
    public void menuDoAnimation(int translateX){
        ObjectAnimator animator = ObjectAnimator.ofFloat(mMenuButton,"rotation",0,translateX);
        animator.setDuration(500);
        animator.start();
    }

    public void doAnimation(Button button,int index){
        if (button.getVisibility() != View.VISIBLE) {
            button.setVisibility(View.VISIBLE);
        }
        int radius = 300;
        int total = 5;
        double degree = Math.toRadians(90)/(total - 1) * index;
        int translationX = -(int) (radius * Math.sin(degree));
        int translationY = -(int) (radius * Math.cos(degree));


        AnimatorSet set = new AnimatorSet();
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(button, "translationX", 0, translationX),
                ObjectAnimator.ofFloat(button, "translationY", 0, translationY),
                ObjectAnimator.ofFloat(button, "scaleX", 0f, 1f),
                ObjectAnimator.ofFloat(button, "scaleY", 0f, 1f),
                ObjectAnimator.ofFloat(button, "alpha", 0f, 1));
        //动画周期为500ms
        set.setDuration(1 * 500).start();
        set.setStartDelay(100*index);


    }
    public void doCloseAnimation(Button button,int index){
        if (button.getVisibility() != View.VISIBLE) {
            button.setVisibility(View.VISIBLE);
        }
        int radius = 300;
        int total = 5;
        double degree = Math.toRadians(90)/(total - 1) * index;
        int translationX = -(int) (radius * Math.sin(degree));
        int translationY = -(int) (radius * Math.cos(degree));


        AnimatorSet set = new AnimatorSet();
        //包含平移、缩放和透明度动画
        set.playTogether(
                ObjectAnimator.ofFloat(button, "translationX", translationX, 0),
                ObjectAnimator.ofFloat(button, "translationY", translationY, 0),
                ObjectAnimator.ofFloat(button, "scaleX", 1f, 0f),
                ObjectAnimator.ofFloat(button, "scaleY", 1f, 0f),
                ObjectAnimator.ofFloat(button, "alpha", 1f, 0));
        //动画周期为500ms
        set.setDuration(1 * 500).start();
        set.setStartDelay(100*index);

    }
}
















