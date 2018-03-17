package com.example.qman.rockpad.dao;

import android.content.Intent;

import com.example.qman.rockpad.dao.dbcommand.EnvParam;
import com.example.qman.rockpad.dao.dbcommand.MoveParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库访问
 * 获取字符串 ，图片 ，*.mp3 文件
 * Created by wei on 2018/3/16.
 */

public class DBService {

    private static Object synobj=new Object();
    private  final  String driver = "com.mysql.jdbc.Driver";//MySQL 驱动
    private final String url = "jdbc:mysql://47.104.76.182:3306/rock_info?useUnicode=true&characterEncoding=UTF-8";//MYSQL数据库连接Url
    private  final String user = "root";//用户名
    private final String password = "123456";//密码

    Connection conn=null;
    PreparedStatement preparedStatement=null;
    ResultSet resultSet=null;

    static DBService dbService=null;
    public static DBService getInstence(){
        if(dbService==null){
            synchronized (synobj){
                if(dbService==null){
                    dbService=new DBService();
                }
            }
        }
        return dbService;
    }

    DBService(){}

    public void openConnection(){
        openConnection(url,user,password);
    }

    //建立数据库连接
    public boolean openConnection(String url,String user,String pass){
        boolean isOpen=false;
        if(conn==null){
           try{
               Class.forName( driver);
               conn= DriverManager.getConnection(url,user,pass);
               if(conn.isValid(10)){
                   isOpen=true;
               }
           }catch (Exception e){
               e.printStackTrace();
           }
       }
       return isOpen;
    }

    /**
     * 关闭数据库连接
     */
    public void colseConnection(){
        if(conn!=null){
            try{
                conn.close();
                conn=null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //查
    public MoveParam selectRockmoveById(String rid){
        if(conn==null) return null;

        final String sql="select * from rock_move where rid = ?";
//        sql="select * from rock_light WHERE rid = %s" % rid
        MoveParam moveParam=new MoveParam();//运动参数
        try{
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,rid);

            resultSet=preparedStatement.executeQuery();
            if(resultSet!=null){
                while(resultSet.next()){

                    moveParam.setId(resultSet.getString("id"));
                    moveParam.setRid(resultSet.getString("rid"));
                    String w=resultSet.getString("rock_move.w");
                    String v=resultSet.getString("v");
                    moveParam.setW(Integer.parseInt(w) );
                    moveParam.setV(Integer.parseInt(v));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closePreRes();
        }
        return moveParam;
    }

    //查
    public void selectRocklightById(String rid){
        if(conn==null) return ;
        final String sql="select * from rock_light where rid = ?";
        try{
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,rid);

            resultSet=preparedStatement.executeQuery(); //执行查询
            if(resultSet!=null){
                while(resultSet.next()){
                    //保存数据
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closePreRes();
        }
    }

    public EnvParam selectEnvParam(String rid){
        if(conn==null) return null;
        final String sql="select * from rock_env where rid=?";
        EnvParam envParam=new EnvParam();
        try{
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,rid);
            preparedStatement.executeQuery();
            if(resultSet!=null){
                while(resultSet.next()){
                    envParam.setId(resultSet.getString("id"));
                    envParam.setRid(resultSet.getString("rid"));
                    envParam.setPm10(resultSet.getString("pm10"));
                    envParam.setPm25(resultSet.getString("pm25"));
                    envParam.setTime(resultSet.getString("time"));
                    envParam.setSmoke(resultSet.getString("smoke"));
                    envParam.setHumidity(resultSet.getString("humidity"));
                    envParam.setTemperature(resultSet.getString("temperature"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closePreRes();
        }
        return envParam;
    }

    //更新操作
    public int updata(EnvParam envParam){
        int res=-1;
        if(conn==null) return res ;
        final String sql="update rock_env set time =?, humidity =?, temperature =?, pm10 =?, PM25 =?, smoke =? where rid=?";
        try{
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,envParam.getTime());
            preparedStatement.setString(2,envParam.getHumidity());
            preparedStatement.setString(3,envParam.getTemperature());
            preparedStatement.setString(4,envParam.getPm10());
            preparedStatement.setString(5,envParam.getPm25());
            preparedStatement.setString(6,envParam.getSmoke());
            preparedStatement.setString(7,envParam.getRid());

            res=preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closePreRes();
        }
        return res;
    }

    //插入环境数据
    public int insert(EnvParam envParam){
        int res=-1;
        if(conn==null  ) return res;
        try {
            boolean isClosed =conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        final String sql="insert into rock_env (rid, time, humidity, temperature, pm10, PM25, smoke) values(?,?,?,?,?,?,?)";
        try{
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,envParam.getRid());
            preparedStatement.setString(2,envParam.getRid());
            preparedStatement.setString(3,envParam.getRid());
            preparedStatement.setString(4,envParam.getRid());
            preparedStatement.setString(5,envParam.getRid());
            preparedStatement.setString(6,envParam.getRid());
            preparedStatement.setString(7,envParam.getRid());

            res=preparedStatement.executeUpdate(); //返回1，执行成功
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closePreRes();
        }
        return res;
    }



    //删除操作
    public int delete(String rid){
        int res=-1;
        if(conn==null) return res;
        final String sql="delete from rock_env where rid=?";
        try{
            preparedStatement=conn.prepareStatement(sql);
            preparedStatement.setString(1,rid);
            res=preparedStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            closePreRes();
        }
        return res;
    }


    private void closePreRes(){
        try{
            if(resultSet!=null){
                resultSet.close();
                resultSet=null;
            }
            if(preparedStatement!=null){
                preparedStatement.close();
                preparedStatement=null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}






