package Server;

import Util.Message;
import Util.User;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerImpl implements ServerInterface {
    private static Map<UUID, String> registeredClients = new ConcurrentHashMap<>();
    private static Message currentMultiCastMessage;
    private static Registry registry;

    static {
        currentMultiCastMessage = new Message("empty");
    }

    @Override
    public void getMessageFromClient(String message, UUID clientUuid) {
        System.out.println("New message from " + registeredClients.get(clientUuid) + ": " + message);
        currentMultiCastMessage.value = message;
        try {
            registry.rebind("currentMultiCastMessage", currentMultiCastMessage);
            Thread.sleep(6000);
            currentMultiCastMessage.value = "empty";
            registry.rebind("currentMultiCastMessage", currentMultiCastMessage);
        } catch (Throwable cause) {
            System.out.println("Oops, something goes wrong!\nCause: " + cause.getMessage());
        }

    }

    @Override
    public void registerClient(User user) {
        registeredClients.put(user.getUuid(), user.getName());
    }

    @Override
    public void unRegisterClient(User user) {
        registeredClients.remove(user.getUuid());
    }

    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        ServerInterface server = new ServerImpl();

        try {
            ServerInterface stubObject = (ServerInterface) UnicastRemoteObject.exportObject(server, 0);
            registry = LocateRegistry.createRegistry(1099);
            registry.bind("Server", stubObject);
            registry.bind("currentMultiCastMessage", currentMultiCastMessage);
            System.out.println("Server is up!");
        } catch (Throwable cause) {
            System.out.println("Oops, something goes wrong!\nCause: " + cause.getMessage());
        }
    }
}
