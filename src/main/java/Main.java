/**
 * Created by Brian on 1/18/2018.
 */

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.*;

public class Main extends Application implements Initializable {
    Stage primaryStage;
    @FXML
    TextField txtSearch;
    @FXML
    ListView lstReceipt;
    @FXML
    Button btnNewSale;
    @FXML
    Button btnPayment;
    @FXML
    Button btnTotal;
    @FXML
    Button btnQuantity;
    @FXML
    TableView<List<String>> tblHistory;
    @FXML
    Label lblTotal;

    private Store store;
    private Register register;
    public static void main(String[] args) { launch(args); }
    private boolean inSale=false;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage=primaryStage;
        try {
            primaryStage.setMinWidth(850);
            primaryStage.setMinHeight(595);
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/register.fxml"));
            Parent root = loader.load();
            primaryStage.setTitle("POS System");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/my.css").toString());
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Start finished");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.store = new Store();
        this.register = store.getRegister();
        outOfSale();

        btnNewSale.setOnAction((event)-> {
            register.makeNewSale();
            btnTotal.setDisable(false);
            btnQuantity.setDisable(false);
            txtSearch.setDisable(false);
            lstReceipt.getItems().clear();
            updateSaleDisplay(register.getReciept());
            txtSearch.requestFocus();
            inSale=true;
        });

        btnTotal.setOnAction((event)-> {
            register.endSale();
            btnPayment.setDisable(false);
            btnQuantity.setDisable(true);
            txtSearch.setDisable(true);
            updateSaleDisplay(register.getReciept());
        });

        btnPayment.setOnAction((event)->{
            //prompt for payment
            TextInputDialog dialog = new TextInputDialog(register.getBalance().stringAbs());
            dialog.setHeaderText("Accept cash payment");
            dialog.setContentText("Please enter the payment amount");
            Optional<String> result = dialog.showAndWait();
            if (!result.isPresent())
                return;
            //Strip currency symbol if there is one
            String paymentAmount = result.get().replace(Currency.getInstance(new Locale("EN","US")).getSymbol(),"");
            //make our payment
            register.makePayment(new Money(new BigDecimal(paymentAmount)));
            if (register.getBalance().getAmount().compareTo(BigDecimal.ZERO)>=0) {
                //Sale was paid for add change, print receipt, log sale
                updateSaleDisplay(register.getReciept());
                showReceipt();
                //log our sale
                store.addSale(register.getSale());
                updateHistoryDisplay();
                outOfSale();
            } else {
                System.out.println(register.getBalance().getAmount().compareTo(BigDecimal.ZERO));
                //payment wasn't enough
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Insufficient payment");
                alert.setContentText("Payment amount was too small, please enter a larger amount");
                alert.showAndWait();
            }
        });

        btnQuantity.setOnAction((event)-> {
            TextInputDialog dialog = new TextInputDialog("2");
            dialog.setTitle("Quantity");
            dialog.setHeaderText("Quantity Needed");
            dialog.setContentText("Please enter the quantity");
            Optional<String> result = dialog.showAndWait();
            if (!result.isPresent())
                return;
            try {
                int itemId = Integer.parseInt(txtSearch.getText());
                int quantity = Integer.parseInt(result.get());
                enterItem(itemId, quantity);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid number");
                alert.setContentText("Item id and quantity must be a number");
            }
        });

        txtSearch.setOnKeyPressed((event) -> {
            if (event.getCode()== KeyCode.ENTER) {
                int itemId=Integer.parseInt(txtSearch.getText());
                enterItem(itemId,1);
            }
            if (event.getCode()==KeyCode.MULTIPLY)
                btnQuantity.fire();
            if (event.getCode()==KeyCode.DIVIDE) {
                btnTotal.fire();
                btnPayment.fire();
            }

        });

        tblHistory.setRowFactory(tv -> {
            TableRow<List<String>> row = new TableRow();
            row.setOnMouseClicked(event -> {
                if (inSale)
                    return;
                if (!row.isEmpty() && event.getButton()== MouseButton.PRIMARY) {
                    updateSaleDisplay(store.getSale(Long.parseLong(row.getItem().get(0))).toString());
                }
            });
            return row;
        });

        for (int colIndex=0;colIndex<tblHistory.getColumns().size();colIndex++) {
            TableColumn<List<String>, String> col = (TableColumn<List<String>, String>) tblHistory.getColumns().get(colIndex);
            final int finalColIndex = colIndex;
            col.setCellValueFactory(data -> {
                List<String> rowValue = data.getValue();
                String cellValue;
                if (finalColIndex < rowValue.size())
                    cellValue = rowValue.get(finalColIndex);
                else
                    cellValue = "";
                return new ReadOnlyStringWrapper(cellValue);
            });
        }

        lstReceipt.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount()==2) {
                    //if we split it with tab and it's a number, that's our item id
                    String line = lstReceipt.getSelectionModel().getSelectedItem().toString();
                    String strID = line.split("\t")[0];
                    if (isId(strID)) {
                        int id = Integer.parseInt(strID);
                        if (inSale) {
                            //Remove item from current sale
                            register.removeItem(id,1);
                            updateSaleDisplay(register.getReciept());
                        } else {
                            //Remove from an old sale
                            List<String> row = tblHistory.getSelectionModel().getSelectedItem();
                            Sale sale = store.getSale(Long.parseLong(row.get(0)));
                            ProductSpecification item = store.getProduct(id);
                            //Item not found, shouldn't be possible but better safe then sorry.
                            if (item == null)
                                return;
                            //prompt that it is ok.
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText("Return "+item.getDescription()+"?");
                            alert.setContentText("Are you sure you wish to return "+item.getDescription()+". This can not be undone.");
                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.isPresent() && result.get()==ButtonType.OK) {
                                sale.removeLineItem(store.getProduct(id), 1);
                                //update changed information.
                                store.addSale(sale);
                            }
                            updateSaleDisplay(sale.toString());
                        }
                    }
                }
            }
        });

    }

    private void enterItem(int itemId,int amount) {
        if (!store.itemExists(itemId)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Item not found");
            alert.setContentText("Item with id "+itemId+" was not found.");
            alert.showAndWait();
            return;
        }
        //Add item.
        register.enterItem(itemId,amount);
        txtSearch.setText("");
        updateSaleDisplay(register.getReciept());
    }

    private void updateSaleDisplay(String receipt) {
        List<String> items = Arrays.asList(receipt.split("\n"));
        lstReceipt.setItems(FXCollections.observableArrayList(items));
        lstReceipt.scrollTo(lstReceipt.getItems().size()-1);
        updateTotal();
    }

    private void updateTotal() {
        //Update total
        if (inSale) {
            Money balance = register.getBalance().negate();
            lblTotal.setText("Amount Due: "+balance);
        } else {
            Money balance = store.getLedger().getTotalSales();
            lblTotal.setText("Today's Sales: "+balance);
        }
    }

    private void updateHistoryDisplay() {
        List<String> sales = Arrays.asList(store.getLedger().toString().split("\n"));
        tblHistory.getItems().clear();
        for (String sale : sales) {
            tblHistory.getItems().add(Arrays.asList(sale.split("\t")));
        }
    }

    private void outOfSale() {
        btnPayment.setDisable(true);
        btnTotal.setDisable(true);
        btnQuantity.setDisable(true);
        txtSearch.setDisable(true);
        inSale=false;
        updateTotal();
    }

    private void showReceipt() {
        Alert alert =  new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Receipt");
        alert.setHeaderText(null);
        alert.setContentText("Printing...");

        TextArea textArea = new TextArea(register.getReciept());
        textArea.setEditable(false);
        textArea.setWrapText(false);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(new Label("Receipt to print"), 0, 0);
        expContent.add(textArea, 0, 1);
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();

        lstReceipt.getItems().clear();
    }

    public static boolean isId(String str) {
        return str.matches("\\d+");
    }
}
