package ua.kiev.prog;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

// серавлеет /add
public class AddServlet extends HttpServlet {

	private static int counter; //счетчик сообщений в общем списке
	private MessageList msgList = MessageList.getInstance(); //полуаем ссылку на обьект со списком сообщений

	//добавить сообщение
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if(AuthorizationServlet.verifyCookie(req)){ //проверка Cookie
			try(InputStream is = req.getInputStream()) {
				Cookie[] cookies = req.getCookies();
				String cookieLogin = cookies[0].getValue(); //вытаскиваем login из Cookie
				byte[] buf = new byte[req.getContentLength()]; //ContentLength может не прийти при работе с браузером (переделать код в цикл)
				is.read(buf); //считываем в буфер
				Message msg = Message.fromJSON(new String(buf)); //получаем стоку из массива байт парсим из строки JSON сообщение
				if (msg != null) { //если сообщение есть
					msg.setFrom(cookieLogin); //устанавливаем поле от кого по логину из Cookie
					msg.setCounter(counter); //нумеруем сообщение
					msgList.add(msg); //добавляем его в список сообщений
					counter++; //итеррируем счетчик
				}
				else {
					resp.setStatus(400); // Bad request
				}
			}
		}
		else {
			resp.setStatus(401); // Unauthorized
		}
	}
}
