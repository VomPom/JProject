package wang.julis.jproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.julis.distance.R;

import wang.julis.jwbase.Utils.CommonUtils;
import wang.julis.jwbase.Utils.ImageUtils;
import wang.julis.jwbase.Utils.QRUtils;
import wang.julis.jwbase.basecompact.BaseActivity;


/*******************************************************
 *
 * Created by julis.wang@beibei.com on 2019/09/23 10:27
 *
 * Description : 博客海报生成
 * History   :
 *
 *******************************************************/

public class PosterGeneratorActivity extends BaseActivity implements View.OnClickListener {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100;
    private EditText etExt;
    private ImageView ivPoster;
    private WebView mWebView;
    private String mUrl, title, content, readTime, writeTime, type, visitor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {
        etExt = findViewById(R.id.et_ext);
        ivPoster = findViewById(R.id.iv_generator);
        findViewById(R.id.btn_generator).setOnClickListener(this);
        findViewById(R.id.btn_jump).setOnClickListener(this);
        init();
    }

    @Override
    protected void initData() {
        mUrl = "http://julis.wang";
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_post_generator;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_generator) {
            getPermissions();
        } else if (vId == R.id.btn_jump) {
            jumpTarget();
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        mWebView = findViewById(R.id.webview);
        // 开启JavaScript支持
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "java_obj");

        // 设置WebView是否支持使用屏幕控件或手势进行缩放，默认是true，支持缩放
        mWebView.getSettings().setSupportZoom(true);

        // 设置WebView是否使用其内置的变焦机制，该机制集合屏幕缩放控件使用，默认是false，不使用内置变焦机制。
        mWebView.getSettings().setBuiltInZoomControls(true);

        // 设置是否开启DOM存储API权限，默认false，未开启，设置为true，WebView能够使用DOM storage API
        mWebView.getSettings().setDomStorageEnabled(true);

        // 触摸焦点起作用.如果不设置，则在点击网页文本输入框时，不能弹出软键盘及不响应其他的一些事件。
        mWebView.requestFocus();

        // 设置此属性,可任意比例缩放,设置webview推荐使用的窗口
        mWebView.getSettings().setUseWideViewPort(true);

        // 设置webview加载的页面的模式,缩放至屏幕的大小
        mWebView.getSettings().setLoadWithOverviewMode(true);

        // 加载链接


        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showLoadingDialog();
                // 在开始加载网页时会回调
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 拦截 url 跳转,在里边添加点击链接跳转或者操作
                view.loadUrl(url);
                mUrl = url;
                etExt.setText(mUrl);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                stopLoadingDialog();
                // 在结束加载网页时会回调
                String jsShowTitle = "javascript:window.java_obj.showTitle("
                        + "document.querySelector('body > section > div > div > " +
                        "div.column.is-8-tablet.is-8-desktop.is-6-widescreen.has-order-2.column-main > " +
                        "div:nth-child(1) > div.card-content.article > h1').innerText);";

                String jsContent = "javascript:window.java_obj.showContent("
                        + "document.querySelector('body > section > div > div > " +
                        "div.column.is-8-tablet.is-8-desktop.is-6-widescreen.has-order-2.column-main > " +
                        "div:nth-child(1) > div.card-content.article > div.content').innerText);";

                String jsReadTime = "javascript:window.java_obj.showReadTime("
                        + "document.querySelector('body > section > div > div > " +
                        "div.column.is-8-tablet.is-8-desktop.is-6-widescreen.has-order-2.column-main > " +
                        "div:nth-child(1) > div.card-content.article > " +
                        "div.level.article-meta.is-size-7.is-uppercase.is-mobile.is-overflow-x-auto > " +
                        "div > span:nth-child(3)').innerText);";

                String jsTime = "javascript:window.java_obj.showTime("
                        + "document.querySelector('body > section > div > div > " +
                        "div.column.is-8-tablet.is-8-desktop.is-6-widescreen.has-order-2.column-main > " +
                        "div:nth-child(1) > div.card-content.article > " +
                        "div.level.article-meta.is-size-7.is-uppercase.is-mobile.is-overflow-x-auto > div >" +
                        " time').getAttribute(\"datetime\"));";

                String jsType = "javascript:window.java_obj.showType("
                        + "document.querySelector('body > section > div > div > " +
                        "div.column.is-8-tablet.is-8-desktop.is-6-widescreen.has-order-2.column-main > " +
                        "div:nth-child(1) > div.card-content.article > " +
                        "div.level.article-meta.is-size-7.is-uppercase.is-mobile.is-overflow-x-auto > " +
                        "div > div > a').innerText);";
                String jsVisitor = "javascript:window.java_obj.showVisitor("
                        + "document.querySelector(\"#busuanzi_value_site_uv\").innerText);";
                view.loadUrl(jsVisitor);
                view.loadUrl(jsType);
                view.loadUrl(jsShowTitle);
                view.loadUrl(jsContent);
                view.loadUrl(jsTime);
                view.loadUrl(jsReadTime);

                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // 加载错误的时候会回调，在其中可做错误处理，比如再请求加载一次，或者提示404的错误页面
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              WebResourceRequest request) {
                // 在每一次请求资源时，都会通过这个函数来回调
                return super.shouldInterceptRequest(view, request);
            }

        });
        jumpTarget();
    }

    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showTitle(String str) {
            title = str;
        }

        @JavascriptInterface
        public void showVisitor(String str) {
            visitor = str;
        }

        @JavascriptInterface
        public void showContent(String str) {
            content = str.replaceAll("\n\n", "\n");
        }

        @JavascriptInterface
        public void showReadTime(String str) {
            readTime = str.replaceAll(" ", "");
        }

        @JavascriptInterface
        public void showTime(String str) {
            writeTime = str;
        }

        @JavascriptInterface
        public void showType(String str) {
            type = str;
        }

    }


    private void jumpTarget() {
        String url = etExt.getText().toString();
        if (TextUtils.isEmpty(url)) {
            url = "http://julis.wang";
        }
        mWebView.loadUrl(url);
    }

    private void doGenerator() {
        initPosterView();
    }

    /**
     * view获取
     */
    @SuppressLint("SetTextI18n")
    private void initPosterView() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_poster, null);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvSummary = view.findViewById(R.id.tv_summary);
        ImageView qrCode = view.findViewById(R.id.qr_code);
        TextView tvTime = view.findViewById(R.id.tv_time);
        TextView tvDesc = view.findViewById(R.id.tv_desc);
        tvSummary.setText(content);
        tvTitle.setText(title);
        tvTime.setText(writeTime + "\n NO:" + visitor + " By julis.wang");
        tvDesc.setText(type + " | " + readTime);
        Bitmap qrBitmap = QRUtils.createQRImage(mUrl, (int) CommonUtils.dip2px(60.0f), 0);
        qrCode.setImageBitmap(qrBitmap);

        int screenWidth = 1080; // CommonUtils.getWidth(this);
        int screenHeight = 1920; //CommonUtils.getHeight(this);
        int widthSpec = View.MeasureSpec.makeMeasureSpec(screenWidth, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(screenHeight, View.MeasureSpec.EXACTLY);

        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        realGenerator(view);
    }

    /**
     * 真正生成海报
     *
     * @param view
     */
    private void realGenerator(View view) {
        Bitmap posterBitmap = Bitmap.createBitmap(
                view.getMeasuredWidth(),
                view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(posterBitmap);
        view.draw(canvas);
        ivPoster.setImageBitmap(posterBitmap);
        ImageUtils.saveImageToGallery(this, posterBitmap);
    }

    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                doGenerator();
            } else {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }
        } else {
            doGenerator();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doGenerator();
            }
        }
    }
}
