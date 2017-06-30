package com.example.qman.rockpad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qman.rockpad.constant.BroadcastType;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProductsActivity extends AppCompatActivity {
    private TextView product_name;
    private TextView product_price;
    private TextView product_salesDaily;
    private TextView product_salesMonth;
    private ImageView product_image;
    private IntentFilter intentFilter;
    private SerialMsgReceiver serialReceiver;

    ScanHandler handler = new ScanHandler();
    private String productID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        //设置控件的值
//        setValue();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
     //   product_price.setText(productID);
        init();
        Intent intent = getIntent();
        productID = intent.getStringExtra("barID");
        query(productID);
        //开启广播接收
        serialReceiver = new SerialMsgReceiver();
        intentFilter=new IntentFilter("com.stone.uartBroadcast");
        registerReceiver(serialReceiver, intentFilter);

    }
    @Override
    public void onResume()
    {
        super.onResume();
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(serialReceiver);
    }
    private void init(){
        //初始化
        product_name = (TextView)findViewById(R.id.product_name);
        product_price = (TextView) findViewById(R.id.product_price);
        product_salesDaily = (TextView) findViewById(R.id.product_salesDaily);
        product_salesMonth = (TextView) findViewById(R.id.product_salesMonth);
        product_image = (ImageView)findViewById(R.id.product_image);
    }

    private void setValue(){
        product_price.setText("2.00元");
        product_salesDaily.setText("200瓶");
        product_salesMonth.setText("2000瓶");
    }

    class SerialMsgReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String type = bundle.getString("info");
            if (type.equals(BroadcastType.SCANBAR))
            {

                query( bundle.getString("value"));
            }
            else if (type.equals(BroadcastType.SCAN2BAR))
            {

            }

        }
    }
    class ScanHandler extends Handler
    {
        public void handleMessage(Message msg){
            switch(msg.what){
                case 1:
                    try {
                        if ((msg.obj.toString() == null) || msg.obj.toString().equals(""))
                        {
                            Toast.makeText(ProductsActivity.this,"未查询到您想要的信息",Toast.LENGTH_LONG).show();
                            break;
                        }
                        JSONObject jsonObject = new JSONObject(JSONTokener(msg.obj.toString()));
                        product_name.setText(jsonObject.getString("name"));
                        product_price.setText(jsonObject.getString("price") + "元");
                        product_salesDaily.setText(jsonObject.getString("daily_sale:"));
                        product_salesMonth.setText(jsonObject.getString("monthly_sale:"));
                        ImageLoader.getInstance().displayImage("http://"+jsonObject.getString("image"),product_image);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }


    public void query(final String id )
    {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String uriAPI = "http://211.159.157.142:8001/test?id=" + id;
                HttpGet httpRequest = new HttpGet(uriAPI);
                try
                {
                    HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
                    if(httpResponse.getStatusLine().getStatusCode() == 200)
                    {
                        String strResult = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
                        Message message = new Message();
                        message.what = 1;
                        message.obj = strResult;
                        handler.sendMessage(message);
                    }
                    else
                    {
                        //    mTextView1.setText("Error Response: "+httpResponse.getStatusLine().toString());
                    }
                }
                catch (ClientProtocolException e)
                {
                    Toast.makeText(ProductsActivity.this,"网络错误，未查询到您想要的信息",Toast.LENGTH_LONG).show();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
        }).start();
    }
    public String JSONTokener(String in) {
        if (in != null && in.startsWith("\ufeff")) {
            in = in.substring(1);
        }
        return in;
    }

}
