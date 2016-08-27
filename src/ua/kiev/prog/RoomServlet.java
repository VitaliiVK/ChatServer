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
        String name = request.getParameter("name"); //парсим логин
        String create = request.getParameter("create"); // парсим флаг создать комнату
        try(OutputStream os = response.getOutputStream()) {
            if("true".equals(create)){ //если нужно создать комнату
                synchronized (listRoomNames) {
                    listRoomNames.add(name);
                   os.write("ok".getBytes());//получаем массив байт из строки и пишем в исходящий поток
                }
            }
            else{ //если нужно проверить наличие
                if(listRoomNames.contains(name)){
                    os.write("ok".getBytes());//получаем массив байт из строки и пишем в исходящий поток
                }
            }
        }
    }

    //получить список созданных комнат
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        for(String s: listRoomNames){
            sb.append(s);
            sb.append("\n");
        }
        try(OutputStream os = response.getOutputStream();) {
            os.write(sb.toString().getBytes());//получаем массив байт из строки и пишем в исходящий поток подтверждение авторизации
        }
    }
}
