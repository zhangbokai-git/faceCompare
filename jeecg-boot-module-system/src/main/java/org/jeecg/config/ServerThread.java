package org.jeecg.config;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
public class ServerThread extends Thread{
    Socket socket = null;
    InetAddress inetAddress=null;//接收客户端的连接

    public ServerThread(Socket socket,InetAddress inetAddress) {
        this.socket = socket;
        this.inetAddress=inetAddress;
    }

    @Override
    public void run() {
        InputStream inputStream = null;//字节输入流
        InputStreamReader inputStreamReader = null;//将一个字节流中的字节解码成字符
        BufferedReader bufferedReader = null;//为输入流添加缓冲
        OutputStream outputStream = null;//字节输出流
        OutputStreamWriter writer = null;//将写入的字符编码成字节后写入一个字节流
        try {
            inputStream = socket.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            bufferedReader = new BufferedReader(inputStreamReader);
            String info = null;//临时

            //循环读取客户端信息
            while ((info = bufferedReader.readLine()) != null) {
                //获取客户端的ip地址及发送数据
                System.out.println("服务器端接收："+"{'from_client':'"+socket.getInetAddress().getHostAddress()+"','data':'"+info+"'}");
            }

            socket.shutdownInput();//关闭输入流

            //响应客户端请求
            outputStream = socket.getOutputStream();
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write("{'to_client':'"+inetAddress.getHostAddress()+"','data':'我是服务器数据'}");
            writer.flush();//清空缓冲区数据
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //关闭资源
            try {
                if (writer != null) {
                    writer.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
