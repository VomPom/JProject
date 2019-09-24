package wang.julis.jproject.Anim;

import android.content.Context;
import android.util.AttributeSet;


/**
 * Created by julis.wang on 2018/10/10 星期三.
 * Project:Intent
 * Description:
 */
public class MyTextView extends android.support.v7.widget.AppCompatTextView{


    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCharText(Character character){
        setText(String.valueOf(character));
    }
}

