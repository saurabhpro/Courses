package week2.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class MessengerServiceImpl implements MessengerService<String> {

    @Override
    public String sendMessage(String clientMessage) {
        if ("Client Message".equals(clientMessage)) {
            return "Server Message";
        }

        return null;
    }

    /**
     * The stub is what does the magic of communicating with the server over the underlying RMI protocol.
     * @throws RemoteException
     */
    public void createStubAndBind() throws RemoteException {

        // a value of zero indicates that we don't care which port exportObject uses, which is typical and so chosen dynamically.
        final var stub = (MessengerService<String>) UnicastRemoteObject.exportObject(this, 0);
        final var registry = LocateRegistry.createRegistry(1099);
        registry.rebind("MessengerService", stub);
    }
}