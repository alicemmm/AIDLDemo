package lomo.asia.aidldemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import lomo.asia.myaidllibrary.Book;
import lomo.asia.myaidllibrary.BookManagerService;
import lomo.asia.myaidllibrary.IBookManger;
import lomo.asia.myaidllibrary.IOnNewBookArrivedListener;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    private IBookManger remoteBookManager;

    private IOnNewBookArrivedListener onNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
        @Override
        public void onNewBookArrived(Book book) throws RemoteException {
            Log.e("tag", "onNewBookArrived: " + book.toString());
            textView.append("\n"+book.toString());
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            remoteBookManager = IBookManger.Stub.asInterface(service);
            try {
                remoteBookManager.registerListener(onNewBookArrivedListener);

                Book book = new Book(1,"大话西游");
                remoteBookManager.addBook(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            remoteBookManager = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);

    }


    /**
     * 绑定服务按钮的点击事件
     *
     * @param view 视图
     */
    public void bindService(View view) {
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    int num = 1;

    public void addBook(View view) {
        Book book = new Book(num++,"新书"+num);
        if (remoteBookManager!=null) {
            try {
                remoteBookManager.addBook(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public void getBookList(View view) {
        if (remoteBookManager!=null) {
            try {
                List<Book> books = remoteBookManager.getBookList();
                for (Book book:books){
                    Log.e("TAG", "getBookList: "+book.toString());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {

        if (remoteBookManager != null && remoteBookManager.asBinder().isBinderAlive()) {
            try {
                remoteBookManager.unregisterListener(onNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        try {
            unbindService(serviceConnection);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }
}
