package com.github.kelly.webserver;

import com.github.kelly.http.request.HttpRequest;
import com.github.kelly.http.response.HttpResponse;
import com.github.kelly.webserver.controller.Controller;
import com.github.kelly.webserver.dispatcher.DispatcherServlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * HTTP Request Handler
 * 프로그램이다.
 * URL 로 식별되고 HTTP 요청을 처리한다.
 * HTTP 호출에 의해 전송되는 데이터(URL 에 쿼리 스트링으로 코딩되는)를 수신하고 처리한다.
 * 핸들러가 데이터를 처리하고 나면 요청한 사람에게 보낼 응답을 만든다.
 */
public class HttpRequestHandler implements Runnable{

    // dispatcher 에게 request 를 주면서 request 에 맞는 응답을 주도록 위임한다.


    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket connection;

            while ((connection = serverSocket.accept()) != null) {
                InputStream inputStream = connection.getInputStream();
                // HttpRequest 에게 요청 메세지 처리하는 것을 맡긴다.
                HttpRequest httpRequest = new HttpRequest(inputStream);

                OutputStream outputStream = connection.getOutputStream();
                // 요청한 사람에게 보낼 응답을 만든다.
                HttpResponse httpResponse = new HttpResponse(outputStream);

                // FrontController
                DispatcherServlet dispatcherServlet = new DispatcherServlet(httpRequest);
//                Controller controller = dispatcherServlet.getController();    // 로직 작업 중
                final Controller controller = dispatcherServlet.dispatch();
                // 하... request 도 넘겨줘야지 ㅠ_ㅜ..........
                controller.process(httpRequest, httpResponse);
            }

        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}
