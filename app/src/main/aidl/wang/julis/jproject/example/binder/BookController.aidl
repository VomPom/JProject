// BookController.aidl
package wang.julis.jproject.example.binder;
import wang.julis.jproject.example.binder.Book;
// Declare any non-default types here with import statements

interface BookController {
        List<Book> getBookList();

        void addBookInOut(inout Book book);
}