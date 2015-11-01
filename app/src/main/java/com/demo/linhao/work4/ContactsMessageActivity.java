package com.demo.linhao.work4;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by linhao on 15/10/29.
 */
public class ContactsMessageActivity extends Activity {
    private TextView nameTextView;
    private TextView mobileTextView;
    private TextView qqTextView;
    private TextView danweiTextView;
    private TextView addressTextView;

    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);

        nameTextView = (TextView)findViewById(R.id.name);
        mobileTextView = (TextView)findViewById(R.id.mobile);
        qqTextView = (TextView)findViewById(R.id.danwei);
        danweiTextView = (TextView)findViewById(R.id.qq);
        addressTextView = (TextView)findViewById(R.id.address);

        Bundle localBundle = getIntent().getExtras();
        int id = localBundle.getInt("user_ID");
        ContactsTable ct = new ContactsTable(this);
        user = ct.getUserByID(id);
        nameTextView.setText("姓名:"+user.getName());
        mobileTextView.setText("电话"+user.getMobile());
        qqTextView.setText("qq"+user.getQq());
        danweiTextView.setText("单位"+user.getDanwei());
        addressTextView.setText("地址"+user.getAddress());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,1,Menu.NONE,"返回");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case 1:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}