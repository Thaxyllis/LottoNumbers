/**
 * Controller Class for Lotto assistant
 */
package Lotto;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.util.*;

public class Controller implements Initializable {
    /**
     * textArea1 - first square box for status and result messages
     * textArea2 - vertical box for data and statistic
     * textArea3 - square box for number generation results
     * getQuant - quantity of sets to generate unique numbers
     * areaNum1-6 - fields for entering user numbers to check occurance
     * btnDown - build database and download button
     * btnGenerate - generate sets of unique numbers button
     * btnStats - button for switch between historic data and statistics in textArea2
     * btnSData - button for switch between historic data and statistics in textArea2
     */
    @FXML public TextArea textArea1, textArea2, textArea3;
    @FXML public TextField getQuant, areaNum1, areaNum2, areaNum3, areaNum4, areaNum5, areaNum6;
    @FXML public Button btnDown, btnGenerate, btnChk, btnStats, btnData;
    @FXML public ChoiceBox<String> choiceBox ;

    /**
     * Initiates downloading and building database in separate thread
     * @param event
     * @throws InterruptedException
     */
    public void downloadButton(ActionEvent event) throws InterruptedException {

        textArea2.appendText("\n" + "Pobieranie danych..." + "\n");
        btnDown.setDisable(true);
        Task<Void> task = new getData();
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
        btnDown.setText("Odśwież");
    }

    /**
     * Checking of user entered numbers if they occured in history
     */
    public void checkUserNumber() {

        if((Lotto.isNumericMulti(areaNum1.getText(), areaNum2.getText(), areaNum3.getText(), areaNum4.getText(), areaNum5.getText(), areaNum6.getText()) && Main.mode != 2) || (Lotto.isNumericMulti(areaNum1.getText(), areaNum2.getText(), areaNum3.getText(), areaNum4.getText(), areaNum5.getText(), areaNum6.getText()) && Main.mode == 2)) {
            int n1 = Integer.parseInt(areaNum1.getText());
            int n2 = Integer.parseInt(areaNum2.getText());
            int n3 = Integer.parseInt(areaNum3.getText());
            int n4 = Integer.parseInt(areaNum4.getText());
            int n5 = Integer.parseInt(areaNum5.getText());

            if(Main.mode != 2) {
                int n6 = Integer.parseInt(areaNum6.getText());
                if ((n1 > 0 && n1 < 50) && (n2 > 0 && n2 < 50) && (n3 > 0 && n3 < 50) && (n4 > 0 && n4 < 50) && (n5 > 0 && n5 < 50) && (n6 > 0 && n6 < 50)) {
                    int[] iCheck = {n1, n2, n3, n4, n5, n6};
                    Arrays.sort(iCheck);
                    if (Lotto.checkDuplication(iCheck) == false) {
                        textArea3.setText("Liczby: " + Arrays.toString(iCheck) + " *nie wystąpiły* w żadnym losowaniu!");
                    } else {
                        textArea3.setText("Liczby: " + Arrays.toString(iCheck) + " *wystąpiły* w jednym z poprzednich losowań" + "\n");
                        textArea3.appendText("Data wystąpienia: " + Lotto.checkDuplicationDate(iCheck));
                    }
                }
            } else {
                if ((n1 > 0 && n1 < 43) && (n2 > 0 && n2 < 43) && (n3 > 0 && n3 < 43) && (n4 > 0 && n4 < 43) && (n5 > 0 && n5 < 43)) {
                    int[] iCheck = {n1, n2, n3, n4, n5};
                    Arrays.sort(iCheck);
                    if (Lotto.checkDuplication(iCheck) == false) {
                        textArea3.setText("Liczby: " + Arrays.toString(iCheck) + " *nie wystąpiły* w żadnym losowaniu!");
                    } else {
                        textArea3.setText("Liczby: " + Arrays.toString(iCheck) + " *wystąpiły* w jednym z poprzednich losowań" + "\n");
                        textArea3.appendText("Data wystąpienia: " + Lotto.checkDuplicationDate(iCheck));
                    }
                }
            }
        } else {
            textArea3.setText("Podaj wszystkie liczby z zakresu 1 - 49");
        }
    }

    /**
     * Switch for data presentation - numbers statistics
     */
    public void numStats() {
        textArea1.setText(Lotto.printCountedNumbers());
        btnStats.setDisable(true);
        btnData.setDisable(false);
    }

    /**
     * Switch for data presentation - sets with dates
     */
    public void rollStats() {
        textArea1.setText(Main.sPrint);
        btnStats.setDisable(false);
        btnData.setDisable(true);
    }

    /**
     * Call to generate unique sets specified times in field
     */
    public void getUserNumbers() {
        int[] nums;
        int setCnt = Integer.parseInt(getQuant.getText());
        int count;
        if(Main.mode != 2) count = 6; else count = 5;

        textArea3.setText("");
        for(int i=1; i<setCnt+1; i++) {
            nums = Lotto.genUnique(count);
            Arrays.sort(nums);
            textArea3.appendText(Arrays.toString(nums) + "\n");
        }
    }

    /**
     * Initialization for main scene, adding choiceBox values and listener for different data types.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        choiceBox.setValue("Lotto");
        choiceBox.getItems().addAll("Lotto", "Plus", "Mini Lotto");

        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value) {
                try {
                    setMode(new_value.intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            });
    }

    /**
     * Calls to change application mode to other data types and resets GUI to starting state.
     * @param mode 0 - Lotto, 1 - Plus, 2 - Mini Lotto
     * @throws Exception
     */
    private void setMode(int mode) throws Exception {
        Main.setMode(mode);
        btnStats.setDisable(true);
        btnData.setDisable(true);
        btnChk.setDisable(true);
        btnChk.setDisable(true);
        btnGenerate.setDisable(true);
        btnDown.setText("Pobierz");
        textArea1.setText("");
        textArea2.setText("Pobierz dane z serwera");
        textArea3.setText("Do wygenerowania unikalnych liczb, należy najpierw pobrać dane.");
        areaNum1.setDisable(true);
        areaNum2.setDisable(true);
        areaNum3.setDisable(true);
        areaNum4.setDisable(true);
        areaNum5.setDisable(true);
        areaNum6.setDisable(true);
        areaNum1.setText("");
        areaNum2.setText("");
        areaNum3.setText("");
        areaNum4.setText("");
        areaNum5.setText("");
        areaNum6.setText("");
    }

    /**
     * Download data button. Clears the Main storage object, creates database and sets propper state of GUI objects.
     */
    private class getData extends Task<Void> {
        @Override
        protected Void call() throws Exception {
            Main.listNumbers.removeAll(Main.listNumbers);
            Main.buildDatabase();

            String type = "";
            if(Main.mode == 0) type = "Lotto";
            else if(Main.mode == 1) type = "Lotto Plus";
            else if(Main.mode == 2) type = "Mini Lotto";

            int cnt = 0;
            for (Numbers n : Main.listNumbers) {
                Main.sPrint += ++cnt + ". | " + n.getDate() + " | " + Arrays.toString(n.getNumbers()) + " " + "\n";
            }

            textArea1.appendText(Main.sPrint);
            textArea2.appendText("Pobieranie zakończone." + "\n" + "Otrzymano " + cnt + " wyników losowań." + "\n");
            textArea2.appendText("Najnowsze liczby z dnia " + Main.listNumbers.get(0).getDate() + "\n");
            textArea2.appendText(type + " " + Arrays.toString(Main.listNumbers.get(0).getNumbers()) + "\n");

            btnDown.setDisable(false);
            btnGenerate.setDisable(false);
            getQuant.setDisable(false);
            btnChk.setDisable(false);

            btnStats.setDisable(false);
            areaNum1.setDisable(false);
            areaNum2.setDisable(false);
            areaNum3.setDisable(false);
            areaNum4.setDisable(false);
            areaNum5.setDisable(false);
            areaNum6.setDisable(false);
            if(Main.mode == 2) {
                areaNum6.setVisible(false);
            } else {
                areaNum6.setVisible(true);
            }
            return null;
        }
    }
}

