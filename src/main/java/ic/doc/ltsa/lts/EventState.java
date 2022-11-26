package ic.doc.ltsa.lts;

import ic.doc.extension.Relation;
import java.io.PrintStream;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class EventState {
    int event;
    int next;
    int machine;
    EventState list;
    EventState nondet;
    EventState path;

    public EventState(int paramInt1, int paramInt2) {
        this.event = paramInt1;
        this.next = paramInt2;
    }

    public Enumeration elements() {
        return new EventStateEnumerator(this);
    }

    public static EventState add(EventState paramEventState1, EventState paramEventState2) {
        if (paramEventState1 == null || paramEventState2.event < paramEventState1.event) {
            paramEventState2.list = paramEventState1;
            return paramEventState2;
        }
        EventState eventState = paramEventState1;
        for (; eventState.list != null && eventState.event != paramEventState2.event && paramEventState2.event >= eventState.list.event; eventState = eventState.list);
        if (eventState.event == paramEventState2.event) {
            EventState eventState1 = eventState;
            if (eventState1.next == paramEventState2.next)
                return paramEventState1;
            while (eventState1.nondet != null) {
                eventState1 = eventState1.nondet;
                if (eventState1.next == paramEventState2.next)
                    return paramEventState1;
            }
            eventState1.nondet = paramEventState2;
        } else {
            paramEventState2.list = eventState.list;
            eventState.list = paramEventState2;
        }
        return paramEventState1;
    }

    public static EventState remove(EventState paramEventState1, EventState paramEventState2) {
        if (paramEventState1 == null)
            return paramEventState1;
        if (paramEventState1.event == paramEventState2.event && paramEventState1.next == paramEventState2.next) {
            if (paramEventState1.nondet == null)
                return paramEventState1.list;
            paramEventState1.nondet.list = paramEventState1.list;
            return paramEventState1.nondet;
        }
        EventState eventState1 = paramEventState1;
        EventState eventState2 = paramEventState1;
        while (eventState1 != null) {
            EventState eventState3 = eventState1;
            EventState eventState4 = eventState1;
            while (eventState3 != null) {
                if (eventState3.event == paramEventState2.event && eventState3.next == paramEventState2.next) {
                    if (eventState1 == eventState3) {
                        if (eventState1.nondet == null) {
                            eventState2.list = eventState1.list;
                            return paramEventState1;
                        }
                        eventState1.nondet.list = eventState1.list;
                        eventState2.list = eventState1.nondet;
                        return paramEventState1;
                    }
                    eventState4.nondet = eventState3.nondet;
                    return paramEventState1;
                }
                eventState4 = eventState3;
                eventState3 = eventState3.nondet;
            }
            eventState2 = eventState1;
            eventState1 = eventState1.list;
        }
        return paramEventState1;
    }

    public static void printAUT(EventState paramEventState, int paramInt, String[] paramArrayOfString, PrintStream paramPrintStream) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            EventState eventState1 = eventState;
            while (eventState1 != null) {
                paramPrintStream.print("(" + paramInt + "," + paramArrayOfString[eventState1.event] + "," + eventState1.next + ")\n");
                eventState1 = eventState1.nondet;
            }
            eventState = eventState.list;
        }
    }

    public static int count(EventState paramEventState) {
        EventState eventState = paramEventState;
        byte b = 0;
        while (eventState != null) {
            EventState eventState1 = eventState;
            while (eventState1 != null) {
                b++;
                eventState1 = eventState1.nondet;
            }
            eventState = eventState.list;
        }
        return b;
    }

    public static boolean hasState(EventState paramEventState, int paramInt) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            EventState eventState1 = eventState;
            while (eventState1 != null) {
                if (eventState1.next == paramInt)
                    return true;
                eventState1 = eventState1.nondet;
            }
            eventState = eventState.list;
        }
        return false;
    }

    public static EventState offsetSeq(int paramInt1, int paramInt2, int paramInt3, EventState paramEventState) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            EventState eventState1 = eventState;
            while (eventState1 != null) {
                if (eventState1.next >= 0)
                    if (eventState1.next == paramInt2) {
                        eventState1.next = paramInt3;
                    } else {
                        eventState1.next += paramInt1;
                    }
                eventState1 = eventState1.nondet;
            }
            eventState = eventState.list;
        }
        return paramEventState;
    }

    public static int toState(EventState paramEventState, int paramInt) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            EventState eventState1 = eventState;
            while (eventState1 != null) {
                if (eventState1.next == paramInt)
                    return eventState1.event;
                eventState1 = eventState1.nondet;
            }
            eventState = eventState.list;
        }
        return -1;
    }

    public static int countStates(EventState paramEventState, int paramInt) {
        EventState eventState = paramEventState;
        byte b = 0;
        while (eventState != null) {
            EventState eventState1 = eventState;
            while (eventState1 != null) {
                if (eventState1.next == paramInt)
                    b++;
                eventState1 = eventState1.nondet;
            }
            eventState = eventState.list;
        }
        return b;
    }

    public static boolean hasEvent(EventState paramEventState, int paramInt) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            if (eventState.event == paramInt)
                return true;
            eventState = eventState.list;
        }
        return false;
    }

    public static boolean isAccepting(EventState paramEventState, String[] paramArrayOfString) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            if (paramArrayOfString[eventState.event].charAt(0) == '@')
                return true;
            eventState = eventState.list;
        }
        return false;
    }

    public static EventState firstCompState(EventState paramEventState, int paramInt, int[] paramArrayOfint) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            if (eventState.event == paramInt) {
                paramArrayOfint[eventState.machine] = eventState.next;
                return eventState.nondet;
            }
            eventState = eventState.list;
        }
        return null;
    }

    public static EventState moreCompState(EventState paramEventState, int[] paramArrayOfint) {
        paramArrayOfint[paramEventState.machine] = paramEventState.next;
        return paramEventState.nondet;
    }

    public static boolean hasTau(EventState paramEventState) {
        if (paramEventState == null)
            return false;
        return (paramEventState.event == 0);
    }

    public static boolean hasOnlyTau(EventState paramEventState) {
        if (paramEventState == null)
            return false;
        return (paramEventState.event == 0 && paramEventState.list == null);
    }

    public static boolean hasOnlyTauAndAccept(EventState paramEventState, String[] paramArrayOfString) {
        if (paramEventState == null)
            return false;
        if (paramEventState.event != 0)
            return false;
        if (paramEventState.list == null)
            return true;
        if (paramArrayOfString[paramEventState.list.event].charAt(0) != '@')
            return false;
        return (paramEventState.list.list == null);
    }

    public static EventState removeAccept(EventState paramEventState) {
        paramEventState.list = null;
        return paramEventState;
    }

    public static EventState addNonDetTau(EventState paramEventState, EventState[] paramArrayOfEventState, BitSet paramBitSet) {
        EventState eventState1 = paramEventState;
        EventState eventState2 = null;
        while (eventState1 != null) {
            EventState eventState = eventState1;
            while (eventState != null) {
                if (eventState.next > 0 && paramBitSet.get(eventState.next)) {
                    int[] arrayOfInt = nextState(paramArrayOfEventState[eventState.next], 0);
                    eventState.next = arrayOfInt[0];
                    for (byte b = 1; b < arrayOfInt.length; b++)
                        eventState2 = add(eventState2, new EventState(eventState.event, arrayOfInt[b]));
                }
                eventState = eventState.nondet;
            }
            eventState1 = eventState1.list;
        }
        if (eventState2 == null)
            return paramEventState;
        return union(paramEventState, eventState2);
    }

    public static boolean hasNonDet(EventState paramEventState) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            if (eventState.nondet != null)
                return true;
            eventState = eventState.list;
        }
        return false;
    }

    public static int[] localEnabled(EventState paramEventState) {
        EventState eventState = paramEventState;
        byte b = 0;
        while (eventState != null) {
            b++;
            eventState = eventState.list;
        }
        if (b == 0)
            return null;
        int[] arrayOfInt = new int[b];
        eventState = paramEventState;
        b = 0;
        while (eventState != null) {
            arrayOfInt[b++] = eventState.event;
            eventState = eventState.list;
        }
        return arrayOfInt;
    }

    public static void hasEvents(EventState paramEventState, BitSet paramBitSet) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            paramBitSet.set(eventState.event);
            eventState = eventState.list;
        }
    }

    public static int[] nextState(EventState paramEventState, int paramInt) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            if (eventState.event == paramInt) {
                EventState eventState1 = eventState;
                byte b1 = 0;
                while (eventState1 != null) {
                    eventState1 = eventState1.nondet;
                    b1++;
                }
                eventState1 = eventState;
                int[] arrayOfInt = new int[b1];
                for (byte b2 = 0; b2 < arrayOfInt.length;) {
                    arrayOfInt[b2] = eventState1.next;
                    eventState1 = eventState1.nondet;
                    b2++;
                }
                return arrayOfInt;
            }
            eventState = eventState.list;
        }
        return null;
    }

    public static EventState renumberEvents(EventState paramEventState, Hashtable paramHashtable) {
        EventState eventState1 = paramEventState;
        EventState eventState2 = null;
        while (eventState1 != null) {
            EventState eventState = eventState1;
            while (eventState != null) {
                int i = ((Integer) paramHashtable.get(new Integer(eventState.event))).intValue();
                eventState2 = add(eventState2, new EventState(i, eventState.next));
                eventState = eventState.nondet;
            }
            eventState1 = eventState1.list;
        }
        return eventState2;
    }

    public static EventState newTransitions(EventState paramEventState, Relation paramRelation) {
        EventState eventState1 = paramEventState;
        EventState eventState2 = null;
        while (eventState1 != null) {
            EventState eventState = eventState1;
            while (eventState != null) {
                Object object = paramRelation.get(new Integer(eventState.event));
                if (object != null)
                    if (object instanceof Integer) {
                        eventState2 = add(eventState2, new EventState(((Integer) object).intValue(), eventState.next));
                    } else {
                        Vector vector = (Vector) object;
                        for (Enumeration enumeration = vector.elements(); enumeration.hasMoreElements();)
                            eventState2 = add(eventState2, new EventState(((Integer) enumeration.nextElement()).intValue(), eventState.next));
                    }
                eventState = eventState.nondet;
            }
            eventState1 = eventState1.list;
        }
        return eventState2;
    }

    public static EventState offsetEvents(EventState paramEventState, int paramInt) {
        EventState eventState1 = paramEventState;
        EventState eventState2 = null;
        while (eventState1 != null) {
            EventState eventState = eventState1;
            while (eventState != null) {
                eventState.event = (eventState.event == 0) ? 0 : (eventState.event + paramInt);
                eventState = eventState.nondet;
            }
            eventState1 = eventState1.list;
        }
        return eventState2;
    }

    public static EventState renumberStates(EventState paramEventState, Hashtable paramHashtable) {
        EventState eventState1 = paramEventState;
        EventState eventState2 = null;
        while (eventState1 != null) {
            EventState eventState = eventState1;
            while (eventState != null) {
                boolean bool = (eventState.next < 0) ? true : ((Integer) paramHashtable.get(new Integer(eventState.next))).intValue();
                eventState2 = add(eventState2, new EventState(eventState.event, bool));
                eventState = eventState.nondet;
            }
            eventState1 = eventState1.list;
        }
        return eventState2;
    }

    public static EventState renumberStates(EventState paramEventState, MyIntHash paramMyIntHash) {
        EventState eventState1 = paramEventState;
        EventState eventState2 = null;
        while (eventState1 != null) {
            EventState eventState = eventState1;
            while (eventState != null) {
                boolean bool = (eventState.next < 0) ? true : paramMyIntHash.get(eventState.next);
                eventState2 = add(eventState2, new EventState(eventState.event, bool));
                eventState = eventState.nondet;
            }
            eventState1 = eventState1.list;
        }
        return eventState2;
    }

    public static EventState addTransToError(EventState paramEventState, int paramInt) {
        EventState eventState1 = paramEventState;
        EventState eventState2 = null;
        if (eventState1 != null && eventState1.event == 0)
            eventState1 = eventState1.list;
        int i = 1;
        while (eventState1 != null) {
            if (i < eventState1.event)
                for (int k = i; k < eventState1.event; k++)
                    eventState2 = add(eventState2, new EventState(k, -1));
            i = eventState1.event + 1;
            EventState eventState = eventState1;
            while (eventState != null) {
                eventState2 = add(eventState2, new EventState(eventState.event, eventState.next));
                eventState = eventState.nondet;
            }
            eventState1 = eventState1.list;
        }
        for (int j = i; j < paramInt; j++)
            eventState2 = add(eventState2, new EventState(j, -1));
        return eventState2;
    }

    public static EventState removeTau(EventState paramEventState) {
        if (paramEventState == null)
            return paramEventState;
        if (paramEventState.event != 0)
            return paramEventState;
        return paramEventState.list;
    }

    public static EventState tauAdd(EventState paramEventState, EventState[] paramArrayOfEventState) {
        EventState eventState1 = paramEventState;
        EventState eventState2 = null;
        if (eventState1 != null && eventState1.event == 0)
            eventState1 = eventState1.list;
        while (eventState1 != null) {
            EventState eventState = eventState1;
            while (eventState != null) {
                if (eventState.next != -1) {
                    EventState eventState3 = paramArrayOfEventState[eventState.next];
                    while (eventState3 != null) {
                        eventState2 = push(eventState2, new EventState(eventState1.event, eventState3.next));
                        eventState3 = eventState3.nondet;
                    }
                }
                eventState = eventState.nondet;
            }
            eventState1 = eventState1.list;
        }
        while (eventState2 != null) {
            paramEventState = add(paramEventState, eventState2);
            eventState2 = pop(eventState2);
        }
        return paramEventState;
    }

    public static void setActions(EventState paramEventState, BitSet paramBitSet) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            paramBitSet.set(eventState.event);
            eventState = eventState.list;
        }
    }

    public static EventState actionAdd(EventState paramEventState, EventState[] paramArrayOfEventState) {
        if (paramEventState == null || paramEventState.event != 0)
            return paramEventState;
        EventState eventState = paramEventState;
        while (eventState != null) {
            if (eventState.next != -1)
                paramEventState = union(paramEventState, paramArrayOfEventState[eventState.next]);
            eventState = eventState.nondet;
        }
        return paramEventState;
    }

    public static EventState union(EventState paramEventState1, EventState paramEventState2) {
        EventState eventState1 = paramEventState1;
        EventState eventState2 = paramEventState2;
        while (eventState2 != null) {
            EventState eventState = eventState2;
            while (eventState != null) {
                eventState1 = add(eventState1, new EventState(eventState.event, eventState.next));
                eventState = eventState.nondet;
            }
            eventState2 = eventState2.list;
        }
        return eventState1;
    }

    public static EventState transpose(EventState paramEventState) {
        EventState eventState1 = null;
        EventState eventState2 = paramEventState;
        while (eventState2 != null) {
            EventState eventState = eventState2;
            while (eventState != null) {
                eventState1 = add(eventState1, new EventState(eventState.next, eventState.event));
                eventState = eventState.nondet;
            }
            eventState2 = eventState2.list;
        }
        eventState2 = eventState1;
        while (eventState2 != null) {
            EventState eventState = eventState2;
            while (eventState != null) {
                int i = eventState.next;
                eventState.next = eventState.event;
                eventState.event = i;
                eventState = eventState.nondet;
            }
            eventState2 = eventState2.list;
        }
        return eventState1;
    }

    public static String[] eventsToNext(EventState paramEventState, String[] paramArrayOfString) {
        EventState eventState = paramEventState;
        byte b1 = 0;
        while (eventState != null) {
            eventState = eventState.nondet;
            b1++;
        }
        eventState = paramEventState;
        String[] arrayOfString = new String[b1];
        for (byte b2 = 0; b2 < arrayOfString.length; b2++) {
            arrayOfString[b2] = paramArrayOfString[eventState.event];
            eventState = eventState.nondet;
        }
        return arrayOfString;
    }

    public static String[] eventsToNextNoAccept(EventState paramEventState, String[] paramArrayOfString) {
        EventState eventState = paramEventState;
        byte b1 = 0;
        while (eventState != null) {
            if (paramArrayOfString[eventState.event].charAt(0) != '@')
                b1++;
            eventState = eventState.nondet;
        }
        eventState = paramEventState;
        String[] arrayOfString = new String[b1];
        for (byte b2 = 0; b2 < arrayOfString.length; b2++) {
            if (paramArrayOfString[eventState.event].charAt(0) != '@') {
                arrayOfString[b2] = paramArrayOfString[eventState.event];
            } else {
                b2--;
            }
            eventState = eventState.nondet;
        }
        return arrayOfString;
    }

    private static EventState push(EventState paramEventState1, EventState paramEventState2) {
        if (paramEventState1 == null) {
            paramEventState2.path = paramEventState2;
        } else {
            paramEventState2.path = paramEventState1;
        }
        return paramEventState1 = paramEventState2;
    }

    private static boolean inStack(EventState paramEventState) {
        return (paramEventState.path != null);
    }

    private static EventState pop(EventState paramEventState) {
        if (paramEventState == null)
            return paramEventState;
        EventState eventState = paramEventState;
        paramEventState = eventState.path;
        eventState.path = null;
        if (paramEventState == eventState)
            return null;
        return paramEventState;
    }

    public static EventState reachableTau(EventState[] paramArrayOfEventState, int paramInt) {
        EventState eventState1 = paramArrayOfEventState[paramInt];
        if (eventState1 == null || eventState1.event != 0)
            return null;
        BitSet bitSet = new BitSet(paramArrayOfEventState.length);
        bitSet.set(paramInt);
        EventState eventState2 = null;
        while (eventState1 != null) {
            eventState2 = push(eventState2, eventState1);
            eventState1 = eventState1.nondet;
        }
        while (eventState2 != null) {
            int i = eventState2.next;
            eventState1 = add(eventState1, new EventState(0, i));
            eventState2 = pop(eventState2);
            if (i != -1) {
                bitSet.set(i);
                EventState eventState = paramArrayOfEventState[i];
                if (eventState != null && eventState.event == 0)
                    while (eventState != null) {
                        if (!inStack(eventState) && (eventState.next < 0 || !bitSet.get(eventState.next)))
                            eventState2 = push(eventState2, eventState);
                        eventState = eventState.nondet;
                    }
            }
        }
        return eventState1;
    }

    private static EventState addtail(EventState paramEventState1, EventState paramEventState2) {
        paramEventState2.path = null;
        if (paramEventState1 != null)
            paramEventState1.path = paramEventState2;
        return paramEventState2;
    }

    private static EventState removehead(EventState paramEventState) {
        if (paramEventState == null)
            return paramEventState;
        EventState eventState = paramEventState;
        paramEventState = eventState.path;
        return paramEventState;
    }

    public static MyIntHash reachable(EventState[] paramArrayOfEventState) {
        byte b = 0;
        MyIntHash myIntHash = new MyIntHash(paramArrayOfEventState.length);
        EventState eventState = null;
        eventState = push(eventState, new EventState(0, 0));
        while (eventState != null) {
            int i = eventState.next;
            eventState = pop(eventState);
            if (!myIntHash.containsKey(i)) {
                myIntHash.put(i, b++);
                EventState eventState1 = paramArrayOfEventState[i];
                while (eventState1 != null) {
                    EventState eventState2 = eventState1;
                    while (eventState2 != null) {
                        if (eventState2.next >= 0 && !myIntHash.containsKey(eventState2.next))
                            eventState = push(eventState, eventState2);
                        eventState2 = eventState2.nondet;
                    }
                    eventState1 = eventState1.list;
                }
            }
        }
        return myIntHash;
    }

    public static int search(EventState paramEventState, EventState[] paramArrayOfEventState, int paramInt1, int paramInt2, int paramInt3) {
        EventState eventState1 = new EventState(0, paramInt1);
        EventState eventState2 = eventState1;
        EventState eventState3 = eventState1;
        byte b = 1;
        boolean bool = false;
        EventState[] arrayOfEventState = new EventState[paramArrayOfEventState.length + 1];
        while (eventState2 != null) {
            int i = eventState2.next;
            arrayOfEventState[i + 1] = eventState2;
            if (i < 0 || i == paramInt2) {
                b = -1;
                break;
            }
            EventState eventState = paramArrayOfEventState[i];
            if (eventState == null && i != paramInt3) {
                b = 0;
                break;
            }
            while (eventState != null) {
                EventState eventState6 = eventState;
                while (eventState6 != null) {
                    if (arrayOfEventState[eventState6.next + 1] == null) {
                        eventState6.machine = i;
                        eventState3 = addtail(eventState3, eventState6);
                        arrayOfEventState[eventState6.next + 1] = eventState1;
                    }
                    eventState6 = eventState6.nondet;
                }
                eventState = eventState.list;
            }
            eventState2 = removehead(eventState2);
        }
        if (eventState2 == null)
            return b;
        EventState eventState4 = null;
        EventState eventState5 = eventState2;
        while (eventState5.next != paramInt1) {
            eventState4 = push(eventState4, eventState5);
            eventState5 = arrayOfEventState[eventState5.machine + 1];
        }
        paramEventState.path = eventState4;
        return b;
    }

    public static void printPath(EventState paramEventState, String[] paramArrayOfString, LTSOutput paramLTSOutput) {
        EventState eventState = paramEventState;
        while (eventState != null) {
            paramLTSOutput.outln("\t" + paramArrayOfString[eventState.event]);
            eventState = pop(eventState);
        }
    }

    public static Vector getPath(EventState paramEventState, String[] paramArrayOfString) {
        EventState eventState = paramEventState;
        Vector vector = new Vector();
        while (eventState != null) {
            vector.addElement(paramArrayOfString[eventState.event]);
            eventState = pop(eventState);
        }
        return vector;
    }
}
