package org.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URL;
import java.util.ResourceBundle;

public class PrimarySceneController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("PrimarySceneController initialized");
        if(accountTextField != null)
            accountTextField.setText("Architect");
        if(passwordTextField != null)
            passwordTextField.setText("123456");

    }
    @FXML
    private Button ForgetButton;
    @FXML
    private Button createAccountButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField accountTextField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Label conditionLabel;
    /**
     * 登录按钮
     * */
    @FXML
    void loginButtonFunc(ActionEvent event) {
        String acStr = accountTextField.getCharacters().toString();
        String psStr = passwordTextField.getCharacters().toString();
        if(acStr.equals(mainFrame.avilableAccount) && psStr.equals(mainFrame.password)){
            System.out.println("Login successfully");
            pageSwitch();
        }
        else{
            conditionLabel.setText("用户名或密码错误，请重试");
        }
    }
    /**
     * 页面转换
     * */
    public void pageSwitch(){
        mainFrame.mainStage.setX(200);
        mainFrame.mainStage.setY(100);
        mainFrame.mainStage.setScene(mainFrame.markScene);
        mainFrame.mainStage.setMaximized(false);
        mainFrame.mainStage.setResizable(true);
        mainFrame.mainStage.setMinWidth(1500);
        mainFrame.mainStage.setMinHeight(800);
    }

}
