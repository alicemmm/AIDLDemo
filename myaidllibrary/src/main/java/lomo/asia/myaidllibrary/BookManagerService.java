package lomo.asia.myaidllibrary;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {
    private static final String TAG = BookManagerService.class.getSimpleName();

    //支持并发
    private CopyOnWriteArrayList<Book> bookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> listenerList = new RemoteCallbackList<>();
    private AtomicBoolean mIsServiceDestoryed = new AtomicBoolean();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private Binder binder = new IBookManger.Stub() {
        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            bookList.add(book);

            int num = listenerList.beginBroadcast();
            for (int i=0;i<num;i++){
                IOnNewBookArrivedListener listener = listenerList.getBroadcastItem(i);
                listener.onNewBookArrived(book);
            }

            listenerList.finishBroadcast();
        }

        @Override
        public void registerListener(IOnNewBookArrivedListener l) throws RemoteException {
            listenerList.register(l);
            int num = listenerList.beginBroadcast();
            listenerList.finishBroadcast();
            Log.e(TAG, "添加完成, 注册接口数: " + num);
        }

        @Override
        public void unregisterListener(IOnNewBookArrivedListener l) throws RemoteException {
            listenerList.unregister(l);
            int num = listenerList.beginBroadcast();
            listenerList.finishBroadcast();
            Log.e(TAG, "删除完成, 注册接口数: " + num);
        }
    };
}
