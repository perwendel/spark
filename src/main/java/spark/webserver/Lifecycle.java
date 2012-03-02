package spark.webserver;

import java.util.concurrent.CopyOnWriteArrayList;

public class Lifecycle {
    
    public enum Phase {
        Starting,
        Started,
        Stopping,
        Stopped
    }
    
    public interface Listener {
        void onPhase(Phase phase, SparkServer sparkServer);
    }
    
    private static CopyOnWriteArrayList<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    public static void addListener(Listener listener) {
        listeners.add(listener);
    }

    public static void serverStarting(SparkServer sparkServer) {
        serverOnPhase(Phase.Starting, sparkServer);
    }
    
    public static void serverStarted(SparkServer sparkServer) {
        serverOnPhase(Phase.Started, sparkServer);
    }
    
    public static void serverStopping(SparkServer sparkServer) {
        serverOnPhase(Phase.Stopping, sparkServer);
    }
    
    public static void serverStopped(SparkServer sparkServer) {
        serverOnPhase(Phase.Stopped, sparkServer);
    }
    
    private static void serverOnPhase(Phase phase, SparkServer sparkServer) {
        for(Listener listener : listeners)
            listener.onPhase(phase, sparkServer);
    }
}
