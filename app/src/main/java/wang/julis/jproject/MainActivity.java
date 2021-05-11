package wang.julis.jproject;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.julis.distance.R;

import java.util.ArrayList;
import java.util.List;

import wang.julis.jproject.blog.ArticlePosterGeneratorActivity;
import wang.julis.jproject.blog.PosterGeneratorActivity;
import wang.julis.jproject.example.Anim.AnimationActivity;
import wang.julis.jproject.example.Anim.AnimatorSetActivity;
import wang.julis.jproject.example.Anim.ObjectAnimatorActivity;
import wang.julis.jproject.example.Anim.ValueAnimatorActivity;
import wang.julis.jproject.example.binder.client.BinderTestActivity;
import wang.julis.jproject.main.ListAdapter;
import wang.julis.jproject.main.ListModel;
import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2019/09/24 14:12
 *
 * Description :
 * History   :
 *
 *******************************************************/

public class MainActivity extends BaseActivity {
    private ListAdapter mAdapter;
    private List<ListModel> mDataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        RecyclerView rvList = findViewById(R.id.rv_list);
        mAdapter = new ListAdapter(this);
        rvList.setAdapter(mAdapter);
        rvList.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void initData() {
        mDataList.add(new ListModel("文章海报生成", ArticlePosterGeneratorActivity.class));
        mDataList.add(new ListModel("博客海报生成", PosterGeneratorActivity.class));
        mDataList.add(new ListModel("补间动画", AnimationActivity.class));
        mDataList.add(new ListModel("ValueAnimator", ValueAnimatorActivity.class));
        mDataList.add(new ListModel("ObjectAnimator", ObjectAnimatorActivity.class));
        mDataList.add(new ListModel("AnimatorSet", AnimatorSetActivity.class));
        mDataList.add(new ListModel("BinderExample", BinderTestActivity.class));
        mAdapter.updateData(mDataList);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }
}
