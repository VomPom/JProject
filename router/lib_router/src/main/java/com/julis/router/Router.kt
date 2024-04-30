package com.julis.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat

/**
 *
 * Created by @juliswang on 2024/04/28 11:12
 *
 * @Description
 */
object Router {
    private val pageMapping = ArrayList<PageInfo>()

    fun init(context: Context) {
        try {
            val mappingClass = Class.forName("${ROUTER_PACKAGE_NAME}.${ROUTER_MAPPING_NAME}")
            val mapMethod = mappingClass.getDeclaredMethod("register")
            mapMethod.invoke(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun registerPage(host: String, activity: Class<out Activity>) {
        pageMapping.add(PageInfo(host, activity))
    }


    fun open(context: Context, host: String) {
        val page = getPageInfo(host)
        if (page != null) {
            val intent = Intent(context, page.activity)
            ActivityCompat.startActivity(context, intent, Bundle())
        }
    }

    private fun getPageInfo(host: String): PageInfo? {
        return if (pageMapping.isEmpty()) {
            null
        } else {
            pageMapping.find { mapping ->
                mapping.host == host
            }
        }
    }
}