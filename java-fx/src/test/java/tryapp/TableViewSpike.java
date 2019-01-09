package tryapp;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import xxx.joker.libs.core.ToAnalyze;

@ToAnalyze
@Deprecated
public class TableViewSpike  extends Application {

    private final TableView<Person> table = new TableView<>();
    private final ObservableList<Person> data
            = FXCollections.observableArrayList(
            new Person("Jacob", "Smith"),
            new Person("Isabella", "Johnson"),
            new Person("Ethan", "Williams"),
            new Person("Emma", "Jones"),
            new Person("Michael", "Brown")
    );

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setWidth(450);
        stage.setHeight(500);

        TableColumn firstNameCol = new TableColumn("First Name");
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Person, String> actionCol = new TableColumn<>("Action");
//        actionCol.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));

        Callback<TableColumn<Person, String>, TableCell<Person, String>> cellFactory
                = //
                new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
                    @Override
                    public TableCell<Person, String> call(final TableColumn<Person, String> param) {
                        final TableCell<Person, String> cell = new TableCell<Person, String>() {

                            final Button btn = new Button("Just Do It");
                            final Image image = new Image(getClass().getClassLoader().getResourceAsStream("img.jpg"));

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        Person person = getTableView().getItems().get(getIndex());
                                        System.out.println(person.getFirstName()
                                                + "   " + person.getLastName());
                                    });
                                    ImageView imgView = new ImageView(image);
                                    imgView.setPreserveRatio(true);
                                    imgView.setFitWidth(20.0);
                                    setGraphic(imgView);
                                    setText("ede");
                                }
                            }
                        };

                        return cell;
                    }
                };

        actionCol.setCellFactory(cellFactory);

        table.setItems(data);
        table.getColumns().addAll(firstNameCol, lastNameCol, actionCol);

        Scene scene = new Scene(new Group());

        ((Group) scene.getRoot()).getChildren().addAll(table);

        stage.setScene(scene);
        stage.show();
    }

    public static class Person {

        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;

        private Person(String fName, String lName) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String fName) {
            firstName.set(fName);
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String fName) {
            lastName.set(fName);
        }

    }
}