package julis.wang.kotlinlearn.jetpack

/*******************************************************
 *
 * Created by juliswang on 2021/11/02 22:44
 *
 * Description :
 *
 *
 *******************************************************/

data class User(val sex: Int, var name: String, val address: Address? = null) : BaseData(sex) {
    fun printUserInfo(): String {
        return "sex:$sex name:$name"
    }
}

data class Address(val id: String, val detail: String?)