package com.vompom.media.codec.v2.utils

import wang.julis.jwbase.utils.Logger

/**
 *
 * Created by @juliswang on 2025/09/25 20:54
 *
 * @Description
 */

object VLog {
    private var level: Int = Logger.LogLevel.VERBOSE.ordinal

    fun setLogLevel(level: Logger.LogLevel) {
        this.level = level.ordinal
    }


    fun i(msg: String) {
        if (level < Logger.LogLevel.INFO.ordinal) {
            Logger.i(msg)
        }
    }

    fun i(tag: String, msg: String) {
        if (level < Logger.LogLevel.INFO.ordinal) {
            Logger.i(tag, msg)
        }
    }

    fun d(msg: String) {
        if (level < Logger.LogLevel.DEBUG.ordinal) {
            Logger.d(msg)
        }
    }

    fun d(tag: String, msg: String) {
        if (level < Logger.LogLevel.DEBUG.ordinal) {
            Logger.d(tag, msg)
        }
    }

    fun e(msg: String) {
        if (level < Logger.LogLevel.ERROR.ordinal) {
            Logger.e(msg)
        }
    }


}