package ua.kiev.prog;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

//сервлет /room
public class RoomServlet extends HttpServlet {

    private static final Set<String> listRoomNames = new HashSet<>();

    //проверка наличия и создание комнаты, если create = "true" то нужно создать, если если create = "false" то проверяем есть ли такая комната
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        if(AuthorizationServlet.verifyCookie(request)) { //// проверка валидности сессии
            String name = request.getParameter("name"); //парсим имя комнаты
            String create = request.getParameter("create"); // парсим флаг создать комнату
            if ("true".equals(create)) { //если нужно создать комнату
                synchronized (listRoomNames) {
                    listRoomNames.add(name);
                    sb.append("isCreated");
                }
            }
            else if(listRoomNames.contains(name)){//если нужно проверить наличие
                    sb.append("isExist");
            }
            else{
                sb.append("notExist");
            }
        }
        else {
            sb.append("Unauthorized request!");
        }
        try(OutputStream os = response.getOutputStream()){
            os.write(sb.toString().getBytes());//получаем массив байт из строки и пишем в исходящий поток
        }
    }

    //получить список созданных комнат
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        if(AuthorizationServlet.verifyCookie(request)){//// проверка валидности сессии
            for(String s: listRoomNames){
                sb.append(s);
                sb.append("\n");
            }
        }
        else {
            sb.append("Unauthorized request!");
        }
        try(OutputStream os = response.getOutputStream();) {
            os.write(sb.toString().getBytes());//получаем массив байт из строки и пишем в исходящий поток подтверждение авторизации
        }
    }
}



//Cookie[] cookies = request.getCookies();
//if(cookies!=null && cookies.length>0) {
//    for (Cookie cookie : cookies) {
//       cookie.getName();
//        cookie.getValue();
//        System.out.print(cookie.getName()+"=");
//        System.out.println(cookie.getValue());
//    }
//}
