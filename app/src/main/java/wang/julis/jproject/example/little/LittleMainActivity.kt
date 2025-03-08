package wang.julis.jproject.example.little

import wang.julis.jproject.example.binder.client.BinderTestActivity
import wang.julis.jwbase.basecompact.baseList.BaseListActivity

/**
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
        addItem("位标记", BitMarkTest::run)
        addItem("Matrix") { MatrixTest.run(this) }
        addItem("ViewStub", ViewStubActivity::class.java)
    }
}