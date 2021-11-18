package Server;

import Util.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

// Определяем интерфейс и его методы, доступные для клиентов сервера
public interface ServerInterface extends Remote {

    void getMessageFromClient(String message, UUID uuid) throws RemoteException;

    void registerClient(User user) throws RemoteException;

    void unRegisterClient(User user) throws RemoteException;
}
