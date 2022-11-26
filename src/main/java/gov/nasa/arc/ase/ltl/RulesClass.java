package gov.nasa.arc.ase.ltl;

public class RulesClass {
    public static void main(String[] paramArrayOfString) {
        System.out.println(getRules());
    }

    public static String getRules() {
        return "p/\\p\np\n\np/\\true\np\n\np/\\false\nfalse\n\np/\\!p\nfalse\n\np\\/p\np\n\np\\/true\ntrue\n\np\\/false\np\n\np\\/!p\ntrue\n\n( X p ) U ( X q )\nX ( p U q )\n\n( p V q ) /\\ ( p V r )\np V ( q /\\ r )\n\n( p V r ) \\/ ( q V r )\n( p \\/ q ) V r\n\n( X p ) /\\ ( X q )\nX ( p /\\ q )\n\nX true\ntrue\n\np U false\nfalse\n\n[] <> p \\/ [] <> q\n[] <> ( p \\/ q )\n\n<> X p\nX <> p\n\n[] [] <> p\n[] <> p\n\n<> [] <> p\n[] <> p\n\nX [] <> p\n[] <> p\n\n<> ( p /\\ [] <> q )\n( <> p ) /\\ ( [] <> q )\n\n[] ( p \\/ [] <> q )\n( [] p ) \\/ ( [] <> q )\n\nX ( p /\\ [] <> q )\n( X p ) /\\ ( [] <> q )\n\nX ( p \\/ [] <> q )\n( X p ) \\/ ( [] <> q )";
    }
}
