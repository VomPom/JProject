package wang.julis.jproject.example.media.little;

import android.util.Log;

import com.google.gson.Gson;
import com.julis.distance.R;

import org.json.JSONObject;

import java.util.Map;

import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by juliswang on 2021/08/25 16:22 
 *
 * Description : 
 *
 *
 *******************************************************/

public class JsonTestActivity extends BaseActivity {
    private static final String TEST_STR = "{\"biz\":{\"a\":1,\"bb\":324},\"source\":\"1234132\",\"t\":\"1625495655\",\"sign\":\"cccc\",\"page\":1,\"pagesize\":{\"x\":1,\"y\":{\"c\":13}},\"event_id\":1}";

    @Override
    protected void initView() {
        findViewById(R.id.btn_test).setOnClickListener(v -> {
            try {
                Gson gson = new Gson();
                Map map = gson.fromJson(TEST_STR, Map.class);

                JSONObject jsonObject = new JSONObject(TEST_STR);
                Log.e("julis", map.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_test;
    }
}
