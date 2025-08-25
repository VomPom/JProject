package wang.julis.jproject.example.source

import wang.julis.jproject.example.source.koin.noScope1.withoutScope1
import wang.julis.jproject.example.source.koin.noScope1.withoutScope2
import wang.julis.jproject.example.source.koin.parameter3.withParameter
import wang.julis.jproject.example.source.koin.scope2.withScope
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
        addItem("Glide", GlideActivity::class.java)
        addItem("MMKV") { MMKVTest.run(this) }
        addItem("Koin") {
            withoutScope1()
            withoutScope2()
            withScope()
            withParameter()
        }
    }
}