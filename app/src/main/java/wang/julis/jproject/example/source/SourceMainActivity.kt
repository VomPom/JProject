package wang.julis.jproject.example.source

import wang.julis.jwbase.basecompact.baseList.BaseListActivity

/**
 *
 * Created by @juliswang on 2025/03/17 10:31
 *
 * @Description
 */
class SourceMainActivity : BaseListActivity() {
    override fun initData() {
        addItem("Retrofit2") { Retrofit2Test.run(this) }
    }
}