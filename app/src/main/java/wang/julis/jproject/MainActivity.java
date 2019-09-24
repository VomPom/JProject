package wang.julis.jproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.julis.distance.R;

import java.util.ArrayList;
import java.util.List;

import wang.julis.jproject.Anim.AnimationActivity;
import wang.julis.jproject.Anim.AnimatorSetActivity;
import wang.julis.jproject.Anim.ObjectAnimatorActivity;
import wang.julis.jproject.Anim.ValueAnimatorActivity;
import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang@beibei.com on 2019/09/24 14:12
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
        mDataList.add(new ListModel("海报生成", PosterGeneratorActivity.class));
        mDataList.add(new ListModel("补间动画", AnimationActivity.class));
        mDataList.add(new ListModel("ValueAnimator", ValueAnimatorActivity.class));
        mDataList.add(new ListModel("ObjectAnimator", ObjectAnimatorActivity.class));
        mDataList.add(new ListModel("AnimatorSet", AnimatorSetActivity.class));
        mAdapter.updateData(mDataList);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }
}
