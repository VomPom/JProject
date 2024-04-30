package com.julis.router

import android.app.Activity

/**
 *
 * Created by @juliswang on 2024/04/30 11:15
 *
 * @Description
 */
internal data class PageInfo(val host: String, val activity: Class<out Activity>)