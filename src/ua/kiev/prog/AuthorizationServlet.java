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

        boolean isAuthoriz = verifyCookie(request);

        try (OutputStream os = response.getOutputStream()) {
            if (!password.equals("")) { //если пароль не пустой значит авторизация
                if (!isAuthoriz) { //если пользователь не залогинен можно логиниться
                    if (userMap.isContainLogin(login)) { //если есть такой логин в базе
                        User user = userMap.getUser(login); // вытаскиваем пользователя по логину
                        if (password.equals(user.getPassWord())) { // если пароль верный
                            user.setOnline(true);

                            HttpSession session = request.getSession(true); //устанавливаем значение session в true
                            session.setAttribute("user_login", login); //записываем логин в атрибут "user_login"

                            os.write("ok".getBytes());//получаем массив байт из строки и пишем в исходящий поток подтверждение авторизации
                        } else {
                            os.write("Invalid passWord".getBytes()); //если пароль неверный
                        }
                    } else {
                        os.write("Invalid login".getBytes()); //если такого пользователя нет
                    }
                } else {
                    os.write("You are logged already!".getBytes()); //если пользователь уже залогинен
                }
            } else { //если пароль пустой значит проверка существования ника
                if (isAuthoriz) { //если пользователь авторизован
                    if (userMap.isContainLogin(login)) {
                        os.write("ok".getBytes()); //получаем массив байт из строки и пишем в исходящий поток подтверждение проверки
                    } else {
                        os.write("Invalid login!".getBytes());
                    }
                } else {
                    os.write("Unauthorized request!".getBytes()); //если пользователь не представился
                }
            }
        }
    }

    // logout - по запросу устанавливает флаг ua.kiev.prog.User в false
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        try(OutputStream os = response.getOutputStream()) {
            if(AuthorizationServlet.verifyCookie(request)) {
                HttpSession session = request.getSession(false); //если сесия уже есть то вернет сессию, если ее нет то вернт null
                String login = (String)session.getAttribute("user_login");
                User user = userMap.getUser(login); //вытаскиваем пользователя по логину
                user.setOnline(false); //устанавливаем флаг оффлайн
                session.removeAttribute("user_login");
                os.write("logout".getBytes()); //пишем в исходящий поток подтверждение
            }
            else {
                os.write("logoutError".getBytes()); //если такого пользователя не нашлось
            }
        }
    }

    // проверка валидности сессии
    static boolean verifyCookie(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        if(session != null) {
            String login = (String) session.getAttribute("user_login");
            if (login != null && !"".equals(login)) {
                return true;
            }
        }
        return false;
    }
}




//работа с Cookie
/*

//создаем и отправляем
        Cookie theLogin = new Cookie("login", login);
        Cookie thePass = new Cookie("pass", password);
        response.addCookie(theLogin);
        response.addCookie(thePass);

//принимаем парсим
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length > 0) {
            String cookieLogin = cookies[0].getValue();
            String cookiePass= cookies[1].getValue();
            if (userMap.isContainLogin(cookieLogin)) { //если есть пользователь с таким логином
                ua.kiev.prog.User user = userMap.getUser(cookieLogin);
                if (cookiePass.equals(user.getPassWord())){
                    return true;
                }
            }
        }
        return false;
*/