package Server;

import Util.Message;
import Util.User;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//     реализуем наш интерфейс для создания объекта заглушки для передачи клиентам
public class ServerImpl implements ServerInterface {
    private static Map<UUID, String> registeredClients = new ConcurrentHashMap<>();     // для хранения зарегистрированных пользователей
    private static Message currentMultiCastMessage;  // мульти-каст сообщение
    private static Registry registry;

    static {
        currentMultiCastMessage = new Message("empty");     // инициализация мульти-каст сообщения
    }

    public static void main(String[] args) {        // запуск осуществляется с VM-option '-Djava.security.policy=.app.policy'

        // проверка безопасности подключения (корневой файл .app.policy)
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        //  создаем remote-объект, реализующий интерфейс Remote
        ServerInterface server = new ServerImpl();

        try {
            ServerInterface stubObject = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);     // определяем заглушку remote-объекта, с которой будут работать клиенты
            registry = LocateRegistry.createRegistry(1099);     // определяем регистр (сервис для именования remote-объектов)
            registry.bind("Server", stubObject);               // регистрируем заглушку в регистр для сообщений Клиент->Сервер
            registry.bind("currentMultiCastMessage", currentMultiCastMessage);      //    регистрируем заглушку для передачи мульти-каст сообщений через сервис 'CheckingNewMessagesService'
            System.out.println("Server is up!");
        } catch (Throwable cause) {
            System.out.println("Oops, something goes wrong! Server has not started, cause: " + cause.getMessage());
        }
    }

    @Override
    public void getMessageFromClient(String message, UUID clientUuid) {         // метод для передачи сообщений Клиент->Сервер
        currentMultiCastMessage.value = "New message from '" + registeredClients.get(clientUuid) + "': " + message;
        System.out.println(currentMultiCastMessage.value);      // вывод сообщения клиента на экран сервера
        try {
            registry.rebind("currentMultiCastMessage", currentMultiCastMessage);        //  перерегистрируем заглушку для передачи мульти-каст сообщения для всех клиентов
            Thread.sleep(3000);     // спим 3 секунды
            currentMultiCastMessage.value = "empty";     //     обнуляем мульти-каст сообщение
            registry.rebind("currentMultiCastMessage", currentMultiCastMessage);        //перерегистрируем заглушку для передачи мульти-каст сообщения
        } catch (Throwable cause) {
            System.out.println("Oops, something goes wrong! Server exception, cause: " + cause.getMessage());
        }

    }

    @Override
    public void registerClient(User user) {
        registeredClients.put(user.getUuid(), user.getName());
        System.out.println("User " + user.getName() + " has been registered");
    }

    @Override
    public void unRegisterClient(User user) {
        registeredClients.remove(user.getUuid());
        System.out.println("User " + user.getName() + " has been unregistered");
    }


}
