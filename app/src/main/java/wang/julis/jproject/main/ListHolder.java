package wang.julis.jproject.main;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.julis.distance.R;

/*******************************************************
 *
 * Created by julis.wang@beibei.com on 2019/09/24 14:16
 *
 * Description :
 * History   :
 *
 *******************************************************/

public class ListHolder extends RecyclerView.ViewHolder {
    private Context mContext;
    private TextView tvDesc;


    public ListHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.mContext = context;
        initView();
    }

    private void initView() {
        tvDesc = itemView.findViewById(R.id.tv_desc);

    }

    public void updateView(final ListModel data, int position) {
        tvDesc.setText(data.activityName);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, data.activityClass));
            }
        });
    }
}
