package julis.wang.learnopengl.opengl.view2gl.gl;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Copyright (C) @2023 THL A29 Limited, a Tencent company. All rights reserved.
 * <p>
 * Created by juliswang on 2023/2/27 16:28
 *
 * @Description
 */
public class AnimationView extends BaseGLTextureView implements GLViewRenderer {

    public AnimationView(Context context) {
        this(context, null);
    }

    public AnimationView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public AnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void onSurfaceCreated() {
//        renderer.onSurfaceCreated();
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
//        renderer.onSurfaceChanged(width, height);
    }

    @Override
    public void onDrawFrame() {
//        renderer.onDrawFrame();
    }

}
