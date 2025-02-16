package wang.julis.jproject.example.media.pag

import wang.julis.jwbase.basecompact.baseList.BaseListActivity

/**
 * Created by juliswang on 2023/7/4 20:16
 *
 * @Description
 */
class PAGMainActivity : BaseListActivity() {
    override fun initData() {
        addItem("Simple PAG", SimplePagActivity::class.java)
        addItem("Create PAGSurface through Texture id", PAGSurfaceTextureIdActivity::class.java)
    }
}