package ua.kiev.prog;

import javax.servlet.http.*;
import java.io.IOException;
import java.io.OutputStream;

// серавлеет /get
public class GetListServlet extends HttpServlet {

	private MessageList msgList = MessageList.getInstance();
	//пользователь в цикле вызывает это сервлет, сервлет возвращает новые сообщения адресованные ему или в чат в котором он находится
	//from - записать сообщения начиная с индекса
	//too - записать сообщения отправленные для кого
	//login - имя пользователя (нужно для приватной переписки)
	//priv - флаг приватная переписка
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		if(AuthorizationServlet.verifyCookie(req)) {/// проверка валидности сессии, если проверку не прошли не отправляем ничего
			HttpSession session = req.getSession(false);
			String login = (String)session.getAttribute("user_login");
			String fromStr = req.getParameter("from"); //получаем параметр from в котором указано с какого сообщения отправить историю
			String tooStr = req.getParameter("too"); //получаем параметр too в котором указан адресат сообщения
			String priv = req.getParameter("priv");//получаем флаг приватные сообщения
			int from = Integer.parseInt(fromStr);
			String json = msgList.toJSON(from, login, tooStr, priv); //получаем строку в формате JSON в которой записан список сообщений
			if (json != null) {//если есть что отсылать
				try (OutputStream os = resp.getOutputStream()) {
					os.write(json.getBytes());//получаем массив байт из строки и пишем в исходящий поток
				}
			}
		}
	}
}
