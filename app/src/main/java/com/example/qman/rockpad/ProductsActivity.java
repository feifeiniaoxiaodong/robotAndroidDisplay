package com.example.qman.rockpad;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private TextView price_value = null;
    private TextView salesDaily_value = null;
    private TextView salesMonth_value = null;
    private ImageView image;

    ScanHandler handler = new ScanHandler();
    private String productID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        //初始化控件
        Intent intent = getIntent();
        productID = intent.getStringExtra("barID");

        init();
        //设置控件的值
//        setValue();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
        query(productID);
    }
    private void init(){
        //初始化
        price_value = (TextView) findViewById(R.id.product_price);
        salesDaily_value = (TextView) findViewById(R.id.product_salesDaily);
        salesMonth_value = (TextView) findViewById(R.id.product_salesMonth);
    }

    private void setValue(){
        price_value.setText("2.00元");
        salesDaily_value.setText("200瓶");
        salesMonth_value.setText("2000瓶");
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
                        price_value.setText(jsonObject.getString("id"));
                        salesDaily_value.setText(jsonObject.getString("name"));
                        salesMonth_value.setText(jsonObject.getString("info"));
                        ImageLoader.getInstance().displayImage("http://"+jsonObject.getString("image"),image);
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
