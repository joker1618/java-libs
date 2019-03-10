package trymodel.entities3;

import xxx.joker.libs.repository.design.JkEntityField;
import xxx.joker.libs.repository.design.JkRepoEntity;

import java.util.List;
import java.util.Set;

public class Book extends JkRepoEntity {

    @JkEntityField(idx = 0)
    private String title;
    @JkEntityField(idx = 1)
    private List<Book> others;
    @JkEntityField(idx = 2)
    private Set<Categ> categs;
    @JkEntityField(idx = 3)
    private Book book;


    @Override
    public String getPrimaryKey() {
        return title;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Book> getOthers() {
        return others;
    }

    public void setOthers(List<Book> others) {
        this.others = others;
    }

    public Set<Categ> getCategs() {
        return categs;
    }

    public void setCategs(Set<Categ> categs) {
        this.categs = categs;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
