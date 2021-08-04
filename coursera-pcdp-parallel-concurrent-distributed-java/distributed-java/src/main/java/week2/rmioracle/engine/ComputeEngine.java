package week2.rmioracle.engine;

import week2.rmioracle.compute.Compute;
import week2.rmioracle.compute.Task;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ComputeEngine implements Compute {

    public ComputeEngine() {
        super();
    }

    public static void main(String[] args) {

        // The main method's first task is to create and install a security manager, which protects access
        // to system resources from untrusted downloaded code running within the Java virtual machine.
        // A security manager determines whether downloaded code has access to the local file system or can perform any other privileged operations.
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "Compute";
            Compute engine = new ComputeEngine();
            Compute stub = (Compute) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            System.out.println("week2.rmioracle.engine.ComputeEngine bound");
        } catch (Exception e) {
            System.err.println("week2.rmioracle.engine.ComputeEngine exception:");
            e.printStackTrace();
        }
    }

    public <T> T executeTask(Task<T> t) {
        return t.execute();
    }
}
