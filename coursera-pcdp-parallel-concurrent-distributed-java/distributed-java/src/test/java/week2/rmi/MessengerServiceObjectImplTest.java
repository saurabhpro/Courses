package week2.rmi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class MessengerServiceObjectImplTest {


    @BeforeEach
    public void init() {
        Assertions.assertDoesNotThrow(() -> {
            MessengerServiceObjectImpl messengerService = new MessengerServiceObjectImpl();
            messengerService.createStubAndBind();
        });
    }

    @Test
    void whenClientSendsMessageToServer_thenServerSendsResponseMessage() {
        try {
            Registry registry = LocateRegistry.getRegistry();
            MessengerService<Message> server = (MessengerService<Message>) registry.lookup("MessengerService");

            Message responseMessage = server.sendMessage(new Message("Hello", "csv"));

            Message expectedMessage = new Message("Hi Saurabh", "pdf");
            assertEquals(responseMessage, expectedMessage);
        } catch (RemoteException | NotBoundException e) {
            fail("Exception Occurred: " + e);
        }
    }
}