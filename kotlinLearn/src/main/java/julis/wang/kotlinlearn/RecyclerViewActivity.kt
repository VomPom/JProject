package julis.wang.kotlinlearn

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import julis.wang.kotlinlearn.databinding.ActivityRecyclerViewTestBinding
import wang.julis.jwbase.basecompact.BaseActivity

/**
 * Copyright (C) @2023 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Created by juliswang on 2023/1/31 17:30
 *
 * @Description
 */
class RecyclerViewActivity : BaseActivity() {
    private val musicPlaceHolderAdapter by lazy { MusicLibraryPlaceHolderAdapter() }
    private var recyclerView: RecyclerView? = null
    private lateinit var binding: ActivityRecyclerViewTestBinding
    override fun initView() {
        binding = ActivityRecyclerViewTestBinding.inflate(layoutInflater)
        showPlaceHolder()
    }

    private fun showPlaceHolder() {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView = findViewById(R.id.rv_music_list)
        recyclerView?.layoutManager = layoutManager
        recyclerView?.adapter = musicPlaceHolderAdapter
    }


    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_recycler_view_test
    }
}