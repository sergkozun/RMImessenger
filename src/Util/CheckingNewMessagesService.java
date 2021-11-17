package Util;

import Server.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

public class CheckingNewMessagesService implements Runnable {
    private Registry registry;
    private Message currentMultiCastMessage;

    public CheckingNewMessagesService(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void run() {
        while (true) {
            try {
                currentMultiCastMessage = (Message) registry.lookup("currentMultiCastMessage");
                if (!currentMultiCastMessage.value.equals("empty")) System.out.println(currentMultiCastMessage.value);
                Thread.sleep(3000);
            } catch (Throwable cause) {
                System.out.println("Oops " + cause.getMessage());
                return;
            }
        }
    }
}
