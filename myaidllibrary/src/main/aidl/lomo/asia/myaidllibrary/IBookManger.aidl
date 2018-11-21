// IBookManger.aidl
package lomo.asia.myaidllibrary;

// Declare any non-default types here with import statements
import lomo.asia.myaidllibrary.Book;
import lomo.asia.myaidllibrary.IOnNewBookArrivedListener;

interface IBookManger {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

      List<Book> getBookList();
      void addBook(in Book book);
      void registerListener(IOnNewBookArrivedListener l);
      void unregisterListener(IOnNewBookArrivedListener l);
}
