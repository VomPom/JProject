package wang.julis.jproject.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.annotations.SerializedName;
import com.julis.distance.R;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import wang.julis.jwbase.Request.BaseApiRequest;
import wang.julis.jwbase.Utils.CommonUtils;
import wang.julis.jwbase.Utils.ImageUtils;
import wang.julis.jwbase.Utils.QRUtils;
import wang.julis.jwbase.Utils.TimeUtils;
import wang.julis.jwbase.basecompact.BaseActivity;


/*******************************************************
 *
 * Created by julis.wang on 2019/09/23 10:27
 *
 * Description : 文章海报生成
 * History   :
 *
 *******************************************************/

public class ArticlePosterGeneratorActivity extends BaseActivity implements View.OnClickListener {
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100;
    private static final int POSTER_WIDTH = 1080;
    private static final int PER_MINUTES_READ = 500; //每分钟读500字

    private Bitmap mPosterBitmap;
    private EditText etTitle, etType, etContent;
    private ImageView ivPoster;
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected void initView() {
        ivPoster = findViewById(R.id.iv_generator);
        etType = findViewById(R.id.et_type);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        ivPoster.setOnClickListener(this);
        findViewById(R.id.btn_generator).setOnClickListener(this);
        findViewById(R.id.btn_save).setOnClickListener(this);

    }

    @Override
    protected void initData() {
        mUrl = "http://julis.wang";
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_article_generator;
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.btn_generator) {
            ivPoster.setVisibility(View.VISIBLE);
            getPermissions();
        } else if (vId == R.id.btn_save) {
            ImageUtils.saveImageToGallery(this, mPosterBitmap);
        } else if (vId == R.id.iv_generator) {
            ivPoster.setVisibility(View.GONE);
        }
    }

    /**
     * view获取
     */
    @SuppressLint("SetTextI18n")
    private void doGenerator() {
        View view = LayoutInflater.from(this).inflate(R.layout.layout_article_poster, null);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(POSTER_WIDTH, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

        ArticleModel data = getArticleData();
        ((TextView) view.findViewById(R.id.tv_title)).setText(data.title);
        ((TextView) view.findViewById(R.id.tv_content)).setText(data.content);
        ((TextView) view.findViewById(R.id.tv_time)).setText(data.writeTime + "\n --By julis.wang");
        ((TextView) view.findViewById(R.id.tv_desc)).setText(data.type + " | " + data.readTime);
        Bitmap qrBitmap = QRUtils.createQRImage(mUrl, (int) CommonUtils.dip2px(60.0f), 0);
        ((ImageView) view.findViewById(R.id.qr_code)).setImageBitmap(qrBitmap);

        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        realGenerator(view);
    }

    private ArticleModel getArticleData() {
        ArticleModel data = new ArticleModel();
        data.content = etContent.getText().toString();
        data.title = etTitle.getText().toString();
        data.type = etType.getText().toString();
        data.writeTime = TimeUtils.changeLongToString3(System.currentTimeMillis() / 1000);
        data.readTime = String.format(Locale.CHINA,
                "%d分钟读完（%d个字)", getReadTime(data.content.length()), data.content.length());
        return data;
    }

    private int getReadTime(int dataSize) {
        return dataSize < PER_MINUTES_READ ? 1 : dataSize / PER_MINUTES_READ;
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
        mPosterBitmap = posterBitmap;
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

    private void setContentWithImages(TextView tvSummary, String content) {
        String html = "<h1>AAAthis is h1</h1>"
                + "<p>This text is normal</p>"
                + "<img src='http://h0.hucdn.com/images202002/8f97ac11a4c14bf1_750x280.png' />";


        Spanned sp = Html.fromHtml(html, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                InputStream is = null;
                try {
                    is = (InputStream) new URL(source).getContent();
                    Drawable d = Drawable.createFromStream(is, "src");
                    d.setBounds(0, 0, d.getIntrinsicWidth(),
                            d.getIntrinsicHeight());
                    is.close();
                    return d;
                } catch (Exception e) {
                    return null;
                }
            }
        }, null);
        tvSummary.setText(sp);

    }

    private static class ArticleModel {
        public String title;
        public String content;
        public String type;
        public String readTime;
        public String writeTime;
        public List<String> mKeywords;
    }

    private  class KeyWordsModel {
        @SerializedName("code")
        public int mCode;
        @SerializedName("message")
        public String mMessage;
        @SerializedName("keywords")
        public List<String> mKeywords;
    }

    private void getKeyWords() {

    }

    private class KeyWordsRequest extends BaseApiRequest<KeyWordsModel> {

        @Override
        public String getBaseUrl() {
            return null;
        }
    }
}






