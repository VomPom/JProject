package wang.julis.jproject.example.binder;

import android.os.Parcel;
import android.os.Parcelable;

/*******************************************************
 *
 * Created by julis.wang on 2021/05/11 18:54
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class Book implements Parcelable {
    public String name;

    public Book(String name) {
        this.name = name;
    }

    protected Book(Parcel in) {
        this.name = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public void readFromParcel(Parcel dest) {
        name = dest.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    @Override
    public String toString() {
        return "\nBook{" +
                "name='" + name + '\'' +
                "}";
    }
}
