package com.hacaller.mockserver4android;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.hacaller.mockserver4android.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ServerThread serverThread;
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        serverThread = new ServerThread();
        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serverThread.start();
            }
        });
    }


    public class ServerThread extends Thread {

        @Override
        public void run() {
            startServer();
        }

        public void startServer(){
            int port = 3000;
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                Log.d("MockServer at", String.valueOf(port));
                getIPAddress(true);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                    String s;
                    StringBuilder builder = new StringBuilder();
                    while ((s = in.readLine()) != null) {
                        Log.d("MockServer", s);
                        builder.append(s);

                        if (s.isEmpty()) {
                            break;
                        }
                    }
                    readURLPath(builder.toString(), out);
                    out.write(new Date().toString());
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (addr.isSiteLocalAddress()){
                        Log.d("MockServer at", addr.getHostAddress());
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    private void readURLPath(String request, BufferedWriter out){
        try {
            if (request.startsWith("POST")){

            } else if (request.startsWith("GET")) {
                out.write("HTTP/1.1 200 OK\r\n");
                out.write("Content-Type: text/html\r\n");
                out.write("\r\n");
                out.write(new Date().toString());
                out.flush();
            }
        } catch (IOException e){}
    }

}
