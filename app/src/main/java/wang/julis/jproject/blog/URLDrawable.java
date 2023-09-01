package wang.julis.jproject.blog;

import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Copyright (C) @2023 THL A29 Limited, a Tencent company. All rights reserved.
 * <p>
 * Created by @juliswang on 2023/09/01 10:32
 *
 * @Description
 */
public class URLDrawable extends BitmapDrawable {
    protected Drawable drawable;

    @Override
    public void draw(Canvas canvas) {
        if (drawable != null) {
            drawable.draw(canvas);
        }
    }
}