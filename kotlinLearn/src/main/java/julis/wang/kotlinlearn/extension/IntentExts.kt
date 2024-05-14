package julis.wang.kotlinlearn.extension

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment

/**
 *
 * Created by @juliswang on 2024/05/14 10:52
 *
 * @Description 一种简单的方式解析 intent数据
 */

inline fun <reified T> ComponentActivity.intentData(key: String) = lazy {
    r<T>(intent, key)
}

inline fun <reified T> ComponentActivity.intentData(key: String, default: T = Any() as T) = lazy {
    r<T>(intent, key) ?: default
}

inline fun <reified T> Fragment.intentData(key: String) = lazy {
    r<T>(activity?.intent, key)
}


inline fun <reified T> Fragment.intentData(key: String, default: T = Any() as T) = lazy {
    r<T>(activity?.intent, key) ?: default
}

//inline fun <reified T> g(intent: Intent?, key: String) =
//    lazy {
//        r<T>(intent, key)
//    }
//
//inline fun <reified T> g(intent: Intent?, key: String, default: T) =
//    lazy {
//        r<T>(intent, key) ?: default
//    }

inline fun <reified R> r(intent: Intent?, key: String): R? {
    if (intent == null) {
        return null
    }
    var r: R?
    when (R::class) {
        Byte::class -> r = intent.getByteExtra(key, 0) as R?
        Boolean::class -> r = intent.getBooleanExtra(key, false) as R?
        Char::class -> r = intent.getCharExtra(key, ' ') as R?
        Short::class -> r = intent.getShortExtra(key, 0) as R?
        Int::class -> r = intent.getIntExtra(key, 0) as R?
        Float::class -> r = intent.getFloatExtra(key, 0f) as R?
        Double::class -> r = intent.getDoubleExtra(key, 0.0) as R?
        Long::class -> r = intent.getLongExtra(key, 0) as R?
        String::class -> r = intent.getStringExtra(key) as R?
        Bundle::class -> r = intent.getBundleExtra(key) as R?
        Parcelable::class -> r = intent.getParcelableArrayListExtra<Parcelable>(key) as R?
        //... wait for business add.
        else -> {
            throw RuntimeException("intentData extension not support this type data yet, please fix it.")
        }
    }
    return r
}

