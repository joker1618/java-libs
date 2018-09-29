package stuff;

import org.junit.Test;
import xxx.joker.libs.javalibs.datamodel.entity.JkComparableEntity;
import xxx.joker.libs.javalibs.datamodel.entity.JkEntityField;
import xxx.joker.libs.javalibs.utils.JkConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class ASSS {

    @Test
    public void trest() {
        CC cc1 = new CC("fede");
        CC cc2 = new CC("ninoll");
        CC cc3 = new CC("delo");
        BB bb1 = new BB(2);
        BB bb2 = new BB(21);
        BB bb3 = new BB(22);
        AA io = new AA("io");
        io.bb = bb1;
        io.bbset = JkConverter.toTreeSet(Arrays.asList(bb2, bb3));
        io.cc = cc1;
        io.cclist = Arrays.asList(cc2,cc3);
    }


    static class AA extends JkComparableEntity {
        @JkEntityField(index = 0)
        BB bb;
        @JkEntityField(index = 1, collectionType = BB.class)
        Set<BB> bbset;
        @JkEntityField(index = 2)
        CC cc;
        @JkEntityField(index = 3, collectionType = CC.class)
        List<CC> cclist;
       @JkEntityField(index = 4)
        String s;

        public AA() {
        }
        public AA(String s) {
            this.s=s;
        }


        @Override
        public String getPrimaryKey() {
            return s;
        }
    }

    static class BB extends JkComparableEntity {
        @JkEntityField(index = 0)
        int nummo;

        public BB() {
        }

        public BB(int nummo) {
            this.nummo = nummo;
        }

        @Override
        public String getPrimaryKey() {
            return nummo+"";
        }
    }

    static class CC extends JkComparableEntity {
        @JkEntityField(index = 0)
        String str;

        public CC() {
        }

        public CC(String str) {
            this.str = str;
        }

        @Override
        public String getPrimaryKey() {
            return str;
        }
    }
}
