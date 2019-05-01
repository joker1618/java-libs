package xxx.joker.apps.formula1.fxgui.fxmodel;

import xxx.joker.apps.formula1.model.entities.F1GranPrix;

import java.util.function.Consumer;

public interface F1GuiModel {

    FxNation getNation(String nationName);

    void setSelectedYear(int year);
    void addChangeActionYear(Consumer<Integer> action);

    void setSelectedGranPrix(F1GranPrix gp);
    void addChangeActionGranPrix(Consumer<F1GranPrix> action);
}
