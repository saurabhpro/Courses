package week2.rmi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class JavaRMIIntegrationTest {

    @BeforeEach
    public void init() {
        Assertions.assertDoesNotThrow(() -> {
            var messengerService = new MessengerServiceImpl();
            messengerService.createStubAndBind();
        });
    }

    @Test
    void whenClientSendsMessageToServer_thenServerSendsResponseMessage() {
        try {
            var registry = LocateRegistry.getRegistry();
            var server = (MessengerService<String>) registry.lookup("MessengerService");

            var responseMessage = server.sendMessage("Client Message");

            var expectedMessage = "Server Message";
            assertEquals(responseMessage, expectedMessage);
        } catch (RemoteException | NotBoundException e) {
            fail("Exception Occurred: " + e);
        }
    }
}