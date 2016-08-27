package ua.kiev.prog;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

//сервлет /reg
public class RegistrationServlet extends HttpServlet {

    private UserMap userMap = UserMap.getInstance();

    // регистрация
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login"); //парсим логин
        String passWord = request.getParameter("pas"); // парсим пароль
        try(OutputStream os = response.getOutputStream()) {
            boolean ifReg = userMap.add(login, new User(passWord)); //пробуем добавить пользователя
            if (ifReg) {//если пользователя с таким именем еще нет
                os.write("ok".getBytes());//получаем массив байт из строки и пишем в исходящий поток подтверждение регистрации
            } else {
                os.write("User with this login already exists!".getBytes());//если такой пользователь уже есть
            }
        }
    }

    //ввозвращает список пользователей + статус online/offline
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = userMap.getKeySet(); //полуаем множество ключей
        for (String s: keySet){ //проходимся по множеству, вытаскиваем пользователей, спрашиваем статус
            sb.append("Name: ");
            sb.append(s);
            sb.append(", status: ");
            if(userMap.getUser(s).isOnline()){
                sb.append("Online\n");
            }
            else{
                sb.append("offline\n");
            }
        }
        try(OutputStream os = response.getOutputStream()){
            os.write(sb.toString().getBytes());//получаем массив байт из строки и пишем в исходящий поток подтверждение авторизации
        }
    }
}
