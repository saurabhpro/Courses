package week2.rmioracle.client;

import week2.rmioracle.compute.Compute;

import java.rmi.registry.LocateRegistry;

public class ComputePi {
    public static void main(String[] args) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            final var name = "Compute";
            final var registry = LocateRegistry.getRegistry(args[0]);
            final var comp = (Compute) registry.lookup(name);
            final var task = new Pi(Integer.parseInt(args[1]));
            final var pi = comp.executeTask(task);
            System.out.println(pi);
        } catch (Exception e) {
            System.err.println("ComputePi exception:");
            e.printStackTrace();
        }
    }
}