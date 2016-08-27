package ua.kiev.prog;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

// серавлеет /add
public class AddServlet extends HttpServlet {
	//полуаем ссылку на обьект со списком сообщений
	private MessageList msgList = MessageList.getInstance();

	//добавить сообщение
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try(InputStream is = req.getInputStream()) {
			byte[] buf = new byte[req.getContentLength()]; //ContentLength может не прийти, переделать код в цикл.
			is.read(buf); //считываем в буфер
			Message msg = Message.fromJSON(new String(buf)); //получаем стоку из массива байт парсим из строки JSON сообщение
			if (msg != null) //если сообщение есть
				msgList.add(msg); //добавляем его в список сообщений
			else
				resp.setStatus(400); // Bad request
		}
	}
}
