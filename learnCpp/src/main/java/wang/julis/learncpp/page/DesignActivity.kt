package wang.julis.learncpp.page

import android.os.Bundle
import wang.julis.jwbase.basecompact.BaseActivity
import wang.julis.learncpp.R
import wang.julis.learncpp.databinding.ActivityDesignBinding
import wang.julis.learncpp.ops.DesignOps

/**
 * Created by juliswang on 2022/7/31 10:48
 *
 * Description :
 *
 *
 */

class DesignActivity : BaseActivity() {
    private val designOps = DesignOps()
    private lateinit var binding: ActivityDesignBinding
    override fun initView() {
        binding.btnCommand.setOnClickListener {
            designOps.command()
        }
        binding.btnFb.setOnClickListener {
            designOps.doubleFrameBuffer()
        }
    }

    override fun initData() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDesignBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun getContentView(): Int {
        return R.layout.activity_design
    }
}