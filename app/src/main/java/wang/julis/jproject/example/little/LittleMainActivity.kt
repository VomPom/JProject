package wang.julis.jproject.example.little

import wang.julis.jproject.example.binder.client.BinderTestActivity
import wang.julis.jwbase.basecompact.baseList.BaseListActivity

/**
 * Copyright (C) @2025 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Created by @juliswang on 2025/02/14 16:46
 *
 * @Description
 */
class LittleMainActivity : BaseListActivity() {
    override fun initData() {
        addItem("JsonTest", JsonTestActivity::class.java)
        addItem("BinderExample", BinderTestActivity::class.java)
        addItem("文字编码", CharacterDecodingActivity::class.java)
        addItem("Router", RouterActivity::class.java)
        addItem("位标记模式", BitMarkTest::run)
    }
}