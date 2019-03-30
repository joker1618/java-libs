package xxx.joker.libs.repository.design;

/**
 * The implementation class cannot contains JkEntity fields
 */
public interface JkRepoFieldCustom<T extends JkRepoFieldCustom> extends Comparable<T> {

    String formatField();

    void parseString(String str);

}
