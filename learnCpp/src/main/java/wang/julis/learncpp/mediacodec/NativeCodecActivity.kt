package wang.julis.learncpp.mediacodec

import android.content.res.AssetManager
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.*
import wang.julis.jwbase.basecompact.BaseActivity
import wang.julis.learncpp.R


/*********************************************************
 * Created by juliswang on 2022/8/12 10:39
 *
 * Description :
 *
 *
 *********************************************************/

class NativeCodecActivity : BaseActivity() {
    val TAG = "NativeCodec"
    private lateinit var mSurfaceHolder1: SurfaceHolder
    private lateinit var mGLView1: MyGLSurfaceView
    private var mRadio1: RadioButton? = null
    private var mRadio2: RadioButton? = null

    private var mSourceString: String? = null
    private var mSurfaceView1: SurfaceView? = null
    private var mSelectedVideoSink: VideoSink? = null
    private var mNativeCodecPlayerVideoSink: VideoSink? = null
    private var mSurfaceHolder1VideoSink: SurfaceHolderVideoSink? = null
    private var mGLView1VideoSink: GLViewVideoSink? = null
    private var mCreated = false
    private var mIsPlaying = false

    /** Load jni .so on initialization */

    init {
        System.loadLibrary("native-codec-jni")
    }

    companion object {
        /** Native methods, implemented in jni folder  */
        external fun createStreamingMediaPlayer(assetMgr: AssetManager?, filename: String?): Boolean
        external fun setPlayingStreamingMediaPlayer(isPlaying: Boolean)
        external fun shutdown()
        external fun setSurface(surface: Surface?)
        external fun rewindStreamingMediaPlayer()
    }

    override fun initView() {
        mGLView1 = findViewById<View>(R.id.glsurfaceview1) as MyGLSurfaceView
        // set up the Surface 1 video sink
        mSurfaceView1 = findViewById<View>(R.id.surfaceview1) as SurfaceView
        mSurfaceHolder1 = mSurfaceView1!!.holder
        mSurfaceHolder1.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                Log.v(TAG, "surfaceChanged format=$format, width=$width, height=$height")
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.v(TAG, "surfaceCreated")
                if (mRadio1!!.isChecked) {
                    setSurface(holder.surface)
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.v(TAG, "surfaceDestroyed")
            }
        })

        // initialize content source spinner
        val sourceSpinner = findViewById<View>(R.id.source_spinner) as Spinner
        val sourceAdapter = ArrayAdapter.createFromResource(
            this, R.array.source_array, android.R.layout.simple_spinner_item
        )
        sourceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sourceSpinner.adapter = sourceAdapter
        sourceSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                mSourceString = parent.getItemAtPosition(pos).toString()
                Log.v(TAG, "onItemSelected $mSourceString")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.v(TAG, "onNothingSelected")
                mSourceString = null
            }
        }
        mRadio1 = findViewById(R.id.radio1)
        mRadio2 = findViewById(R.id.radio2)
        val checkListener: CompoundButton.OnCheckedChangeListener =
            CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                if (buttonView === mRadio1 && isChecked) {
                    mRadio2!!.isChecked = false
                }
                if (buttonView === mRadio2 && isChecked) {
                    mRadio1!!.isChecked = false
                }
                if (isChecked) {
                    if (mRadio1!!.isChecked) {
                        if (mSurfaceHolder1VideoSink == null) {
                            mSurfaceHolder1VideoSink = SurfaceHolderVideoSink(mSurfaceHolder1)
                        }
                        mSelectedVideoSink = mSurfaceHolder1VideoSink
                        mGLView1.onPause()
                    } else {
                        mGLView1.onResume()
                        if (mGLView1VideoSink == null) {
                            mGLView1VideoSink = GLViewVideoSink(mGLView1)
                        }
                        mSelectedVideoSink = mGLView1VideoSink
                    }
                    switchSurface()
                }
            }
        mRadio1!!.setOnCheckedChangeListener(checkListener)
        mRadio2!!.setOnCheckedChangeListener(checkListener)
        mRadio2!!.toggle()

        // the surfaces themselves are easier targets than the radio buttons
        mSurfaceView1!!.setOnClickListener { mRadio1!!.toggle() }
        mGLView1.setOnClickListener { mRadio2!!.toggle() }

        // initialize button click handlers
        // native MediaPlayer start/pause
        (findViewById<View>(R.id.start_native) as Button).setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                if (!mCreated) {
                    if (mNativeCodecPlayerVideoSink == null) {
                        if (mSelectedVideoSink == null) {
                            return
                        }
                        mSelectedVideoSink!!.useAsSinkForNative()
                        mNativeCodecPlayerVideoSink = mSelectedVideoSink
                    }
                    if (mSourceString != null) {
                        mCreated = createStreamingMediaPlayer(
                            resources.assets,
                            mSourceString
                        )
                    }
                }
                if (mCreated) {
                    mIsPlaying = !mIsPlaying
                    setPlayingStreamingMediaPlayer(mIsPlaying)
                }
            }
        })

        // native MediaPlayer rewind
        (findViewById<View>(R.id.rewind_native) as Button).setOnClickListener {
            if (mNativeCodecPlayerVideoSink != null) {
                rewindStreamingMediaPlayer()
            }
        }
    }

    private fun switchSurface() {
        if (mCreated && mNativeCodecPlayerVideoSink !== mSelectedVideoSink) {
            // shutdown and recreate on other surface
            Log.i("@@@", "shutting down player")
            shutdown()
            mCreated = false
            mSelectedVideoSink!!.useAsSinkForNative()
            mNativeCodecPlayerVideoSink = mSelectedVideoSink
            if (mSourceString != null) {
                Log.i("@@@", "recreating player")
                mCreated = createStreamingMediaPlayer(resources.assets, mSourceString)
                mIsPlaying = false
            }
        }
    }

    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_native_mediacodec
    }


    /** Called when the activity is about to be paused.  */
    override fun onPause() {
        mIsPlaying = false
        setPlayingStreamingMediaPlayer(false)
        mGLView1.onPause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        if (mRadio2!!.isChecked) {
            mGLView1.onResume()
        }
    }

    /** Called when the activity is about to be destroyed.  */
    override fun onDestroy() {
        shutdown()
        mCreated = false
        super.onDestroy()
    }


    // VideoSink abstracts out the difference between Surface and SurfaceTexture
    // aka SurfaceHolder and GLSurfaceView
    abstract class VideoSink {
        abstract fun setFixedSize(width: Int, height: Int)
        abstract fun useAsSinkForNative()
    }

    class SurfaceHolderVideoSink(private val mSurfaceHolder: SurfaceHolder?) : VideoSink() {
        override fun setFixedSize(width: Int, height: Int) {
            mSurfaceHolder!!.setFixedSize(width, height)
        }

        override fun useAsSinkForNative() {
            val s: Surface = mSurfaceHolder!!.surface
            Log.i("@@@", "setting surface $s")
            setSurface(s)
        }
    }

    class GLViewVideoSink(myGLSurfaceView: MyGLSurfaceView?) : VideoSink() {
        private val mMyGLSurfaceView: MyGLSurfaceView?
        override fun setFixedSize(width: Int, height: Int) {}
        override fun useAsSinkForNative() {
            val st: SurfaceTexture? = mMyGLSurfaceView?.getSurfaceTexture()
            val s = Surface(st)
            setSurface(s)
            s.release()
        }

        init {
            mMyGLSurfaceView = myGLSurfaceView
        }
    }


}