package Util;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class Message implements MessageInterface, Serializable {
    public String value;

    public Message(String value) {
        this.value = value;
    }

    @Override
    public String value() throws RemoteException {
        return value;
    }
}
