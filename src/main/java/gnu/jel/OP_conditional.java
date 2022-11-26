package gnu.jel;

class OP_conditional extends OP_function {
    OPlist trueList;
    OPlist falseList;

    boolean canInterpret() {
        return false;
    }

    void compile(ExpressionImage paramExpressionImage) {
        paramExpressionImage.asm_branch_start_true();
        this.trueList.compile(paramExpressionImage);
        paramExpressionImage.asm_branch_start_false();
        this.falseList.compile(paramExpressionImage);
        paramExpressionImage.asm_branch_end();
    }

    void interpret(OPlist paramOPlist) throws Throwable {
    }

    public boolean optimize(OPlist paramOPlist) throws Throwable {
        boolean bool;
        if (this.prev instanceof OP_load) {
            OPlist oPlist;
            boolean bool1 = ((Boolean) ((OP_load) this.prev).what).booleanValue();
            paramOPlist.remove(this.prev);
            bool = true;
            if (bool1) {
                oPlist = this.trueList;
            } else {
                oPlist = this.falseList;
            }
            oPlist.optimize();
            for (OP oP = oPlist.getFirst(); oP != null; oP = oP1) {
                OP oP1 = oP.next;
                oPlist.remove(oP);
                paramOPlist.addBefore(this, oP);
            }
            paramOPlist.remove(this);
        } else {
            bool = this.trueList.optimize();
            bool = !(!this.falseList.optimize() && !bool);
        }
        return bool;
    }

    public void setFalseList(OPlist paramOPlist) {
        this.falseList = paramOPlist;
    }

    public void setTrueList(OPlist paramOPlist) {
        this.trueList = paramOPlist;
    }

    public void setType(Class paramClass) {
        OP oP1 = this.trueList.getLast();
        if (oP1 instanceof OP_convert) {
            ((OP_convert) oP1).setType(paramClass);
        } else {
            this.trueList.addLast(new OP_convert(paramClass));
        }
        OP oP2 = this.falseList.getLast();
        if (oP2 instanceof OP_convert) {
            ((OP_convert) oP2).setType(paramClass);
        } else {
            this.falseList.addLast(new OP_convert(paramClass));
        }
    }

    public String toString() {
        return "[?" + this.trueList.toString() + ":" + this.falseList.toString() + "]";
    }
}
