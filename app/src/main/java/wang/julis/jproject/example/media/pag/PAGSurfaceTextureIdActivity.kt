package wang.julis.jproject.example.media.pag

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.View
import com.julis.wang.R
import julis.wang.learnopengl.opengl.utils.GLUtil
import org.libpag.PAGFile
import org.libpag.PAGPlayer
import org.libpag.PAGSurface
import wang.julis.jwbase.basecompact.BaseActivity
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Created by juliswang on 2023/7/6 10:46
 *
 * @Description
 */
class PAGSurfaceTextureIdActivity : BaseActivity() {
    private var glSurfaceView: GLSurfaceView? = null
    private var glRender: GLRender? = null
    private lateinit var pagFile: PAGFile

    override fun initView() {
        glSurfaceView = findViewById<View>(R.id.gl_surfaceView) as GLSurfaceView
        glRender = GLRender(pagFile)
        glSurfaceView?.setEGLContextClientVersion(2)
        glSurfaceView?.setRenderer(glRender)
        glSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    override fun initData() {
        pagFile = PAGFile.Load(assets, "pag/PAG_LOGO.pag") // pag是 400x400 可能会变形
    }

    override fun getContentView(): Int {
        return R.layout.activity_glsurfaceview
    }


    class GLRender(private val pagFile: PAGFile) : GLSurfaceView.Renderer {

        val TAG = "GLRender"
        var textureId = 0
        var pagPlayer: PAGPlayer? = null
        var duration: Long = 0
        var mWidth = 0
        var mHeight = 0
        var timestamp: Long = 0

        private val VERTEX_MAIN = """attribute vec2  vPosition;
                            attribute vec2  vTexCoord;
                            varying vec2    texCoord;
                            
                            void main() {
                                texCoord = vTexCoord;
                                gl_Position = vec4 ( vPosition.x, vPosition.y, 0.0, 1.0 );
                            }"""

        private val FRAGMENT_MAIN = """precision mediump float;

                            varying vec2                texCoord;
                            uniform sampler2D sTexture;
                            
                            void main() {
                                gl_FragColor = texture2D(sTexture, texCoord);
                            }"""

        private val SQUARE_COORDS = floatArrayOf(
            1.0f, -1.0f,
            -1.0f, -1.0f,
            1.0f, 1.0f,
            -1.0f, 1.0f
        )

        private val TEXTURE_COORDS = floatArrayOf(
            1f, 1f,
            0f, 1f,
            1f, 0f,
            0f, 0f
        )

        private var mProgram = 0
        private var VERTEX_BUF: FloatBuffer? = null;
        private var TEXTURE_COORD_BUF: FloatBuffer? = null

        private fun initShader() {
            if (VERTEX_BUF == null) {
                VERTEX_BUF = ByteBuffer.allocateDirect(SQUARE_COORDS.size * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer()
                VERTEX_BUF!!.put(SQUARE_COORDS)
                VERTEX_BUF!!.position(0)
            }
            if (TEXTURE_COORD_BUF == null) {
                TEXTURE_COORD_BUF = ByteBuffer.allocateDirect(TEXTURE_COORDS.size * 4)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer()
                TEXTURE_COORD_BUF!!.put(TEXTURE_COORDS)
                TEXTURE_COORD_BUF!!.position(0)
            }
            if (mProgram == 0) {
                mProgram = GLUtil.buildProgram(VERTEX_MAIN, FRAGMENT_MAIN)
            }
        }


        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
            mWidth = pagFile.width()
            mHeight = pagFile.height()
            duration = pagFile.duration()
            textureId = initRenderTarget()
            val pagSurface = PAGSurface.FromTexture(textureId, mWidth, mHeight)
            pagPlayer = PAGPlayer()
            pagPlayer!!.composition = pagFile
            pagPlayer!!.surface = pagSurface
        }

        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
            GLES20.glViewport(0, 0, width, height)
            Log.d(TAG, "width is $width height is $height")
        }

        override fun onDrawFrame(gl: GL10?) {
            if (timestamp == 0L) {
                timestamp = System.currentTimeMillis()
            }
            val playTime = (System.currentTimeMillis() - timestamp) * 1000
            pagPlayer!!.progress = (playTime % duration * 1.0f / duration).toDouble()
            pagPlayer!!.flush()
            initShader()
            Log.d(TAG, "draw texture id is $textureId")
            GLES20.glUseProgram(mProgram)
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
            val vPositionLocation = GLES20.glGetAttribLocation(mProgram, "vPosition")
            GLES20.glEnableVertexAttribArray(vPositionLocation)
            GLES20.glVertexAttribPointer(vPositionLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, VERTEX_BUF)
            val vTexCoordLocation = GLES20.glGetAttribLocation(mProgram, "vTexCoord")
            GLES20.glEnableVertexAttribArray(vTexCoordLocation)
            GLES20.glVertexAttribPointer(vTexCoordLocation, 2, GLES20.GL_FLOAT, false, 4 * 2, TEXTURE_COORD_BUF)
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        }

        private fun initRenderTarget(): Int {
            val id = intArrayOf(0)
            GLES20.glGenTextures(1, id, 0)
            if (id[0] == 0) {
                return 0
            }
            val textureId = id[0]
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat())
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                mWidth,
                mHeight,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                null
            )
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            return textureId
        }
    }
}