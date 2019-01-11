package xxx.joker.libs.repository.design;

public interface JkEntityFieldCustom extends Comparable<JkEntityFieldCustom> {

    String formatEntity();

    void setFromString(String str);

}
