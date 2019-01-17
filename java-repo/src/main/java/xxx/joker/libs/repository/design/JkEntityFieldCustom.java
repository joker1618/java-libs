package xxx.joker.libs.repository.design;

/**
 * The implementation class cannot contains JkEntity fields
 */
public interface JkEntityFieldCustom<T extends JkEntityFieldCustom> extends Comparable<T> {

    String formatField();

    void setFromString(String str);

}
