package ic.doc.ltsa.lts;

public class PrintTransitions {
    CompactState sm;

    public PrintTransitions(CompactState paramCompactState) {
        this.sm = paramCompactState;
    }

    public void print(LTSOutput paramLTSOutput, int paramInt) {
        byte b1 = 0;
        paramLTSOutput.outln("Process:");
        paramLTSOutput.outln("\t" + this.sm.name);
        paramLTSOutput.outln("States:");
        paramLTSOutput.outln("\t" + this.sm.maxStates);
        paramLTSOutput.outln("Transitions:");
        paramLTSOutput.outln("\t" + this.sm.name + " = Q0,");
        for (byte b2 = 0; b2 < this.sm.maxStates; b2++) {
            paramLTSOutput.out("\tQ" + b2 + "\t= ");
            EventState eventState = EventState.transpose(this.sm.states[b2]);
            if (eventState == null) {
                if (b2 == this.sm.endseq) {
                    paramLTSOutput.out("END");
                } else {
                    paramLTSOutput.out("STOP");
                }
                if (b2 < this.sm.maxStates - 1) {
                    paramLTSOutput.outln(",");
                } else {
                    paramLTSOutput.outln(".");
                }
            } else {
                paramLTSOutput.out("(");
                while (eventState != null) {
                    b1++;
                    if (b1 > paramInt) {
                        paramLTSOutput.outln("EXCEEDED MAXPRINT SETTING");
                        return;
                    }
                    String[] arrayOfString = EventState.eventsToNext(eventState, this.sm.alphabet);
                    Alphabet alphabet = new Alphabet(arrayOfString);
                    paramLTSOutput.out(alphabet.toString() + " -> ");
                    if (eventState.next < 0) {
                        paramLTSOutput.out("ERROR");
                    } else {
                        paramLTSOutput.out("Q" + eventState.next);
                    }
                    eventState = eventState.list;
                    if (eventState == null) {
                        if (b2 < this.sm.maxStates - 1) {
                            paramLTSOutput.outln("),");
                            continue;
                        }
                        paramLTSOutput.outln(").");
                        continue;
                    }
                    paramLTSOutput.out("\n\t\t  |");
                }
            }
        }
    }
}
