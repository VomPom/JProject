package com.vompom.media.codec.v2.utils

import wang.julis.jwbase.utils.Logger

/**
 *
 * Created by @juliswang on 2025/09/25 20:54
 *
 * @Description
 */

object VLog {
    private var level: Int = Logger.LogLevel.DEBUG.ordinal

    fun setLogLevel(level: Logger.LogLevel) {
        this.level = level.ordinal
    }


    fun v(msg: String) {
        if (level <= Logger.LogLevel.VERBOSE.ordinal) {
            Logger.i(msg)
        }
    }

    fun v(tag: String, msg: String) {
        if (level <= Logger.LogLevel.VERBOSE.ordinal) {
            Logger.i(tag, msg)
        }
    }

    fun d(msg: String) {
        if (level <= Logger.LogLevel.DEBUG.ordinal) {
            Logger.d(msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (level <= Logger.LogLevel.DEBUG.ordinal) {
            Logger.d(tag, msg)
        }
    }

    fun e(msg: String) {
        if (level <= Logger.LogLevel.ERROR.ordinal) {
            Logger.e(msg)
        }
    }


}