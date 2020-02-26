package xxx.joker.libs.core.object;

public class TitledObj<T> {

    private final String title;
    private final T obj;

    private TitledObj(String title, T obj) {
        this.title = title;
        this.obj = obj;
    }

    public static <T> TitledObj<T> of(T obj) {
        return new TitledObj<>(null, obj);
    }
    public static <T> TitledObj<T> of(String title, T obj) {
        return new TitledObj<>(title, obj);
    }

    public String getTitle() {
        return title;
    }

    public T getObj() {
        return obj;
    }
}
