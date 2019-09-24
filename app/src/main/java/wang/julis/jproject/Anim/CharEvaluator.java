package wang.julis.jproject.Anim;

import android.animation.TypeEvaluator;

/**
 * Created by julis.wang on 2018/10/9 星期二.
 * Project:Intent
 * Description:
 */
public class CharEvaluator implements TypeEvaluator<Character> {

    @Override
    public Character evaluate(float fraction, Character startValue, Character endValue) {
        int startInt  = (int)startValue;
        int endInt = (int)endValue;
        int curInt = (int)(startInt + fraction *(endInt - startInt));
        char result = (char)curInt;
        return result;

    }
}
