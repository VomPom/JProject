package wang.julis.jproject.Little;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.julis.distance.R;

import wang.julis.jwbase.basecompact.BaseActivity;


public class RectVisibleActivity extends BaseActivity {
    private int lastX = 0;
    private int lastY = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        final ImageView imageView = (ImageView) findViewById(R.id.img);

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Rect localRect = new Rect();
//                imageView.getLocalVisibleRect(localRect); //getLocalVisibleRect(Rect r)方法可以把视图的长和宽映射到一个Rect对象上。
//                Toast.makeText(getBaseContext(),"Local:" + String.valueOf(localRect)
//                        +"\ntop:"+localRect.top
//                        +"\nbottom:"+localRect.bottom
//                        +"\nleft:"+localRect.left
//                        +"\nright:"+localRect.right,Toast.LENGTH_SHORT).show();
//                Log.e("julis","local" + localRect);
//            }
//        });
//        imageView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Rect localRect = new Rect();
//                Point globalOffset = new Point();
//                imageView.getGlobalVisibleRect(localRect,globalOffset);
//                //getGlobalVisibleRect方法的作用是获取视图在屏幕坐标系中的偏移量
//                //getGlobalVisibleRect方法的作用是获取视图在屏幕坐标中的可视区域
//                //globalOffset的值就是targetView原点偏离屏幕坐标原点的距离
//                Toast.makeText(getBaseContext(),"Global:" + String.valueOf(localRect)
//                        +"\ntop:"+localRect.top
//                        +"\nbottom:"+localRect.bottom
//                        +"\nleft:"+localRect.left
//                        +"\nright:"+localRect.right
//                        +"\n\n"+globalOffset,Toast.LENGTH_LONG).show();
//                Log.e("julis","Global:" + localRect);
//                return false;
//            }
//        });
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;

                        int left = v.getLeft() + dx;
                        int top = v.getTop() + dy;
                        int right = v.getRight() + dx;
                        int bottom = v.getBottom() + dy;

                        v.layout(left, top, right, bottom);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();

                        Rect localRect = new Rect();
                        v.getLocalVisibleRect(localRect);
                        ((TextView) findViewById(R.id.local))
                                .setText("local" + localRect.toString());

                        Rect globalRect = new Rect();
                        Point globalOffset = new Point();
                        v.getGlobalVisibleRect(globalRect, globalOffset);
                        ((TextView) findViewById(R.id.global))
                                .setText("global" + globalRect.toString());
                        ((TextView) findViewById(R.id.offset))
                                .setText("globalOffset:" + globalOffset.x + "," + globalOffset.y);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_rect_visiable;
    }


}
