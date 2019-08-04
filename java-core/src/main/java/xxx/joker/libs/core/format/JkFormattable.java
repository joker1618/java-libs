package xxx.joker.libs.core.format;


import xxx.joker.libs.core.runtimes.JkReflection;

public interface JkFormattable<T> {

    String format();

    T parse(String str);

    static <T extends JkFormattable> T parse(Class<T> clazz, String str) {
        T instance = JkReflection.createInstance(clazz);
        return (T) instance.parse(str);
    }

}
