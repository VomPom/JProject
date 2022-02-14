package julis.wang.learnopengl.opengl.glitem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

import julis.wang.learnopengl.R;

/*******************************************************
 *
 * Created by julis.wang on 2019/09/24 14:15
 *
 * Description :
 * History   :
 *
 *******************************************************/


public class GLItemListAdapter extends RecyclerView.Adapter<GLItemListHolder> {
    private final Context mContext;
    private final List<GLItemListModel> mDataList = new ArrayList<>();
    private ItemOnclickListener listener;

    public void setItemListener(ItemOnclickListener listener) {
        this.listener = listener;
    }

    public GLItemListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public GLItemListHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.layout_list_item, viewGroup, false);
        return new GLItemListHolder(view, mContext, listener);
    }

    public void onBindViewHolder(@NonNull GLItemListHolder listHolder, int i) {
        listHolder.updateView(mDataList.get(i), i);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public void updateData(List<GLItemListModel> data) {
        mDataList.clear();
        mDataList.addAll(data);
        notifyDataSetChanged();
    }

    public interface ItemOnclickListener {
        void onClick(int index);
    }
}
