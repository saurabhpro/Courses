package week2.rmioracle.compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * https://docs.oracle.com/javase/tutorial/rmi/overview.html
 */
public interface Compute extends Remote {
    <T> T executeTask(Task<T> t) throws RemoteException;
}