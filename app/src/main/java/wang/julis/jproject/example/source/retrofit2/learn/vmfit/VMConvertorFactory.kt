package wang.julis.jproject.example.source.retrofit2.learn.vmfit

import wang.julis.jwbase.utils.GsonUtils
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType

/**
 *
 * Created by @juliswang on 2025/03/18 20:55
 *
 * @Description 基于 Gson 的一个转化器实现
 */
class VMConvertorFactory {
    fun <T> convert(value: String, method: Method): T {
        val lastParameter = method.genericParameterTypes.last()
        val responseType = getParameterLowerBound(0, lastParameter as ParameterizedType)
        return GsonUtils.fromJsonString(value, responseType)
    }

    private fun getParameterLowerBound(index: Int, type: ParameterizedType): Type {
        val paramType = type.actualTypeArguments[index]
        if (paramType is WildcardType) {
            return paramType.lowerBounds[0]
        }
        return paramType
    }
}