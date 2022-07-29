package julis.wang.kotlinlearn

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
        submitActivityList()
    }

}