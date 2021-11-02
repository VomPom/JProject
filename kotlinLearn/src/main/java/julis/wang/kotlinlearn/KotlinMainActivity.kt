package julis.wang.kotlinlearn

import julis.wang.kotlinlearn.main.BaseListActivity
import julis.wang.kotlinlearn.viewmodel.ViewModelTestActivity

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