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
	private int counter; //колличество сообщений в общем списке

	//добавить сообщение
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try(InputStream is = req.getInputStream()) {
			byte[] buf = new byte[req.getContentLength()]; //ContentLength может не прийти от браузера, переделать код в цикл.
			is.read(buf); //считываем в буфер
			Message msg = Message.fromJSON(new String(buf)); //получаем стоку из массива байт парсим из строки JSON сообщение
			if (msg != null) { //если сообщение есть
				msg.setCounter(counter); //нумеруем сообщение
				msgList.add(msg); //добавляем его в список сообщений
				counter++; //итеррируем счетчик
			}
			else
				resp.setStatus(400); // Bad request
		}
	}
}
