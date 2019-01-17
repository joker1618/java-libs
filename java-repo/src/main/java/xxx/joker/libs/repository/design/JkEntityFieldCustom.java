package xxx.joker.libs.repository.design;

public interface JkEntityFieldCustom extends Comparable<JkEntityFieldCustom> {

    String formatField();

    void setFromString(String str);

}
