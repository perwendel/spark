package spark.callback;

public class Event {

    private boolean cancelled = false;
    private Event.Type type;

    Event(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void cancel() {
        if (type.cancelable) {
            this.cancelled = true;
        }
    }

    public enum Type {
        ROUTE_ADD(true),
        ROUTE_ADDED(false),
        FILTER_ADD(true),
        FILTER_ADDED(false),
        SERVER_STARTING(true),
        SERVER_STARTED(false),
        SERVER_STOPPING(true),
        SERVER_STOPPED(false);

        final boolean cancelable;

        Type(boolean cancelable) {
            this.cancelable = cancelable;
        }
    }

    public enum Priority {
        LOW, NORMAL, HIGH
    }

    static class Holder implements Comparable {

        private Priority priority;
        private Callbacks.ICallback callback;

        Holder(Priority priority, Callbacks.ICallback callback) {
            this.priority = priority;
            this.callback = callback;
        }

        Priority getPriority() {
            return priority;
        }

        Callbacks.ICallback getCallback() {
            return callback;
        }

        @Override
        public int compareTo(Object o) {
            if (this.getClass() != o.getClass()) {
                throw new ClassCastException();
            }
            return ((Holder) o).getPriority().ordinal() - this.getPriority().ordinal();
        }
    }

}
