package gnu.jel;

class OPlist extends OP {
    protected int size = 0;

    void addBefore(OP paramOP1, OP paramOP2) {
        if (paramOP1 == this.next) {
            addFirst(paramOP2);
            return;
        }
        paramOP2.prev = paramOP1.prev;
        paramOP2.next = paramOP1;
        paramOP1.prev.next = paramOP2;
        paramOP1.prev = paramOP2;
        this.size++;
    }

    void addFirst(OP paramOP) {
        paramOP.prev = null;
        paramOP.next = null;
        if (this.next != null) {
            paramOP.next = this.next;
            this.next.prev = paramOP;
        }
        this.next = paramOP;
        this.size++;
    }

    void addLast(OP paramOP) {
        paramOP.prev = null;
        paramOP.next = null;
        if (this.prev != null) {
            paramOP.prev = this.prev;
            this.prev.next = paramOP;
        }
        this.prev = paramOP;
        if (this.next == null)
            this.next = paramOP;
        this.size++;
    }

    void compile(ExpressionImage paramExpressionImage) {
        for (OP oP = this.next; oP != null; oP = oP.next)
            oP.compile(paramExpressionImage);
    }

    OPlist cut_end(OP paramOP) {
        OP oP = paramOP;
        OPlist oPlist = new OPlist();
        while (oP != null) {
            OP oP1 = oP.next;
            remove(oP);
            oPlist.addLast(oP);
            oP = oP1;
        }
        return oPlist;
    }

    OP getFirst() {
        return this.next;
    }

    OP getLast() {
        return this.prev;
    }

    boolean optimize() {
        try {
            OP oP = this.next;
            boolean bool = false;
            while (oP != null) {
                OP oP1 = oP.next;
                bool = (!oP.optimize(this) && !bool) ? false : true;
                oP = oP1;
            }
            return bool;
        } catch (Throwable throwable) {
            return false;
        }
    }

    void remove(OP paramOP) {
        if (paramOP.prev != null) {
            paramOP.prev.next = paramOP.next;
        } else {
            this.next = paramOP.next;
        }
        if (paramOP.next != null) {
            paramOP.next.prev = paramOP.prev;
        } else {
            this.prev = paramOP.prev;
        }
        paramOP.next = null;
        paramOP.prev = null;
        this.size--;
    }

    int size() {
        return this.size;
    }

    public String toString() {
        OP oP = this.next;
        StringBuffer stringBuffer = new StringBuffer();
        while (oP != null) {
            stringBuffer.append(oP.toString());
            oP = oP.next;
        }
        return stringBuffer.toString();
    }
}
