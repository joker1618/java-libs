package trymodel;

import org.junit.Test;
import trymodel.entities3.Book;
import trymodel.entities3.Categ;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.utils.JkConvert;
import xxx.joker.libs.repository.JkRepoFile;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static xxx.joker.libs.core.utils.JkConsole.display;

public class Test3 extends JkRepoFile {

    public Test3() {
        super(TestModel.DB_FOLDER, "test3", "trymodel.entities3");
    }

    @Test
    public void testRepo3_A() {
        JkFiles.delete(TestModel.DB_FOLDER);

        Test3 test3 = new Test3();

        Set<Categ> categSet = test3.getDataSet(Categ.class);
        Set<Book> bookSet = test3.getDataSet(Book.class);

        Book b1 = new Book();
        b1.setTitle("primo");
        Book b2 = new Book();
        b2.setTitle("zoo");
        Book b3 = new Book();
        b3.setTitle("full book");
        Book b4 = new Book();
        b4.setTitle("insomnia");

        Categ cat1 = new Categ("cat1");
        Categ cat2 = new Categ("cat2");
        Categ cat3 = new Categ("cat3");

        categSet.add(cat1);
        bookSet.add(b1);

        b2.setCategs(JkConvert.toHashSet(Arrays.asList(cat1, cat2)));
        b2.setOthers(Collections.emptyList());
        b2.setBook(b4);
        bookSet.add(b2);

        b1.getCategs().add(cat3);
        b1.getCategs().add(cat1);
        b1.getOthers().add(b2);
        b1.getOthers().add(b3);
        b1.setBook(b4);

        printRepo(categSet, bookSet);

        test3.commit();
    }

    private void printRepo(Set<Categ> categSet, Set<Book> bookSet, String... prefix) {
        if(prefix.length == 1)  display("{}", prefix[0]);
        display("\n####################################################");
        display("CATEGS");
        categSet.forEach(c -> display("  - {}", c));
        display("BOOKS");
        bookSet.forEach(c -> display("  - {}", c));
        display("####################################################\n");
    }

    @Test
    public void testRepo3_B() {
        Test3 test3 = new Test3();

        Set<Categ> categSet = test3.getDataSet(Categ.class);
        Set<Book> bookSet = test3.getDataSet(Book.class);

        printRepo(categSet, bookSet);

        test3.commit();
    }

    @Test
    public void testRepo3_C_remove() {
        Test3 test3 = new Test3();
        Set<Categ> categSet = test3.getDataSet(Categ.class);
        Set<Book> bookSet = test3.getDataSet(Book.class);
        categSet.clear();
        bookSet.removeIf(b -> b.getTitle().startsWith("insomnia"));
        printRepo(categSet, bookSet);
        test3.commit();
    }

}