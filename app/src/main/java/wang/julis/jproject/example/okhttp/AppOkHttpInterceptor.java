package wang.julis.jproject.example.okhttp;


import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;


/*******************************************************
 *
 * Created by julis.wang on 2021/05/24 17:13
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class AppOkHttpInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Log.e("=====julis", "app request :" + request.toString());
        Response response = chain.proceed(request);
        try {
            chain.getClass().getMethod("proceed");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        for (String key : request.headers().toMultimap().keySet()) {
            Log.e("zp_test", "header: {" + key + " : " + request.headers().toMultimap().get(key) + "}");
        }
        Log.e("zp_test", "url: " + request.url().uri().toString());
        ResponseBody responseBody = response.body();
        if (HttpHeaders.hasBody(response) && responseBody != null) {
            BufferedReader bufferedReader = new BufferedReader(new
                    InputStreamReader(responseBody.byteStream(), "utf-8"));
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                Log.e("zp_test", "response: " + result);
            }
            responseBody.string();
        }
        return response.newBuilder().build();
    }
}
