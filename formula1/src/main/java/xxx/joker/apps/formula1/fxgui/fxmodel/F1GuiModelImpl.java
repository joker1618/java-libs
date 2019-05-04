package xxx.joker.apps.formula1.fxgui.fxmodel;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1GranPrix;
import xxx.joker.libs.core.cache.JkCache;
import xxx.joker.service.sharedRepo.JkSharedRepo;
import xxx.joker.service.sharedRepo.JkSharedRepoImpl;

import java.util.function.Consumer;

public class F1GuiModelImpl implements F1GuiModel {

    private static final F1GuiModel instance = new F1GuiModelImpl();

    private F1Model model = F1ModelImpl.getInstance();
    private JkSharedRepo sharedRepo = JkSharedRepoImpl.getInstance();

    private JkCache<String, FxNation> cacheNation = new JkCache<>();
    private JkCache<Integer, SeasonView> cacheYears = new JkCache<>();

    private SimpleIntegerProperty selectedYear = new SimpleIntegerProperty();
    private SimpleObjectProperty<F1GranPrix> selectedGranPrix = new SimpleObjectProperty<>();


    private F1GuiModelImpl() {

    }

    public static F1GuiModel getInstance() {
        return instance;
    }

    @Override
    public FxNation getNation(String nationName) {
        return cacheNation.get(nationName, () -> new FxNation(sharedRepo.getNation(nationName)));
    }

    @Override
    public void setSelectedYear(int year) {
        selectedYear.set(year);
    }

    @Override
    public void addChangeActionYear(Consumer<Integer> action) {
        selectedYear.addListener((obs,o,n) -> { if(n != null && n != o) action.accept(n.intValue()); });
    }

    @Override
    public void setSelectedGranPrix(F1GranPrix gp) {
        selectedGranPrix.set(gp);
    }

    @Override
    public void addChangeActionGranPrix(Consumer<F1GranPrix> action) {
        selectedGranPrix.addListener((obs,o,n) -> {
            if(n != null && n != o) {
                action.accept(n);
            }
        });
    }
}
