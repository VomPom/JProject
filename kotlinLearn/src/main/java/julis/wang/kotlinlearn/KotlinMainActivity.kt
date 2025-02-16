package julis.wang.kotlinlearn

import julis.wang.kotlinlearn.feature.CoroutinesActivity
import julis.wang.kotlinlearn.feature.FuncActivity
import julis.wang.kotlinlearn.feature.IntentExtActivity
import julis.wang.kotlinlearn.feature.KeywordActivity
import julis.wang.kotlinlearn.feature.ViewBindingActivity
import julis.wang.kotlinlearn.jetpack.ViewModelTestActivity
import wang.julis.jwbase.basecompact.baseList.BaseListActivity

/*******************************************************
 *
 * Created by juliswang on 2021/11/02 22:24
 *
 * Description :
 *
 *
 *******************************************************/

class KotlinMainActivity : BaseListActivity() {
    override fun initData() {
        addItem("ViewModel", ViewModelTestActivity::class.java)
        addItem("ViewBinding", ViewBindingActivity::class.java)
        addItem("Intent", IntentExtActivity::class.java)
        addItem("关键词", KeywordActivity::class.java)
        addItem("协程", CoroutinesActivity::class.java)
        addItem("高阶函数", FuncActivity::class.java)

    }

}