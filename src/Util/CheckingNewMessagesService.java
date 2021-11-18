package Util;

import Server.ServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

// Сервис для остлеживания новых мультикаст-сообщений на сервере
public class CheckingNewMessagesService implements Runnable {
    private Registry registry;
    private Message currentMultiCastMessage;

    public CheckingNewMessagesService(Registry registry) {
        this.registry = registry;
    }

    @Override
    public void run() {
        while (true) {      // цикл для непрерывного отслеживания с интервалом в 3 секунды
            try {
                currentMultiCastMessage = (Message) registry.lookup("currentMultiCastMessage");
                if (!currentMultiCastMessage.value.equals("empty"))
                    System.out.println(currentMultiCastMessage.value);      // Вывод на экран мульти-каст сообщений, только если имеются новые (не 'empty')
                Thread.sleep(3000);     // спим 3 секунды
            } catch (Throwable cause) {
                System.out.println("Oops, something goes wrong!\nCause: " + cause.getMessage());
                return;
            }
        }
    }
}
