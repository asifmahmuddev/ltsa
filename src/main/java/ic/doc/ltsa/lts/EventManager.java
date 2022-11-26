package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class EventManager implements Runnable {
    Hashtable clients = new Hashtable();
    Vector queue = new Vector();
    Thread athread;
    boolean stopped = false;

    public EventManager() {
        this.athread = new Thread(this);
        this.athread.start();
    }

    public synchronized void addClient(EventClient paramEventClient) {
        this.clients.put(paramEventClient, paramEventClient);
    }

    public synchronized void removeClient(EventClient paramEventClient) {
        this.clients.remove(paramEventClient);
    }

    public synchronized void post(LTSEvent paramLTSEvent) {
        this.queue.addElement(paramLTSEvent);
        notifyAll();
    }

    public void stop() {
        this.stopped = true;
    }

    private synchronized void dopost() {
        while (this.queue.size() == 0) {
            try {
                wait();
            } catch (InterruptedException interruptedException) {
            }
        }
        LTSEvent lTSEvent = this.queue.firstElement();
        Enumeration enumeration = this.clients.keys();
        while (enumeration.hasMoreElements()) {
            EventClient eventClient = enumeration.nextElement();
            eventClient.ltsAction(lTSEvent);
        }
        this.queue.removeElement(lTSEvent);
    }

    public void run() {
        while (!this.stopped)
            dopost();
    }
}
