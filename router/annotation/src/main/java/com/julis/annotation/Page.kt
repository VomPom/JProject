package com.julis.annotation

/**
 * Created by @juliswang on 2024/04/28 11:25
 *
 * @Description
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Page(val value: String)