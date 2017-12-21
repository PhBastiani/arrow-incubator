package arrow.kindedj.fromkindedj;

import io.kindedj.Hk;

public class KJDataclassHK {
    private KJDataclassHK() {
    }

    public static <A> String show(Hk<KJDataclassHK, A> hk) {
        return ((KJDataclass1<A>) hk).a.toString();
    }

    public static final class KJDataclass1<A> implements Hk<KJDataclassHK, A> {
        public final A a;

        public KJDataclass1(A a) {
            this.a = a;
        }
    }
}
