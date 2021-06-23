package wang.julis.jproject.example.okhttp;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.julis.distance.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import wang.julis.jwbase.Utils.CommonUtils;
import wang.julis.jwbase.basecompact.BaseActivity;

/*******************************************************
 *
 * Created by julis.wang on 2021/05/11 19:33
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class OkHttpTestActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void initView() {
        findViewById(R.id.btn_ok_http_request).setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_okhttp_test;
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        List<String> list = CommonUtils.getAppList(this);
        list.size();
        if (viewId == R.id.btn_ok_http_request) {
            test(OkHttpTestActivity.this);
//            for (int i = 0; i < 5; i++) {
//                InfoModel data = new InfoModel(new Random().nextInt(4), "message");
//                RequestUtils.request(data);
//            }
        }

    }

    private void test(Context context) {
        ContentResolver provider = context.getContentResolver();
        Cursor cursorTid;
        try {
            Uri uri = Uri.parse("content://com.yuewen.kui.provider/kui_config/getString");
            //uri拼接，前面是provider地址，第二个是sharepreference的Name，由于我存入的数据是String,原作者将getString与前面的uri拼接
            String[] selectionArgs = {"0", "hello", ""};//第一个参数是否是安全模式，一般为0。第二个参数读取的key，第三个参数defaultValue
            cursorTid = provider.query(uri, null, null, selectionArgs, null);
            Bundle extras = cursorTid.getExtras();
            String hello1 = (String) extras.get("value");
            Log.e("julis", hello1);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage() + "\t" + e.toString());
            e.printStackTrace();
        }
    }

    private void request() {
        String url = "http://127.0.0.1:8000";

        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("method", "method");
        paramsMap.put("url", "url");
        paramsMap.put("data", "data");
        paramsMap.put("response", "response");
        String data = JSONObject.wrap(paramsMap).toString();
        FormBody.Builder builder = new FormBody.Builder();
        for (String key : paramsMap.keySet()) {
            builder.add(key, paramsMap.get(key));
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), data);
        OkHttpClient okHttpClient = new
                OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        final Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();


        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("julis", "onFailure: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("julis", "Got result onResponse: " + response.message());
            }
        });
    }
}
