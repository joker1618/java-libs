package xxx.joker.libs.core.types;


public interface JkFormattable<T> {

    String format();

    T parse(String str);

}
