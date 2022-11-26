package gov.nasa.arc.ase.ltl;

class Pool {
    private static int last_assigned = 0;
    private static boolean stopped = false;

    public static int assign() {
        if (!stopped)
            return last_assigned++;
        return last_assigned;
    }

    public static void stop() {
        stopped = true;
        last_assigned--;
    }

    public static void reset_static() {
        last_assigned = 0;
        stopped = false;
    }
}
