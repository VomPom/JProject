package com.vompom.media.codec.v2.player

import android.view.Surface
import com.vompom.media.codec.v2.docode.model.Asset

/**
 *
 * Created by @juliswang on 2025/09/28 20:08
 *
 * @Description 定义播放器提供的接口
 */

interface IPlayer {
    fun bindPlayer(videoList: List<Asset>, surface: Surface)
    fun play()
    fun pause()
    fun seekTo(positionUs: Long)
    fun stop()
    fun release()
    fun duration(): Long
    fun setLoop(loop: Boolean)
    fun setPlayerListener(listener: PlayerListener)
    interface PlayerListener {

        fun onPositionChanged(currentDurationUs: Long, playerDurationUs: Long)
    }
}