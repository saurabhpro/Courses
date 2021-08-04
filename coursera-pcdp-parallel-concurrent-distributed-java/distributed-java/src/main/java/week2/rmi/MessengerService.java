package week2.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * When two JVMs need to communicate, Java RMI is one option we have to make that happen.
 * https://itnext.io/java-rmi-for-pentesters-structure-recon-and-communication-non-jmx-registries-a10d5c996a79
 * @param <T>
 */
public interface MessengerService<T> extends Remote {

    T sendMessage(T clientMessage) throws RemoteException;
}