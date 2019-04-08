package xxx.joker.apps.formula1.gui.wrapper;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import xxx.joker.apps.formula1.fxlibs.X_FxUtil;
import xxx.joker.apps.formula1.model.entities.F1Entrant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class F1EntrantWrapper {

    private F1Entrant entrant;
    private String teamName;
    private String teamNation;
    private String engine;
    private int carNo;
    private String driverName;
    private String driverNation;
    private String driverCity;
    private String driverBirthDate;

    public F1EntrantWrapper(F1Entrant entrant) {
        this.entrant = entrant;
    }

    public static TableView<F1EntrantWrapper> createTableView() {
        TableView<F1EntrantWrapper> table = new TableView<>();
        TableColumn<F1EntrantWrapper, String> colTeamName = X_FxUtil.createTableColumnString("TEAM NAME", "teamName");
        TableColumn<F1EntrantWrapper, String> colTeamNation = X_FxUtil.createTableColumnString("TEAM NATION", "teamNation");
        TableColumn<F1EntrantWrapper, String> colEngine = X_FxUtil.createTableColumnString("ENGINE", "engine");
        TableColumn<F1EntrantWrapper, Integer> colCarNo = X_FxUtil.createTableColumnInteger("CAR NUM", "carNo");
        TableColumn<F1EntrantWrapper, String> colDriverName = X_FxUtil.createTableColumnString("DRIVER NAME", "driverName");
        TableColumn<F1EntrantWrapper, String> colDriverNation = X_FxUtil.createTableColumnString("DRIVER NATION", "driverNation");
        TableColumn<F1EntrantWrapper, String> colDriverCity = X_FxUtil.createTableColumnString("DRIVER CITY", "driverCity");
        TableColumn<F1EntrantWrapper, LocalDate> colDriverBirthDate = X_FxUtil.createTableColumnLocalDate("DRIVER BIRTHDATE", "driverBirthDate", "dd-MM-yyyy");
        table.getColumns().addAll(colTeamName, colTeamNation, colEngine, colCarNo, colDriverName, colDriverNation, colDriverCity, colDriverBirthDate);
        return table;
    }

    public String getTeamName() {
        return entrant.getTeam().getTeamName();
    }

    public String getTeamNation() {
        return entrant.getTeam().getNation();
    }

    public String getEngine() {
        return entrant.getEngine();
    }

    public int getCarNo() {
        return entrant.getCarNo();
    }

    public String getDriverName() {
        return entrant.getDriver().getFullName();
    }

    public String getDriverNation() {
        return entrant.getDriver().getNation();
    }

    public String getDriverCity() {
        return entrant.getDriver().getBirthCity();
    }

    public LocalDate getDriverBirthDate() {
        return entrant.getDriver().getBirthDate();
    }

    @Override
    public String toString() {
        return entrant.toString();
    }
}
