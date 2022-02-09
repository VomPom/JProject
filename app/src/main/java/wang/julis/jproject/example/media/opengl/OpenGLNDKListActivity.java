package wang.julis.jproject.example.media.opengl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.julis.distance.R;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import wang.julis.jproject.example.media.opengl.glitem.GLItemListAdapter;
import wang.julis.jproject.example.media.opengl.glitem.GLItemListModel;
import wang.julis.jwbase.basecompact.BaseActivity;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;
import static wang.julis.jproject.example.media.opengl.MyGLSurfaceView.IMAGE_FORMAT_RGBA;
import static wang.julis.jproject.example.media.opengl.MyNativeRender.SAMPLE_TYPE_TEXTURE_MAP;
import static wang.julis.jproject.example.media.opengl.MyNativeRender.SAMPLE_TYPE_TRIANGLE;
import static wang.julis.jproject.example.media.opengl.MyNativeRender.SAMPLE_TYPE_YUV_TEXTURE_MAP;


/*******************************************************
 *
 * Created by julis.wang on 2022/02/09 15:27
 *
 * Description : NDK 实现OpenGL ES相关效果
 *
 *
 * History   :
 *
 *******************************************************/

public class OpenGLNDKListActivity extends BaseActivity {

    private MyGLSurfaceView mGLSurfaceView;
    private FrameLayout mContainer;
    protected GLItemListAdapter mAdapter;
    protected final List<GLItemListModel> mDataList = new ArrayList<>();

    private int index = 0;

    private final MyGLRender mGLRender = new MyGLRender();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGLRender.init();
    }


    @Override
    protected void initView() {
        mContainer = findViewById(R.id.fl_surface_container);
        RecyclerView rvList = findViewById(R.id.rv_list);

        mAdapter = new GLItemListAdapter(this);
        rvList.setAdapter(mAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(this));

        ImageView ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(v -> {
            mContainer.removeView(mGLSurfaceView);
            ivClose.setVisibility(View.GONE);
        });

        mAdapter.setItemListener(index -> {
            ivClose.setVisibility(View.VISIBLE);
            onRenderChange(index);
        });
    }

    private void onRenderChange(int index) {
        int SAMPLE_TYPE = 200;
        int sampleType = index + SAMPLE_TYPE;
        mGLRender.setParamsInt(SAMPLE_TYPE, index + SAMPLE_TYPE, 0);

        mGLSurfaceView = new MyGLSurfaceView(this, mGLRender);
        mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);

        mContainer.addView(mGLSurfaceView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));


        switch (sampleType) {
            case SAMPLE_TYPE_TRIANGLE:
                break;
            case SAMPLE_TYPE_TEXTURE_MAP:
                loadRGBAImage(R.drawable.julis);
                break;
            case SAMPLE_TYPE_YUV_TEXTURE_MAP:
                break;
        }

        mGLSurfaceView.requestRender();
    }

    @Override
    protected void initData() {
        Arrays.stream(SAMPLE_TITLES).forEach(this::addItem);
        submitActivityList();
    }

    protected void addItem(String activityName) {
        mDataList.add(new GLItemListModel(activityName, index++));
    }

    protected void submitActivityList() {
        mAdapter.updateData(mDataList);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_opengl_list;
    }

    private Bitmap loadRGBAImage(int resId) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buf);
                byte[] byteArray = buf.array();
                mGLRender.setImageData(IMAGE_FORMAT_RGBA, bitmap.getWidth(), bitmap.getHeight(), byteArray);
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    private static final String[] SAMPLE_TITLES = {
            "基础三角形",
            "纹理映射",
            "YUV Rendering",
            "VAO&VBO",
            "FBO Offscreen Rendering",
            "EGL Background Rendering",
            "FBO Stretching",
            "Coordinate System",
            "Basic Lighting",
            "Transform Feedback",
            "Complex Lighting",
            "Depth Testing",
            "Instancing",
            "Stencil Testing",
            "Blending",
            "Particles",
            "SkyBox",
            "Assimp Load 3D Model",
            "PBO",
            "Beating Heart",
            "Cloud",
            "Time Tunnel",
            "Bezier Curve",
            "Big Eyes",
            "Face Slender",
            "Big Head",
            "Rotary Head",
            "Visualize Audio",
            "Scratch Card",
            "3D Avatar",
            "Shock Wave",
            "MRT",
            "FBO Blit",
            "Texture Buffer",
            "Uniform Buffer",
            "RGB to YUYV",
            "Multi-Thread Render",
            "Text Render",
            "Portrait stay color",
            "GL Transitions_1",
            "GL Transitions_2",
            "GL Transitions_3",
            "GL Transitions_4",
            "RGB to NV21",
            "RGB to I420",
    };

}
