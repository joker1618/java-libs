package stuff;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.junit.Test;
import xxx.joker.libs.javalibs.repository.entity.JkDefaultEntity;
import xxx.joker.libs.javalibs.repository.entity.JkEntityField;
import xxx.joker.libs.javalibs.utils.JkConverter;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static xxx.joker.libs.javalibs.utils.JkConsole.display;

public class ASSS {

    @Test
    public void tres34t() throws Exception {

        AA aa = new AA();
        aa.setBb(new BB(78));

        Method mget = AA.class.getMethod("getBb");
        BB gotBB = (BB) mget.invoke(aa);
        display(gotBB.toString());

        BB bb = new BB(5);

        Method mset = AA.class.getMethod("setBb", BB.class);
        mset.invoke(aa, bb);

        gotBB = (BB) mget.invoke(aa);
        display(gotBB.toString());
    }

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


    static class AA extends JkDefaultEntity {
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

        public BB getBb() {
            return bb;
        }

        public void setBb(BB bb) {
            this.bb = bb;
        }

        @Override
        public String getPrimaryKey() {
            return s;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
        }
    }

    static class BB extends JkDefaultEntity {
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

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
        }
    }

    static class CC extends JkDefaultEntity {
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

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
        }
    }
}
