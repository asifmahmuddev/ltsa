package ic.doc.ltsa.lts;

import java.util.Enumeration;
import java.util.NoSuchElementException;

final class EventStateEnumerator implements Enumeration {
    EventState es;
    EventState list;

    EventStateEnumerator(EventState paramEventState) {
        this.es = paramEventState;
        if (paramEventState != null)
            this.list = paramEventState.list;
    }

    public boolean hasMoreElements() {
        return (this.es != null);
    }

    public Object nextElement() {
        if (this.es != null) {
            EventState eventState = this.es;
            if (this.es.nondet != null) {
                this.es = this.es.nondet;
            } else {
                this.es = this.list;
                if (this.es != null)
                    this.list = this.list.list;
            }
            return eventState;
        }
        throw new NoSuchElementException("EventStateEnumerator");
    }
}
