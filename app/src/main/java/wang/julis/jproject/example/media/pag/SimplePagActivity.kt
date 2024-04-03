package wang.julis.jproject.example.media.pag

import android.app.Activity
import android.content.pm.PackageManager
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.julis.wang.R
import org.libpag.PAGFile
import org.libpag.PAGView
import wang.julis.jwbase.basecompact.BaseActivity

/**
 * Created by juliswang on 2023/7/5 15:28
 *
 * @Description
 */
class SimplePagActivity : BaseActivity(), View.OnClickListener {
    private lateinit var pagView: PAGView
    private lateinit var pagFile: PAGFile

    override fun initView() {
        initPAGView()
        initListener()
    }

    override fun initData() {
        pagFile = PAGFile.Load(assets, "pag/PAG_LOGO.pag")
    }

    override fun getContentView(): Int {
        return R.layout.activity_simple_pag_activity
    }

    private fun initPAGView() {
        pagView = PAGView(this).apply {
            composition = pagFile
            setRepeatCount(0)
        }
        addContentView(
            pagView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        pagView.play()
    }

    private fun initListener() {
        findViewById<Button>(R.id.btn_play_pause).setOnClickListener(this)
        findViewById<Button>(R.id.btn_export_mp4).setOnClickListener(this)
        findViewById<Button>(R.id.btn_replace_image).setOnClickListener(this)
        findViewById<Button>(R.id.btn_replace_text).setOnClickListener(this)
    }

    private fun pausePlay() {
        if (pagView.isPlaying) {
            pagView.stop()
        } else {
            pagView.play()
        }
    }

    private fun exportMp4() {
        verifyStoragePermissions(this)
        PAGExportHelper(pagFile).pagExportToMP4()
    }


    private fun verifyStoragePermissions(activity: Activity) {
        val permissions = arrayOf(
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
        )
        try {
            val permission = ActivityCompat.checkSelfPermission(
                activity,
                "android.permission.WRITE_EXTERNAL_STORAGE"
            )
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    activity,
                    permissions,
                    1
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun replaceImage() {

    }

    private fun replaceText() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_play_pause -> {
                pausePlay()
                return
            }
            R.id.btn_export_mp4 -> {
                exportMp4()
                return
            }
            R.id.btn_replace_image -> {
                replaceImage()
                return
            }
            R.id.btn_replace_text -> {
                replaceText()
                return
            }

        }
    }

}