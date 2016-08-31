package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;

//сервлет /autoriz
public class AuthorizationServlet extends HttpServlet {

    //Map c логинами и паролями
    private static UserMap userMap = UserMap.getInstance();

    public AuthorizationServlet() {
        userMap.add("admin", new User("admin")); //тестовый акаунт админа
    }

    //авторизация + если пароль пришел = "" - проверка есть ли закой пользователь
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String login = request.getParameter("login"); //парсим логин
        String password = request.getParameter("pas"); // парсим пароль

        try(OutputStream os = response.getOutputStream()) {
            if (userMap.isContainLogin(login)) { //если есть пользователь с таким логином
                if(password.equals("")){ //если пароль пустой, значит запрос на проверку сущетсвования пользователя с таким логином
                    if(AuthorizationServlet.verifyCookie(request)) {//проверка Cookie
                        os.write("ok".getBytes()); //получаем массив байт из строки и пишем в исходящий поток подтверждение проверки
                    }
                    else{
                        os.write("Authorization problem".getBytes());
                    }
                    return;
                }
                User user = userMap.getUser(login); // вытаскиваем пользователя по логину
                if (password.equals(user.getPassWord())) { // если пароль верный
                    user.setOnline(true);

                    //создаем и отправляем
                    Cookie theLogin = new Cookie("login", login);
                    Cookie thePass = new Cookie("pass", password);
                    response.addCookie(theLogin);
                    response.addCookie(thePass);

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

    // logout - по запросу для логина устанавливает флаг User в false
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        try(OutputStream os = response.getOutputStream()) {
            if(AuthorizationServlet.verifyCookie(request)) {
                Cookie[] cookies = request.getCookies();
                String cookieLogin = cookies[0].getValue();
                User user = userMap.getUser(cookieLogin); //вытаскиваем пользователя по логину
                user.setOnline(false); //устанавливаем флаг оффлайн
                os.write("logout".getBytes()); //пишем в исходящий поток подтверждение
            }
            else {
                os.write("logoutError".getBytes()); //если такого пользователя не нашлось
            }
        }
    }

    // проверка на соотвествие Cookies
    public static boolean verifyCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length > 0) {
            String cookieLogin = cookies[0].getValue();
            String cookiePass= cookies[1].getValue();
            if (userMap.isContainLogin(cookieLogin)) { //если есть пользователь с таким логином
                User user = userMap.getUser(cookieLogin);
                if (cookiePass.equals(user.getPassWord())){
                    return true;
                }
            }
        }
        return false;
    }
}