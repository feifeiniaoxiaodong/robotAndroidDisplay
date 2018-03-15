package stonectr.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Sunshine on 2017/4/28.
 */

public class SocketReceiver {
    class SocketReceiveThread extends Thread
    {
        @Override
        public void run()
        {
            try {
                ServerSocket serivce = new ServerSocket(3200);
                while (true) {

                    Socket socket = serivce.accept();
                    OutputStream ou = socket.getOutputStream();
                    BufferedReader br =new BufferedReader(new InputStreamReader(socket.getInputStream())) ;
                    String line = null;
                    ou.write("getmessage".getBytes("gbk"));
                    ou.flush();
                    //半关闭socket
                    socket.shutdownOutput();
                    //获取客户端的信息
                    while ((line = br.readLine()) != null)
                    {
                        System.out.println("获取到输入信息"+line);
                        String[] s = line.split(" ");
                        if (s[0].equals("move"))
                        {
                            //执行移动指令
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
