package xxx.joker.apps.formula1.fxgui.fxmodel;

import java.util.function.Consumer;

public interface F1GuiModel {

    FxNation getNation(String nationName);

    void setSelectedYear(int year);
    void addYearChangeAction(Consumer<Integer> listener);
}
