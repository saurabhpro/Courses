package week2.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class MessengerServiceObjectImpl implements MessengerService<Message> {


    public void createStubAndBind() throws RemoteException {
        final var stub = (MessengerService<String>) UnicastRemoteObject.exportObject(this, 0);
        final var registry = LocateRegistry.createRegistry(1099);
        registry.rebind("MessengerService", stub);
    }

    @Override
    public Message sendMessage(Message clientMessage) throws RemoteException {

        if ("Hello".equals(clientMessage.getMessageText())) {
            return new Message("Hi Saurabh", "pdf");
        }

        return null;
    }
}