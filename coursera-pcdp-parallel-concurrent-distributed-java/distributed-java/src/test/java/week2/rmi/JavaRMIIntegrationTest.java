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

class JavaRMIIntegrationTest {

    @BeforeEach
    public void init() {
        Assertions.assertDoesNotThrow(() -> {
            MessengerServiceImpl messengerService = new MessengerServiceImpl();
            messengerService.createStubAndBind();
        });
    }

    @Test
    void whenClientSendsMessageToServer_thenServerSendsResponseMessage() {
        try {
            Registry registry = LocateRegistry.getRegistry();
            MessengerService<String> server = (MessengerService<String>) registry.lookup("MessengerService");

            String responseMessage = server.sendMessage("Client Message");

            String expectedMessage = "Server Message";
            assertEquals(responseMessage, expectedMessage);
        } catch (RemoteException | NotBoundException e) {
            fail("Exception Occurred: " + e);
        }
    }
}