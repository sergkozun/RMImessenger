package Client;

import Server.ServerInterface;
import Util.CheckingNewMessagesService;
import Util.User;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.UUID;

// Сделал второго клиента для проверок, с повторением кода, на скорую руку.
public class Client2 {
    public static void main(String[] args) throws RemoteException {     // запуск осуществляется с VM-option '-Djava.security.policy=.app.policy'

        // проверка безопасности подключения (корневой файл .app.policy)
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry = null;
        ServerInterface stubObject = null;

        try {
            registry = LocateRegistry.getRegistry("192.168.237.120");        // получаем регистр от сервера
            stubObject = (ServerInterface) registry.lookup("Server");       // получаем интерфейс объекта сервера (заглушку) из регистра по имени
        } catch (Throwable cause) {
            System.out.println("Oops, something goes wrong!\nCause: " + cause.getMessage());
            return;
        }

        User user = new User();     // создаем юзера с уникальным UUID и именем
        Scanner scn = new Scanner(System.in);
        System.out.println("Please enter your name:");
        user.setName(scn.nextLine());       // клиент задает свое имя с клавиатуры
        user.setUuid(UUID.randomUUID());    // клиент получает уникальный идентификатор
        stubObject.registerClient(user);    // регистрируем клиента на сервере
        System.out.println("'For exit press: q'");
        String q = "";
        Thread checkingNewMessages = new Thread(new CheckingNewMessagesService(registry));  // создаем сервис для отслеживания новых мультикаст-сообщений
        checkingNewMessages.start();    // запускаем сервсис вторым потоком
        while (!q.equals("q")) {
            System.out.println("Please enter your message:");
            q = scn.nextLine();         // пока пользователь не ввел 'q', в бесконечном цикле ждем ввода нового сообщения
            stubObject.getMessageFromClient(q, user.getUuid());     // при вводе сообщения вызываем метод отправки на сервер и дальнейшего мультикаста
        }
        scn.close();        // освобождаем ресурсы
        checkingNewMessages.interrupt();    // отключаем второй поток (сервис для отслеживания сообщений)
        stubObject.unRegisterClient(user);  // удаляем клиента из базы сервера
    }

}
