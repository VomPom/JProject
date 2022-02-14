package julis.wang.learnopengl.opengl.egl;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import julis.wang.learnopengl.R;

/*******************************************************
 *
 * Created by julis.wang on 2022/02/11 11:43
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class EGLActivity extends AppCompatActivity {
    private static final String TAG = "EGLActivity";
    public static final int PARAM_TYPE_SHADER_INDEX = 200;
    private static final float IMG_WIDTH = 853.0f;
    private static final float IMG_HEIGHT = 1280.0f;
    private ImageView mImageView;
    private Button mBtn;
    private NativeEglRender mBgRender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_egl);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mBtn = (Button) findViewById(R.id.button);
        mBgRender = new NativeEglRender();
        mBgRender.native_EglRenderInit();

        mBtn.setOnClickListener(v -> {
            if (mBtn.getText().equals("重制")) {
                mImageView.setImageResource(R.drawable.person);
                mBtn.setText("后台渲染");
            } else {
                startBgRender();
                mBtn.setText("重制");
            }
        });
        int width = getScreenWidth();
        int height = (int) (width * (IMG_HEIGHT / IMG_WIDTH));
        mImageView.setLayoutParams(new WindowManager.LayoutParams(width, height));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBgRender.native_EglRenderUnInit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_egl, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        int shaderIndex = 0;
        if (id == R.id.action_shader0) {
            shaderIndex = 0;
        } else if (id == R.id.action_shader1) {
            shaderIndex = 1;
        } else if (id == R.id.action_shader2) {
            shaderIndex = 2;
        } else if (id == R.id.action_shader3) {
            shaderIndex = 3;
        } else if (id == R.id.action_shader4) {
            shaderIndex = 4;
        } else if (id == R.id.action_shader5) {
            shaderIndex = 5;
        } else if (id == R.id.action_shader6) {
            shaderIndex = 6;
        } else if (id == R.id.action_shader7) {
            shaderIndex = 7;
        }

        if (mBgRender != null) {
            mBgRender.native_EglRenderSetIntParams(PARAM_TYPE_SHADER_INDEX, shaderIndex);
            startBgRender();
            mBtn.setText("重制");
        }
        return true;
    }

    private void startBgRender() {
        loadRGBAImage(R.drawable.person, mBgRender);
        mBgRender.native_EglRenderDraw();

        mImageView.setImageBitmap(createBitmapFromGLSurface(0, 0, (int) IMG_WIDTH, (int) IMG_HEIGHT));
    }

    private int getScreenWidth() {
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    private void loadRGBAImage(int resId, NativeEglRender render) {
        InputStream is = this.getResources().openRawResource(resId);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
            if (bitmap != null) {
                int bytes = bitmap.getByteCount();
                ByteBuffer buf = ByteBuffer.allocate(bytes);
                bitmap.copyPixelsToBuffer(buf);
                byte[] byteArray = buf.array();
                render.native_EglRenderSetImageData(byteArray, bitmap.getWidth(), bitmap.getHeight());
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Bitmap createBitmapFromGLSurface(int x, int y, int w, int h) {
        int bitmapBuffer[] = new int[w * h];
        int bitmapSource[] = new int[w * h];
        IntBuffer intBuffer = IntBuffer.wrap(bitmapBuffer);
        intBuffer.position(0);
        try {
            GLES20.glReadPixels(x, y, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                    intBuffer);
            int offset1, offset2;
            for (int i = 0; i < h; i++) {
                offset1 = i * w;
                offset2 = (h - i - 1) * w;
                for (int j = 0; j < w; j++) {
                    int texturePixel = bitmapBuffer[offset1 + j];
                    int blue = (texturePixel >> 16) & 0xff;
                    int red = (texturePixel << 16) & 0x00ff0000;
                    int pixel = (texturePixel & 0xff00ff00) | red | blue;
                    bitmapSource[offset2 + j] = pixel;
                }
            }
        } catch (GLException e) {
            return null;
        }
        return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888);
    }
}
