package com.example.qman.rockpad.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.qman.rockpad.R;

import java.util.Map;

/**
 * Created by Qman on 2017/2/15.
 */

public class ActivityUtil extends AppCompatActivity {
    public static Fragment mContent;//当前显示的fragment
   /* public static void jumpFormActivityToActivity(Activity fromActivity, Class<? extends Activity> toActivity){
        Intent intent = new Intent();
        intent.setClass(fromActivity, toActivity.class);
        Activity activity = new Activity();
        activity.startActivity(intent);//RegisterFragmentActivity为要打开的新窗口的类名
    }
    public void jumpFormActivityToFragment(){

    }*/

    /**
     * </br><b>title : </b>       设置Activity全屏显示
     * </br><b>description :</b>设置Activity全屏显示。
     * @param activity Activity引用
     * @param isFull true为全屏，false为非全屏
     */
    public static void setFullScreen(Activity activity,boolean isFull){
        Window window = activity.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        if (isFull) {
            params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            window.setAttributes(params);
            window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.setAttributes(params);
            window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    /**
     * </br><b>title : </b>       隐藏系统标题栏
     * </br><b>description :</b>隐藏Activity的系统默认标题栏
     * @param activity Activity对象
     */
    public static void hideTitleBar(Activity activity){
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    /**
     * </br><b>title : </b>       设置Activity的显示方向为垂直方向
     * </br><b>description :</b>强制设置Actiity的显示方向为垂直方向。
     * @param activity Activity对象
     */
    public static void setScreenVertical(Activity activity){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * </br><b>title : </b>       设置Activity的显示方向为横向
     * </br><b>description :</b>强制设置Actiity的显示方向为横向。
     * @param activity Activity对象
     */
    public static void setScreenHorizontal(Activity activity){
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * </br><b>title : </b>     隐藏软件输入法
     * </br><b>description :</b>隐藏软件输入法
     * </br><b>time :</b>       2012-7-12 下午7:20:00
     * @param activity
     */
    public static void hideSoftInput(Activity activity){
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * </br><b>title : </b>       使UI适配输入法
     * </br><b>description :</b>使UI适配输入法
     * </br><b>time :</b>     2012-7-17 下午10:21:26
     * @param activity
     */
    public static void adjustSoftInput(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    /**
     * </br><b>title : </b>       跳转到某个Activity。
     * </br><b>description :</b>跳转到某个Activity
     * </br><b>time :</b>     2012-7-8 下午3:20:00
     * @param activity          本Activity
     * @param targetActivity    目标Activity的Class
     */
    public static void switchTo(Activity activity,Class<? extends Activity> targetActivity){
        switchTo(activity, new Intent(activity,targetActivity));
    }

    /**
     * </br><b>title : </b>       根据给定的Intent进行Activity跳转
     * </br><b>description :</b>根据给定的Intent进行Activity跳转
     * </br><b>time :</b>     2012-7-8 下午3:22:23
     * @param activity          Activity对象
     * @param intent            要传递的Intent对象
     */
    public static void switchTo(Activity activity,Intent intent){
        activity.startActivity(intent);
    }

    /**
     * </br><b>title : </b>       带参数进行Activity跳转
     * </br><b>description :</b>带参数进行Activity跳转
     * </br><b>time :</b>     2012-7-8 下午3:24:54
     * @param activity          Activity对象
     * @param targetActivity    目标Activity的Class
     * @param params            跳转所带的参数
     */
    public static void switchTo(Activity activity,Class<? extends Activity> targetActivity,Map<String,Object> params){
        if( null != params ){
            Intent intent = new Intent(activity,targetActivity);
            for(Map.Entry<String, Object> entry : params.entrySet()){
                setValueToIntent(intent, entry.getKey(), entry.getValue());
            }
            switchTo(activity, intent);
        }
    }


    /**
     * Activity跳转到Fragment
     * @param activity
     * @param fragment
     * @param containerViewId
     */
    public static void switchToFragment(Activity activity,Fragment fragment,int containerViewId){
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(containerViewId,fragment);
        /*if (!fragment.isAdded()) {    // 先判断是否被add过
            tx.hide(mContent).add(containerViewId, fragment); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            tx.hide(mContent).show(fragment); // 隐藏当前的fragment，显示下一个
        }*/
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
        mContent = fragment;
    }

    /**
     * Fragment跳转到Fragment,带参数跳转
     * @param activity
     * @param fragment
     * @param containerViewId
     */
    public static void switchToFragment(Activity activity,Fragment fragment,int containerViewId,String params){
        FragmentManager fm = activity.getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Bundle args = new Bundle();
        args.putString("param", params);
        fragment.setArguments(args);
        transaction.replace(containerViewId,fragment);
        /*if (!fragment.isAdded()) {    // 先判断是否被add过
            tx.hide(mContent).add(containerViewId, fragment); // 隐藏当前的fragment，add下一个到Activity中
        } else {
            tx.hide(mContent).show(fragment); // 隐藏当前的fragment，显示下一个
        }*/
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
        mContent = fragment;
    }




    /**
     * 设置标题栏显示文字
     * @param activity
     * @param containerViewId
     * @param params
     */
    public static void setTitle(Activity activity,int containerViewId,String params){
        TextView title = (TextView)activity.findViewById(containerViewId);
        title.setText(params);
    }

    /**
     * 全部可见
     * @param title
     * @param toolbar_search
     * @param toolbar_add
     */
    public static void setAllVisibilitys(TextView title, Button toolbar_search, Button toolbar_add, Button toolbar_draw){
        title.setVisibility(View.VISIBLE);
        toolbar_search.setVisibility(View.VISIBLE);
        toolbar_add.setVisibility(View.VISIBLE);
        toolbar_draw.setVisibility(View.VISIBLE);
    }

    /**
     * 只可见标题栏
     * @param title
     * @param toolbar_search
     * @param toolbar_add
     */
    public static void setOnlyVisibilitys(TextView title, Button toolbar_search, Button toolbar_add, Button toolbar_draw){
        title.setVisibility(View.VISIBLE);
        Log.i("title:",title.getText().toString());
        toolbar_search.setVisibility(View.GONE);
        Log.i("toolbar_search:",toolbar_search.getText().toString());
        toolbar_add.setVisibility(View.GONE);
        Log.i("toolbar_add:",toolbar_add.getText().toString());
        toolbar_draw.setVisibility(View.GONE);
        Log.i("toolbar_draw:",toolbar_draw.getText().toString());
    }

    /**
     * 只可见标题栏，带activity
     * @param activity
     * @param title_Id
     * @param toolbar_search_Id
     * @param toolbar_add_Id
     * @param toolbar_draw_Id
     */
    public static void setAllVisibilitys(Activity activity,int title_Id, int toolbar_search_Id, int toolbar_add_Id, int toolbar_draw_Id){
        TextView titleActivity = (TextView)activity.findViewById(title_Id);
        Button toolbar_search_button = (Button)activity.findViewById(toolbar_search_Id);
        Button toolbar_add_button = (Button)activity.findViewById(toolbar_add_Id);
        Button toolbar_draw_button = (Button)activity.findViewById(toolbar_draw_Id);
        titleActivity.setVisibility(View.VISIBLE);
        toolbar_search_button.setVisibility(View.VISIBLE);
        toolbar_add_button.setVisibility(View.VISIBLE);
        toolbar_draw_button.setVisibility(View.VISIBLE);
    }
    /**
     * 只可见标题栏，带activity
     * @param activity
     * @param title_Id
     * @param toolbar_search_Id
     * @param toolbar_add_Id
     * @param toolbar_draw_Id
     */
    public static void setOnlyVisibilitys(Activity activity,int title_Id, int toolbar_search_Id, int toolbar_add_Id, int toolbar_draw_Id){
        TextView titleActivity = (TextView)activity.findViewById(title_Id);
        Button toolbar_search_button = (Button)activity.findViewById(toolbar_search_Id);
        Button toolbar_add_button = (Button)activity.findViewById(toolbar_add_Id);
        Button toolbar_draw_button = (Button)activity.findViewById(toolbar_draw_Id);
        titleActivity.setVisibility(View.VISIBLE);
        toolbar_search_button.setVisibility(View.GONE);
        toolbar_add_button.setVisibility(View.GONE);
        toolbar_draw_button.setVisibility(View.GONE);
    }

    public static void putParam(Activity activity, String key, String val){
        Intent intent= activity.getIntent();
        intent.putExtra(key, (String) val);
    }
    /**
     * 获取当前Activity带有的参数
     * @param activity
     * @param param
     * @return
     */
    public static String getParam(Activity activity, String param){
        Intent intent= activity.getIntent();
        return intent.getStringExtra(param);
    }
    public static int getIntParam(Activity activity, String param){
        Intent intent= activity.getIntent();
        return intent.getIntExtra(param,0);
    }

    /**
     * 更新Bundle中的参数
     * @param activity
     * @param key
     * @param val
     */
    public static void changeParam(Activity activity, String key, String val){
        Intent intent= activity.getIntent();
        intent.removeExtra(key);
        intent.putExtra(key, (String) val);
    }

    /**
     * </br><b>title : </b>       显示Toast消息。
     * </br><b>description :</b>显示Toast消息，并保证运行在UI线程中
     * </br><b>time :</b>     2012-7-10 下午08:36:02
     * @param activity
     * @param message
     */
    public static void toastShow(final Activity activity,final String message){
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 在Fragment中显示Toast消息
     * @param fragment
     * @param message
     */
    public static void toastShowFragment(final Fragment fragment,final String message){
        fragment.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(fragment.getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * </br><b>title : </b>       将值设置到Intent里
     * </br><b>description :</b>将值设置到Intent里
     * </br><b>time :</b>     2012-7-8 下午3:31:17
     * @param intent            Inent对象
     * @param key               Key
     * @param val               Value
     */
    public static void setValueToIntent(Intent intent, String key, Object val) {
        if (val instanceof Boolean)
            intent.putExtra(key, (Boolean) val);
        else if (val instanceof Boolean[])
            intent.putExtra(key, (Boolean[]) val);
        else if (val instanceof String)
            intent.putExtra(key, (String) val);
        else if (val instanceof String[])
            intent.putExtra(key, (String[]) val);
        else if (val instanceof Integer)
            intent.putExtra(key, (Integer) val);
        else if (val instanceof Integer[])
            intent.putExtra(key, (Integer[]) val);
        else if (val instanceof Long)
            intent.putExtra(key, (Long) val);
        else if (val instanceof Long[])
            intent.putExtra(key, (Long[]) val);
        else if (val instanceof Double)
            intent.putExtra(key, (Double) val);
        else if (val instanceof Double[])
            intent.putExtra(key, (Double[]) val);
        else if (val instanceof Float)
            intent.putExtra(key, (Float) val);
        else if (val instanceof Float[])
            intent.putExtra(key, (Float[]) val);
    }
}
