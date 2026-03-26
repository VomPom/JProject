package com.vompom.sourcecode

import com.vompom.sourcecode.koin.noScope1.withoutScope1
import com.vompom.sourcecode.koin.noScope1.withoutScope2
import com.vompom.sourcecode.koin.parameter3.withParameter
import com.vompom.sourcecode.koin.scope2.withScope
import com.vompom.sourcecode.little.GlideActivity
import com.vompom.sourcecode.little.MMKVTest
import com.vompom.sourcecode.retrofit2.Retrofit2Test
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