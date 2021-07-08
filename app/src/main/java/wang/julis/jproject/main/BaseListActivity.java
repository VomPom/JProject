package wang.julis.jproject.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.julis.distance.R;

import java.util.ArrayList;
import java.util.List;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/07/08 13:49
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public abstract class BaseListActivity extends BaseActivity {
    protected ListAdapter mAdapter;
    protected final List<ListModel> mDataList = new ArrayList<>();

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

    }

    protected void addActivity(String activityName, Class<?> activityClass) {
        mDataList.add(new ListModel(activityName, activityClass));
    }

    protected void submitActivityList() {
        mAdapter.updateData(mDataList);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_list;
    }
}
