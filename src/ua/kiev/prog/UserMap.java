package ua.kiev.prog;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//создаем список пользователей в формате синглтона, так как с обьектом будет работать много потоков и нужно
//синхронизировать доступ с MAP, чтобы 2 пользователя не смогли одновременно проверить что такого логина нет и
//зарегестрироваться под одним логином, в результате чего регистрация одного из них будет не успешна
public class UserMap {

    private static final UserMap userMap = new UserMap();
    private  Map<String, User> map = new HashMap<String, User>(); //список

    public static UserMap getInstance() { //метод возвращающий ссылку на единственный экземпляр класса
        return userMap;
    }

    private UserMap() {}// приватный пустой конструктор, значит обьекты этого класса создавать не сможем

    //синхронизированный метод добавить пользователя
    public synchronized boolean add(String login, User user) {
        if(!isContainLogin(login)) { //если пользователя с таким логином нет, регистрируем, возвращаем true
            map.put(login, user);
            return true;
        }
        else {
            return false;
        }
    }

    //синхронизированный метод проверить есть ли пользователь
    public synchronized boolean isContainLogin(String login){
        if(map.containsKey(login)){
            return true;
        }
        else{
            return false;
        }
    }

    //синхронизированный метод получить пользователя
    public synchronized User getUser(String login){
        return map.get(login);
    }

    //синхронизированный метод получить keySet
    public synchronized Set<String> getKeySet(){
        return map.keySet();
    }

}
