package wang.julis.jproject.example.media.opengl.glitem;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.julis.distance.R;

/*******************************************************
 *
 * Created by julis.wang on 2019/09/24 14:16
 *
 * Description :
 * History   :
 *
 *******************************************************/

public class GLItemListHolder extends RecyclerView.ViewHolder {
    private TextView tvDesc;
    private final GLItemListAdapter.ItemOnclickListener listener;


    public GLItemListHolder(@NonNull View itemView, Context context, GLItemListAdapter.ItemOnclickListener listener) {
        super(itemView);
        this.listener = listener;
        initView();
    }

    private void initView() {
        tvDesc = itemView.findViewById(R.id.tv_desc);

    }

    public void updateView(final GLItemListModel data, int position) {
        tvDesc.setText(data.actionName);
        itemView.setOnClickListener(v -> listener.onClick(position));
    }
}
