package com.demo.linhao.work4;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MyContactsActivity extends Activity {
    private ListView listView;
    private BaseAdapter listViewAdapter;
    private User users[];
    private int selectItem=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setTitle("通讯录");

        listView = (ListView)findViewById(R.id.listView);
        loadContacts();
    }

    private void loadContacts() {
        ContactsTable ct = new ContactsTable(this);
        users = ct.getAllUser();
        listViewAdapter = new BaseAdapter() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null)
                {
                    TextView textView = new TextView(MyContactsActivity.this);
                    textView.setTextSize(22);
                    convertView = textView;
                }
                String mobile = users[position].getMobile()==null?"":users[position].getMobile();
                ((TextView)convertView).setText(users[position].getName()+"---"+mobile);
                if(position == selectItem)
                {
                    convertView.setBackgroundColor(Color.YELLOW);
                }else
                {
                    convertView.setBackgroundColor(0);
                }
                return convertView;
            }
            @Override
            public int getCount() {
                return users.length;
            }

            @Override
            public Object getItem(int position) {
                return users[position];
            }

            @Override
            public long getItemId(int position) {
                return position;
            }
        };
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectItem = i;
                listViewAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(Menu.NONE,1,Menu.NONE,"添加");
        menu.add(Menu.NONE,2,Menu.NONE,"编辑");
        menu.add(Menu.NONE,3,Menu.NONE,"查看信息");
        menu.add(Menu.NONE,4,Menu.NONE,"删除");
        menu.add(Menu.NONE,5,Menu.NONE,"查询");
        menu.add(Menu.NONE,6, Menu.NONE, "导入到手机电话薄");
        menu.add(Menu.NONE,7, Menu.NONE, "退出");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case 1:
                Intent intent = new Intent(MyContactsActivity.this,AddContactsActivity.class);
                startActivity(intent);
                break;
            case 2:
                if(users[selectItem].getId_DB()>0)
                {
                    intent = new Intent(MyContactsActivity.this,UpdateContactsActivity.class);
                    intent.putExtra("user_ID",users[selectItem].getId_DB());
                    startActivity(intent);
                }else
                {
                    Toast.makeText(this, "无结果记录，无法操作！", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                if(users[selectItem].getId_DB()>0)
                {
                    intent = new Intent(MyContactsActivity.this,ContactsMessageActivity.class);
                    intent.putExtra("user_ID",users[selectItem].getId_DB());
                    startActivity(intent);
                }else{
                    Toast.makeText(this,"无结果记录，无法操作！",Toast.LENGTH_SHORT).show();
                }
                break;
            case 4:
                if(users[selectItem].getId_DB()>0)
                {
                    delete();
                }else{
                    Toast.makeText(this,"无结果记录，无法操作！",Toast.LENGTH_SHORT).show();
                }
                break;
            case 5:
                new FindDialog(MyContactsActivity.this).show();
                break;
            case 6:
                if(users[selectItem].getId_DB()>0)
                {
                    importPhone(users[selectItem].getName(),users[selectItem].getMobile());
                    Toast.makeText(this,"导入成功"+users[selectItem].getName()+"到手机电话簿",Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(this,"无结果记录，无法操作！",Toast.LENGTH_SHORT).show();
                }
                break;
            case 7:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContactsTable ct = new ContactsTable(this);
        users = ct.getAllUser();
        listViewAdapter.notifyDataSetChanged();
    }

    private void importPhone(String name, String phone) {
        Uri phoneURL = ContactsContract.Data.CONTENT_URI;
        ContentValues values = new ContentValues();

        Uri rawContactUri = this.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI,values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        this.getContentResolver().insert(phoneURL, values);

        values.clear();
        values.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        values.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
        values.put(ContactsContract.CommonDataKinds.Phone.NUMBER,phone);
        values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        this.getContentResolver().insert(phoneURL,values);
    }

    private void delete() {
        Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("系统信息");
        alert.setMessage("是否删除联系人");
        alert.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                ContactsTable ct = new ContactsTable(MyContactsActivity.this);
                if (ct.deleteByUser(users[selectItem])) {
                    users = ct.getAllUser();
                    listViewAdapter.notifyDataSetChanged();
                    selectItem = 0;
                    Toast.makeText(MyContactsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(MyContactsActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.show();
    }

    private class FindDialog extends Dialog {
        public FindDialog(Context context)
        {
            super(context);
        }
        protected  void onCreate(Bundle saveInstanceState)
        {
            super.onCreate(saveInstanceState);
            setContentView(R.layout.find);
            setTitle("联系人查询");
            Button find = (Button) findViewById(R.id.find);
            Button cancel = (Button) findViewById(R.id.cancel);
            find.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText value = (EditText) findViewById(R.id.value);
                    ContactsTable ct = new ContactsTable(MyContactsActivity.this);
                    users = ct.findUserByKey(value.getText().toString());
                    for (int i =0;i<users.length;i++)
                    {
                        System.out.println("姓名是"+users[i].getName()+",电话是" +users[i].getMobile());
                    }
                    listViewAdapter.notifyDataSetChanged();
                    selectItem=0;
                    dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }
}
