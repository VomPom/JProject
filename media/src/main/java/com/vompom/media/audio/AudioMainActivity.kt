package com.vompom.media.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import com.vompom.media.utils.ResUtils
import wang.julis.jwbase.basecompact.baseList.BaseListActivity
import wang.julis.jwbase.utils.Logger
import java.io.File

/**
 *
 * Created by @juliswang on 2025/11/13 10:26
 *
 * @Description
 */

class AudioMainActivity : BaseListActivity() {
    override fun initData() {
        addItem("AudioRecord/Track", AudioRecordTrackActivity::class.java)
        addItem("PCM转WAV") { pcmToWav() }
    }

    @SuppressLint("SetTextI18n")
    private fun pcmToWav() {
        val pcmToWavUtil = PcmToWavUtil(
            44100,
            AudioFormat.CHANNEL_IN_STEREO,
            AudioFormat.ENCODING_PCM_8BIT
        )
        val sourcePath = ResUtils.pcm44_1k32bit
        val savePath = ResUtils.createSavePath("${File(sourcePath).name}.wav")
        pcmToWavUtil.pcmToWav(sourcePath, savePath)
        Logger.d("PCM to WAV success, save path: $savePath")
    }

}
