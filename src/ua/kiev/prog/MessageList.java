package ua.kiev.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

//класс для храниния сообщений
public class MessageList {
	
	private static final MessageList msgList = new MessageList(); //константа, в которую положим единственный обьект ua.kiev.prog.MessageList

	private final List<Message> list = new ArrayList<Message>(); //список с сообщениями ua.kiev.prog.MessageList
	
	public static MessageList getInstance() { //метод возвращающий ссылку на единственный экземпляр класса
		return msgList;
	}
  
  	private MessageList() {}// приватный пустой конструктор, значит обьекты этого класса создавать не сможем

	//синхронизированный метод добавить сообщение
	public synchronized void add(Message m) {
		list.add(m);
	}

	//синхронизированный метод который вернет строку в формате JSON в которой будет запиан наш лист с сообщениями + фильтр
	//n - записать сообщения начиная с индекса
	//too - записать сообщения отправленные для кого
	//login - имя пользователя (нужно для приватной переписки)
	//priv - флаг приватная переписка
	public synchronized String toJSON(int n, String login, String too, String priv) {
		List<Message> res = new ArrayList<Message>(); //составлям список к отправке
		for (int i = n; i < list.size(); i++) {
			Message message = list.get(i);
			if(priv.equals("true")){//если сообщеия приватные то присылаем только переписку 2х людей
				if (message.getTo().equals(too)&&message.getFrom().equals(login) ||
						message.getTo().equals(login)&&message.getFrom().equals(too)) {
					res.add(message);
				}
			}
			else { //если main-chat или chat-room то сообщения в чат в котором он находится
				if (message.getTo().equals(too)) { // || message.getTo().equals(login) //и лично пользователю
					res.add(message);
				}
			}
		}
		if (res.size() > 0) { //если есть что отравлять
			Gson gson = new GsonBuilder().create();
			return gson.toJson(res.toArray()); //преобразуем в JSON, возвращаем как результат работы метода
		}
		else {//если отправить нечего отправляем null
			return null;
		}
	}
}
