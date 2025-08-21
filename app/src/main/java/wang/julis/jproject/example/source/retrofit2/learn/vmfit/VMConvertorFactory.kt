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

    /**
     * 这段代码的作用是从一个 参数化类型（ParameterizedType） 中获取指定索引位置的类型参数的下界（lower bound）。
     * 它主要用于处理泛型类型中的通配符（?）及其边界（bounds）
     *
     * 参考自 [retrofit2.Utils.getParameterLowerBound]
     * @param index Int                 示要获取的类型参数的索引位置
     * @param type ParameterizedType    表示示一个参数化类型（例如 List<String> 或 Map<Integer, String>）。
     * @return Type                     返回指定索引位置的类型参数的下界（Type 类型）。
     */
    private fun getParameterLowerBound(index: Int, type: ParameterizedType): Type {
        val paramType = type.actualTypeArguments[index]
        if (paramType is WildcardType) {
            return paramType.lowerBounds[0]
        }
        return paramType
    }
}