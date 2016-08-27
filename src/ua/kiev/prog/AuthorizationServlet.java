package ua.kiev.prog;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;

//сервлет /autoriz
public class AuthorizationServlet extends HttpServlet {

    //Map c логинами и паролями
    private UserMap userMap = UserMap.getInstance();

    public AuthorizationServlet() {
        userMap.add("admin", new User("admin"));
    }

    // logout - по запросу для логина устанавливает флаг User в false
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        String login = request.getParameter("login"); //парсим логин
        try(OutputStream os = response.getOutputStream()) {
            if (userMap.isContainLogin(login)) { //если есть пользователь с таким логином
                User user = userMap.getUser(login); //вытаскиваем пользователя по логину
                user.setOnline(false); //устанавливаем флаг оффлайн
                os.write("logout".getBytes()); //пишем в исходящий поток подтверждение
            }
            else {
                os.write("logoutError".getBytes()); //если такого пользователя не нашлось
            }
        }
    }

    //авторизация + если пароль пришел = "" - проверка есть ли закой пользователь
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login"); //парсим логин
        String password = request.getParameter("pas"); // парсим пароль
        try(OutputStream os = response.getOutputStream()) {
            if (userMap.isContainLogin(login)) { //если есть пользователь с таким логином
                if(password.equals("")){ //если пароль пустой, значит запрос на проверку сущетсвования пользователя с таким логином
                    os.write("ok".getBytes()); //получаем массив байт из строки и пишем в исходящий поток подтверждение проверки
                    return;
                }
                User user = userMap.getUser(login); // вытаскиваем пользователя по логину
                if (password.equals(user.getPassWord())) { // если пароль верный
                    user.setOnline(true);
                    os.write("ok".getBytes());//получаем массив байт из строки и пишем в исходящий поток подтверждение авторизации
                    //HttpSession session = request.getSession(true); //устанавливаем значение session в true
                    //session.setAttribute("user_login", login); //записываем логин в атрибут "user_login"
                } else {
                    os.write("Invalid passWord".getBytes()); //если пароль неверный
                }
            } else {
                os.write("Invalid login".getBytes()); //если такого пользователя нет
            }
        }
    }
}