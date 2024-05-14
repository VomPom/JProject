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
        addActivity("ViewModel", ViewModelTestActivity::class.java)
        addActivity("ViewBinding", ViewBindingActivity::class.java)
        addActivity("Intent", IntentExtActivity::class.java)
        addActivity("关键词", KeywordActivity::class.java)
        addActivity("协程", CoroutinesActivity::class.java)
        addActivity("高阶函数", FuncActivity::class.java)

    }

}