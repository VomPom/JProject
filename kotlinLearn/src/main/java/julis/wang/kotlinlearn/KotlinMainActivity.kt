package julis.wang.kotlinlearn

import julis.wang.kotlinlearn.feature.CoroutineDialogActivity
import julis.wang.kotlinlearn.feature.CoroutinesBaseTest
import julis.wang.kotlinlearn.feature.CoroutinesFlowTest
import julis.wang.kotlinlearn.feature.FlowVideoModelActivity
import julis.wang.kotlinlearn.feature.FuncActivity
import julis.wang.kotlinlearn.feature.IntentExtActivity
import julis.wang.kotlinlearn.feature.KeywordActivity
import julis.wang.kotlinlearn.feature.ViewBindingActivity
import julis.wang.kotlinlearn.jetpack.RoomTest
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
        addItem("高阶函数", FuncActivity::class.java)
        addItem("关键词", KeywordActivity::class.java)
        addItem("Room") { RoomTest.run(this) }
        addItem("FlowViewModel", FlowVideoModelActivity::class.java)
        addItem("协程基础") { CoroutinesBaseTest.run(this) }
        addItem("协程Flow") { CoroutinesFlowTest.run(this) }
        addItem("协程消灭对称式Dialog", CoroutineDialogActivity::class.java)

    }

}