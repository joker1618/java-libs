package xxx.joker.apps.formula1.gui;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.lang3.StringUtils;
import org.scenicview.ScenicView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xxx.joker.apps.formula1.gui.wrapper.F1EntrantWrapper;
import xxx.joker.apps.formula1.model.F1Model;
import xxx.joker.apps.formula1.model.F1ModelImpl;
import xxx.joker.apps.formula1.model.entities.F1Entrant;
import xxx.joker.apps.formula1.model.entities.F1Team;
import xxx.joker.libs.core.files.JkFiles;
import xxx.joker.libs.core.lambdas.JkStreams;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static xxx.joker.libs.core.utils.JkConsole.display;
import static xxx.joker.libs.core.utils.JkStrings.strf;

public class Formula1GUI extends Application {

    private static final Logger LOG = LoggerFactory.getLogger(Formula1GUI.class);
    private static boolean scenicView;

    private Stage primaryStage;

    private final int year = 2018;
    TableView<F1Entrant> table = new TableView<>();
//    TableView<F1EntrantWrapper> table = new TableView<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Pane pane = createRootPane();

        // Create scene
        Group root = new Group();
        Scene scene = new Scene(root, 600, 300);
        scene.setRoot(pane);

        // Show stage
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();

        if(scenicView) {
            ScenicView.show(scene);
        }
    }

    private Pane createRootPane() {
        F1Model model = F1ModelImpl.getInstance();
//        table = F1EntrantWrapper.createTableView();
//        table = new TableView<>();
//        TableColumn<F1Entrant, F1Team> colTeamName = X_FxUtil.createTableColumn("TEAM", "team", F1Team::getTeamName);
//        TableColumn<F1Entrant, F1Team> colTeamNat = X_FxUtil.createTableColumn("NATION", "team", F1Team::getNation);
//        TableColumn<F1Entrant, String> colEngine = X_FxUtil.createTableColumnString("ENGINE", "engine");
//        TableColumn<F1Entrant, Integer> colCarNo = X_FxUtil.createTableColumnInteger("CAR NUM", "carNo");
//        TableColumn<F1Entrant, F1Driver> colDriverName = X_FxUtil.createTableColumn("DRIVER", "driver", F1Driver::getCity);
//        TableColumn<F1Entrant, String> colDriverNat = X_FxUtil.createTableColumnString("NATION", "DN");
////        TableColumn<F1Entrant, F1Driver> colDriverNat = X_FxUtil.createTableColumn("NATION", "driver", F1Driver::getNation);
//        TableColumn<F1Entrant, F1Driver> colDriverCity = X_FxUtil.createTableColumn("BIRTH CITY", "driver", F1Driver::getBirthCity);
//        TableColumn<F1Entrant, F1Driver> colDriverBirthDate = X_FxUtil.createTableColumn("BIRTH DATE", "driver", d -> d.getBirthDate().toString());
////        table.getColumns().addAll(colEngine, colCarNo);
//        table.getColumns().addAll(colTeamName, colTeamNat, colEngine, colCarNo, colDriverName, colDriverNat, colDriverCity, colDriverBirthDate);
//        List<F1Entrant> entrants = model.getEntrants(year);
//        List<F1EntrantWrapper> items = JkStreams.map(entrants, F1EntrantWrapper::new);
//        table.getItems().setAll(items);

        Label labelTitle = new Label(strf("ENTRANTS {}", year));

        createTableEntrants();
        table.getItems().setAll(model.getEntrants(year));

        BorderPane bpane = new BorderPane();
        bpane.setTop(labelTitle);
        bpane.setCenter(table);
        return bpane;
    }

    @Override
    public void stop() throws Exception {
//        F1Model.getInstance().commit();
        LOG.debug("STOP APP");
        table.getItems().forEach(e -> display(e.toString()));
    }

    public static void main(String[] args) {
        scenicView = args.length > 0 && args[0].equals("-scenicView");
//		scenicView = true;
        launch(args);
    }

    private void createTableEntrants() {
        table = new TableView<>();
        table.getColumns().add(createColumn("TEAM NAME", e -> e.getTeam().getTeamName()));
//        table.getColumns().add(createColumn("TEAM NATION", e -> e.getTeam().getNation()));
        TableColumn<F1Entrant, String> colTeamNation = createColumn("TEAM NATION", e -> e.getTeam().getNation());
        colTeamNation.setCellFactory(p -> new ImageCell<>());
        table.getColumns().add(colTeamNation);
        table.getColumns().add(createColumn("engine"));
        table.getColumns().add(createColumn("carNo"));
        table.getColumns().add(createColumn("DRIVER NAME", e -> e.getDriver().getFullName()));
        table.getColumns().add(createColumn("DRIVER INFO", e -> strf("{}, {}, {}", e.getDriver().getBirthDate(), e.getDriver().getBirthCity(), e.getDriver().getNation())));

    }

//    public static <T> TableColumn<T, String> createStringColumn(String varName) {
//        return createColumn(varName);
//    }
//    public static <T> TableColumn<T, String> createStringColumn(String header, String varName) {
//        return createColumn(header, varName);
//    }
//    public static <T> TableColumn<T, String> createStringColumn(String header, Function<T, String> extractor) {
//        return createColumn(header, extractor);
//    }

    public static <T, V> TableColumn<T, V> createColumn(String varName) {
        return createColumn(getColumnHeader(varName), varName);
    }
    public static <T, V> TableColumn<T, V> createColumn(String header, String varName) {
        TableColumn<T, V> colTeamName = new TableColumn<>();
        colTeamName.setText(header);
        colTeamName.setCellValueFactory(new PropertyValueFactory<>(varName));
//        colTeamName.setCellFactory(getSafeCellFactory());
        return colTeamName;
    }
    public static <T,V> TableColumn<T, V> createColumn(String header, Function<T, V> extractor) {
        TableColumn<T, V> colTeamName = new TableColumn<>();
        colTeamName.setText(header);
        colTeamName.setCellValueFactory(param -> new SimpleObjectProperty<>(extractor.apply(param.getValue())));
//        colTeamName.setCellFactory(getSafeCellFactory());
        return colTeamName;
    }

    public static String getColumnHeader(String varName) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < varName.length(); i++) {
            char c = varName.charAt(i);
            if(c >= 'A' && c <= 'Z') {
                sb.append(" ");
            }
            sb.append(c);
        }
        String res = sb.toString().replace("_", " ").replaceAll(" +", " ").trim();
        return res.toUpperCase();
    }


    public static <T, V> Callback<TableColumn<T, V>, TableCell<T, V>> getCellFactory(Function<V, String> toStringFunc) {
        return column -> new TableCell<T, V> () {
            @Override
            protected void updateItem (V item, boolean empty) {
                super.updateItem (item, empty);
                if (item == null || empty) {
                    setText("---");
                } else {
                    setText(toStringFunc.apply(item));
                }
            }
        };
    }
//    public static <T, V> Callback<TableColumn<T, V>, TableCell<T, V>> getFlagCellFactory(F1Team) {
//        return column -> new TableCell<T, V> () {
//            @Override
//            protected void updateItem (V item, boolean empty) {
//                super.updateItem (item, empty);
//                if (item == null || empty) {
//                    setText("---");
//                } else {
//                    setText(item.toString());
//                }
//            }
//        };
//    }

    private static class ImageCell<T> extends TableCell<T, String> {

        private final ImageView image;

        public ImageCell() {
            // add ImageView as graphic to display it in addition
            // to the text in the cell
            this.image = new ImageView();
            this.image.setFitHeight(30);
            this.image.setFitWidth(45);
            this.image.setPreserveRatio(true);
            setGraphic(this.image);
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                // set back to look of empty cell
                setText(null);
                image.setImage(null);
            } else {
                // set image and text for non-empty cell
                Path flagPath = Paths.get("C:\\Users\\fede\\.appsFolder\\formula1\\images\\icons\\flags\\Monaco.png");
                Image img = new Image(JkFiles.toURL(flagPath));
                image.setImage(img);
                setText(item);
            }
        }
    }

    /*
    The advantages are that:
        The column is typed with a "java8 Date" to avoid the sort problem evoqued by @Jordan
        The method "getDateCell" is generic and can be used as an util function for all Java8 Time types (Local Zoned etc.)
    --------------------------------------------------

    @FXML
    private TableColumn<MyBeanUi, ZonedDateTime> dateColumn;

    @FXML
    public void initialize () {
      // The normal binding to column
      dateColumn.setCellValueFactory(cellData -> cellData.getValue().getCreationDate());

      //.. All the table initialisation and then
      DateTimeFormatter format = DateTimeFormatter .ofLocalizedDate(FormatStyle.SHORT);
      dateColumn.setCellFactory (getDateCell(format));

    }

    public static <ROW,T extends Temporal> Callback<TableColumn<ROW, T>, TableCell<ROW, T>> getDateCell (DateTimeFormatter format) {
      return column -> {
        return new TableCell<ROW, T> () {
          @Override
          protected void updateItem (T item, boolean empty) {
            super.updateItem (item, empty);
            if (item == null || empty) {
              setText (null);
            }
            else {
              setText (format.format (item));
            }
          }
        };
      };
    }
     */


    /*
    private class ColumnFormatter<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {
        private Format format;

        public ColumnFormatter(Format format) {
            super();
            this.format = format;
        }
        @Override
        public TableCell<S, T> call(TableColumn<S, T> arg0) {
            return new TableCell<S, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(new Label(format.format(item)));
                    }
                }
            };
        }
    }
     */
}
