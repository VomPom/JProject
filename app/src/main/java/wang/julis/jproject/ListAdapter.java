package wang.julis.jproject;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.julis.distance.R;

import java.util.ArrayList;
import java.util.List;

/*******************************************************
 *
 * Created by julis.wang@beibei.com on 2019/09/24 14:15
 *
 * Description :
 * History   :
 *
 *******************************************************/


public class ListAdapter extends RecyclerView.Adapter<ListHolder> {
    private Context mContext;
    private List<ListModel> mDataList = new ArrayList<>();

    public ListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_list_item, viewGroup, false);
        return new ListHolder(view, mContext);
    }

    public void onBindViewHolder(@NonNull ListHolder listHolder, int i) {
        listHolder.updateView(mDataList.get(i), i);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateData(List<ListModel> data) {
        mDataList.clear();
        mDataList.addAll(data);
        notifyDataSetChanged();
    }

}
