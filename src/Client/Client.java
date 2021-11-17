package Client;

import Server.ServerInterface;
import Util.CheckingNewMessagesService;
import Util.User;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.UUID;

public class Client {
    public static void main(String[] args) throws RemoteException {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        Registry registry = null;
        ServerInterface stubObject = null;

        try {
            registry = LocateRegistry.getRegistry("192.168.199.50");
            stubObject = (ServerInterface) registry.lookup("Server");
        } catch (Throwable cause) {
            System.out.println("Oops, something goes wrong!\nCause: " + cause.getMessage());
            return;
        }

        User user = new User();
        Scanner scn = new Scanner(System.in);
        System.out.println("Please enter your name:");
        user.setName(scn.nextLine());
        user.setUuid(UUID.randomUUID());
        stubObject.registerClient(user);
        System.out.println("For exit press: q");
        String q = "";
        Thread checkingNewMessages = new Thread(new CheckingNewMessagesService(registry));
        checkingNewMessages.start();
        while (!q.equals("q")) {
            System.out.println("Please enter your message:");
            q = scn.nextLine();
            stubObject.getMessageFromClient(q, user.getUuid());
        }
        scn.close();
        checkingNewMessages.interrupt();
        stubObject.unRegisterClient(user);
    }
}
