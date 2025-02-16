package wang.julis.jproject.example.binder.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import wang.julis.jproject.example.binder.Book;
import wang.julis.jproject.example.binder.BookController;
import wang.julis.jwbase.utils.ToastUtils;

/*******************************************************
 *
 * Created by julis.wang on 2021/05/11 18:57
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class AIDLService extends Service {
    private final List<Book> bookList = new ArrayList<>(); //Server data.

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
    }

    private void initData() {
        bookList.add(new Book("尘埃落定"));
        bookList.add(new Book("人类简史"));
        bookList.add(new Book("未来简史"));
        bookList.add(new Book("科学简史"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private final BookController.Stub stub = new BookController.Stub() {
        private int bookIndex = 0;

        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBookInOut(Book book) throws RemoteException {
            if (book != null) {
                book.name = book.name + ++bookIndex; // modifier original data.
                bookList.add(book);
                ToastUtils.showToastShort("Add new book index of:" + bookIndex);
            } else {
                Log.e("Test", "Null book");
            }
        }
    };
}
