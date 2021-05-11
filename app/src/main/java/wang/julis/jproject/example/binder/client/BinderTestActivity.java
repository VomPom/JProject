package wang.julis.jproject.example.binder.client;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

import com.julis.distance.R;

import java.util.ArrayList;
import java.util.List;

import wang.julis.jproject.example.binder.Book;
import wang.julis.jproject.example.binder.BookController;
import wang.julis.jwbase.Utils.ToastUtils;
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

public class BinderTestActivity extends BaseActivity implements View.OnClickListener {
    private boolean connected;
    private BookController bookController;
    private final List<Book> bookList = new ArrayList<>();  //Client data.


    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookController = BookController.Stub.asInterface(service);
            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };

    @Override
    protected void initView() {
        findViewById(R.id.btn_get_book).setOnClickListener(this);
        findViewById(R.id.btn_add_book).setOnClickListener(this);
        findViewById(R.id.btn_bind_service).setOnClickListener(this);
        findViewById(R.id.btn_unbind_service).setOnClickListener(this);
    }

    @Override
    protected void initData() {
        bindService();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_binder_test;
    }

    private void bindService() {
        Intent intent = new Intent();
        intent.setPackage(getPackageName()); //in same pack, so use getPackageName();
        intent.setAction("julis.wang.binder.example");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        try {
            switch (viewId) {
                case R.id.btn_add_book:
                    if (connected) {
                        bookController.addBookInOut(new Book("三体"));
                    } else {
                        ToastUtils.showToastShort("Service isn't connected");
                    }
                    break;
                case R.id.btn_get_book:
                    if (connected) {
                        bookList.addAll(bookController.getBookList());
                        ToastUtils.showToastShort(bookList.toString());
                    } else {
                        ToastUtils.showToastShort("Service isn't connected");
                    }
                    break;
                case R.id.btn_bind_service:
                    bindService();
                    break;
                case R.id.btn_unbind_service:
                    unbindService(serviceConnection);
                    ToastUtils.showToastShort("Service unbind.");
                    break;
                default:
                    break;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
