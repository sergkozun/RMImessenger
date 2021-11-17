package Util;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MessageInterface extends Remote {
    String value() throws RemoteException;
}
