package org.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.DirectoryChooser;
import org.XmlReader.XmlAccessor;
import org.structures.FilePair;
import org.structures.RectArc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;

import static org.XmlReader.XmlAccessor.RectArcList2Xml;

public class MarkSceneController implements Initializable {

    //定义文件存储的哈希表，用于在选择文件后反向检索
    public static HashMap<String, FilePair> fileList;
    //定义三个文件名清单，用于左侧列表选择文件
    public static ArrayList<String> allPicNameList, donePicNameList, undonePicNameList;
    //定义矩形清单，用于绘制当前所有矩形
    public ArrayList<RectArc> rectArcList = null;
    public Image markImage = null;
    public FilePair currentFilePair = null;

    private double startX, startY, endX, endY;
    private double actualX1 = 0;
    private double actualY1 = 0;
    private double actualX2 = 0;
    private double actualY2 = 0;
    private Line lu, ld, lr, ll;
    private Line focusLine1, focusLine2;
    private boolean isDragging;
    private boolean hasInitialized = false;
    private boolean isEditing = false;
    private int rectIndex = 1;
    private int senseRate = 4;
    private int rectIndexCurrent = -1;
    private int flag = 0;
    private boolean isRebooting = false;
    public String  describe1="";
    public String  describe2="";
    public double usedWidth = 0;
    public double usedHeight = 0;
    public double offsetX = 0;
    public double offsetY = 0;
    public double zoomRate = 5;
    public String mode = "r";
    public String direction = "up";

    public double markAbsoluteX;
    public double markAbsoluteY;

    public Color defaultColor = Color.BLACK;

    @FXML
    private HBox aboveHBox;
    @FXML
    private ListView allPicListView;
    @FXML
    private ListView donePicListView;
    @FXML
    private ListView undonePicListView;
    @FXML
    private ListView wordListView;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private AnchorPane markAnchorPane;
    @FXML
    private AnchorPane zoomerAnchorPane;
    @FXML
    private ImageView markImageView;
    @FXML
    private ImageView zoomerImageView;
    @FXML
    private TextArea wordTextArea;
    @FXML
    private Label conditionLabel;
    @FXML
    private Slider zoomerSlider;
    @FXML
    private Button editButton;
    @FXML
    private Slider senseSlider;
    @FXML
    private ToggleGroup TG;
    @FXML
    private RadioButton editModeRadio;
    @FXML
    private RadioButton rectModeRadio;
    @FXML
    private RadioButton selectModeRadio;
    @FXML
    private RadioButton viewModeRadio;
    @FXML
    private ToggleGroup DTG;
    @FXML
    private RadioButton upModeRadio;
    @FXML
    private RadioButton downModeRadio;
    @FXML
    private RadioButton leftModeRadio;
    @FXML
    private RadioButton rightModeRadio;

    /**
     * 页面初始化函数
     * */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /**
         * 变量初始化，防空值
         * */
        {
            isDragging = false;
            startX = 0;
            startY = 0;
            endX = 0;
            endY = 0;
        }
        /**
         * 线条初始化
         * */
        {
            lu = new Line(0, 0, 0, 0);
            ld = new Line(0, 0, 0, 0);
            lr = new Line(0, 0, 0, 0);
            ll = new Line(0, 0, 0, 0);
            markAnchorPane.getChildren().add(lu);
            markAnchorPane.getChildren().add(ld);
            markAnchorPane.getChildren().add(lr);
            markAnchorPane.getChildren().add(ll);

            focusLine1 = new Line(175, 90, 175, 110);
            focusLine2 = new Line(165, 100, 185, 100);
            zoomerAnchorPane.getChildren().add(focusLine1);
            zoomerAnchorPane.getChildren().add(focusLine2);
        }
        /**
         * 状态词条初始化
         * */
        {
            conditionLabel.setText("就绪");
        }
        /**
         * 列表选择器初始化
         * 包含监听初始化
         * 选择模式初始化
         * */
        {
            imageListItemChangeListener ilicl =  new imageListItemChangeListener();
            allPicListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            allPicListView.getSelectionModel().selectedItemProperty().addListener(ilicl);
            donePicListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            donePicListView.getSelectionModel().selectedItemProperty().addListener(ilicl);
            undonePicListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            undonePicListView.getSelectionModel().selectedItemProperty().addListener(ilicl);
            wordListView.getSelectionModel().selectedItemProperty().addListener(new wordListItemChangeListener());
        }
        /**
         * 画板尺寸监听器初始化
         * */
        {
            resizeChangeListener rcl = new resizeChangeListener();
            markAnchorPane.widthProperty().addListener(rcl);
            markAnchorPane.widthProperty().addListener(rcl);
        }
        /**
         * 矩形列表初始化
         * */
        {
            rectArcList = new ArrayList<>();
        }
        /**
         * 拖动条监听器初始化
         * */
        {
            zoomerSlider.setValue(zoomRate);
            zoomerSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if(newValue.intValue() != zoomRate) {
                        zoomRate = newValue.intValue();

                    }
                }
            });
        }
        /**
         * 拖动条监听器初始化
         * */
        {
            senseSlider.setValue(senseRate);
            senseSlider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                    if(newValue.intValue() != senseRate) {
                        senseRate = newValue.intValue();
                        System.out.println(senseRate);

                    }
                }
            });
        }
        /**
         * 模式选择器初始化
         * */
        {
            selectModeRadio.setUserData("s");
            editModeRadio.setUserData("e");
            viewModeRadio.setUserData("v");
            rectModeRadio.setUserData("r");

            TG.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                public void changed(ObservableValue<? extends Toggle> ov,
                                    Toggle old_toggle, Toggle new_toggle) {
                    if (TG.getSelectedToggle() != null) {
                        mode = TG.getSelectedToggle().getUserData().toString();
                        System.out.println("Current Mode : " + mode);
                    }
                    endEdit();
                    resetZoomer();
                }
            });
        }
        /**
         * 方向选择器初始化
         * */
        {
            editButton.setVisible(false);
            upModeRadio.setUserData("up");
            downModeRadio.setUserData("down");
            leftModeRadio.setUserData("left");
            rightModeRadio.setUserData("right");

            DTG.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                public void changed(ObservableValue<? extends Toggle> ov,
                                    Toggle old_toggle, Toggle new_toggle) {
                    if (DTG.getSelectedToggle() != null) {
                        direction = DTG.getSelectedToggle().getUserData().toString();
                        System.out.println("Current Direction : " + direction);
                    }
                }
            });
        }
        /**
         * 键盘监听器和初始化
         * */
        {
            aboveHBox.setOnKeyTyped(new EventHandler<KeyEvent>() {

                @Override
                public void handle(KeyEvent event) {

                    if (event.isControlDown()) {
                        if (string2Ascii(event.getCharacter()).equals("23")) {
                            upModeRadio.setSelected(true);
                            upModeRadio.requestFocus();
                        } else if (string2Ascii(event.getCharacter()).equals("19")) {
                            downModeRadio.setSelected(true);
                            downModeRadio.requestFocus();
                        } else if (string2Ascii(event.getCharacter()).equals("1")) {
                            leftModeRadio.setSelected(true);
                            leftModeRadio.requestFocus();
                        } else if (string2Ascii(event.getCharacter()).equals("4")) {
                            rightModeRadio.setSelected(true);
                            rightModeRadio.requestFocus();
                        }
                    } else if (mode.equals("e") && rectIndexCurrent > 0) {
                        if (event.getCharacter().equalsIgnoreCase("w")) {
                            if (direction.equals("up")) {     //UU
                                int a = rectIndexCurrent;
                                rectArcList.get(rectIndexCurrent - 1).Y1 -= senseRate;
                                rectArcList.get(rectIndexCurrent - 1).resetLines();
                                save();
                                rectIndexCurrent = a;
                                startEdit();
                            } else if (direction.equals("down")) {  //DU
                                int a = rectIndexCurrent;
                                rectArcList.get(rectIndexCurrent - 1).Y2 -= senseRate;
                                rectArcList.get(rectIndexCurrent - 1).resetLines();
                                save();
                                rectIndexCurrent = a;
                                startEdit();
                            }
                        } else if (event.getCharacter().equalsIgnoreCase("s")) {
                            if (direction.equals("up")) {     //UD
                                int a = rectIndexCurrent;
                                rectArcList.get(rectIndexCurrent - 1).Y1 += senseRate;
                                rectArcList.get(rectIndexCurrent - 1).resetLines();
                                save();
                                rectIndexCurrent = a;
                                startEdit();
                            } else if (direction.equals("down")) {  //DD
                                int a = rectIndexCurrent;
                                rectArcList.get(rectIndexCurrent - 1).Y2 += senseRate;
                                rectArcList.get(rectIndexCurrent - 1).resetLines();
                                save();
                                rectIndexCurrent = a;
                                startEdit();
                            }
                        } else if (event.getCharacter().equalsIgnoreCase("a")) {
                            if (direction.equals("left")) {     //LL
                                int a = rectIndexCurrent;
                                rectArcList.get(rectIndexCurrent - 1).X1 -= senseRate;
                                rectArcList.get(rectIndexCurrent - 1).resetLines();
                                save();
                                rectIndexCurrent = a;
                                startEdit();
                            } else if (direction.equals("right")) {  //RL
                                int a = rectIndexCurrent;
                                rectArcList.get(rectIndexCurrent - 1).X2 -= senseRate;
                                rectArcList.get(rectIndexCurrent - 1).resetLines();
                                save();
                                rectIndexCurrent = a;
                                startEdit();
                            }
                        } else if (event.getCharacter().equalsIgnoreCase("d")) {
                            if (direction.equals("left")) {     //LR
                                int a = rectIndexCurrent;
                                rectArcList.get(rectIndexCurrent - 1).X1 += senseRate;
                                rectArcList.get(rectIndexCurrent - 1).resetLines();
                                save();
                                rectIndexCurrent = a;
                                startEdit();
                            } else if (direction.equals("right")) {  //RR
                                int a = rectIndexCurrent;
                                rectArcList.get(rectIndexCurrent - 1).X2 += senseRate;
                                rectArcList.get(rectIndexCurrent - 1).resetLines();
                                save();
                                rectIndexCurrent = a;
                                startEdit();
                            }
                        }
                    }

                }
            });
        }
    }

    /**
     * 鼠标按压检测
     * */
    @FXML
    void mPressed(MouseEvent event) {
        if(isEditing)
            endEdit();
        if(mode.equals("r")) {
            if (event.getSceneX() - markAbsoluteX >= actualX1 &&
                    event.getSceneX() - markAbsoluteX <= actualX2 &&
                    event.getSceneY() - markAbsoluteY >= actualY1 &&
                    event.getSceneY() - markAbsoluteY <= actualY2)

            {//这里防止画笔起始越界
                System.out.println("Mouse Pressed : (" + event.getSceneX() + ", " + event.getSceneY() + ")");
                startX = event.getSceneX();
                startY = event.getSceneY();
                isDragging = true;
                locationUpdate();
                setZoomer(event.getSceneX(), event.getSceneY());
            }
        }
        else if(mode.equals("e")){
            if(rectIndexCurrent > 0) {
                startX = event.getSceneX();
                startY = event.getSceneY();
            }
        }

    }
    /**
     * 鼠标拖动检测
     * */
    @FXML
    void mDragged(MouseEvent event) {
        if(mode.equals("r")) {
            if (isDragging) {

                if (event.getSceneX() < actualX1 + markAbsoluteX) {
                    endX = markAbsoluteX + actualX1;
                } else if (event.getSceneX() > actualX2 + markAbsoluteX) {
                    endX = markAbsoluteX + actualX2;
                } else {
                    endX = event.getSceneX();
                }

                if (event.getSceneY() < actualY1 + markAbsoluteY) {
                    endY = markAbsoluteY + actualY1;
                } else if (event.getSceneY() > actualY2 + markAbsoluteY) {
                    endY = markAbsoluteY + actualY2;
                } else {
                    endY = event.getSceneY();
                }

                paintRect();
                setZoomer(endX, endY);
            }
        }
        else if(mode.equals("e")){
            if(rectIndexCurrent > 0) {
                double moveX = event.getSceneX() - startX;
                double moveY = event.getSceneY() - startY;
                startX = event.getSceneX();
                startY = event.getSceneY();
                rectArcList.get(rectIndexCurrent - 1).X1 += moveX;
                rectArcList.get(rectIndexCurrent - 1).X2 += moveX;
                rectArcList.get(rectIndexCurrent - 1).Y1 += moveY;
                rectArcList.get(rectIndexCurrent - 1).Y2 += moveY;
                if(currentFilePair != null)//手动保存
                    RectArcList2Xml(actualX2 - actualX1, actualY2 - actualY1, actualX1, actualY1, currentFilePair, rectArcList);
                fitImage();

            }
        }
    }
    /**
     * 鼠标释放检测
     * */
    @FXML
    void mRelease(MouseEvent event) {
        if(isDragging) {
            System.out.println("Mouse Released : (" + event.getSceneX() + ", " + event.getSceneY() + ")");

            if (!((event.getSceneX() - startX < 0.1 && event.getSceneX() - startX > -0.1) ||
                    (event.getSceneY() - startY < 0.1 && event.getSceneY() - startY > -0.1))) {
                //此处if防止细微误触的错误
                rectArcList.add(new RectArc(startX - markAbsoluteX,
                        startY - markAbsoluteY,
                        endX - markAbsoluteX,
                        endY - markAbsoluteY,
                        defaultColor));
            }

            if (currentFilePair != null)//自动保存
                RectArcList2Xml(actualX2 - actualX1, actualY2 - actualY1, actualX1, actualY1, currentFilePair, rectArcList);
            //消除上一次加载的影响
            resetRectArc();
            resetWord();
            //从xml装载rectArcList
            XmlAccessor.Xml2RectArcList(actualX2 - actualX1, actualY2 - actualY1, actualX1, actualY1, this.currentFilePair, this.rectArcList);
            //从rectArcList中载入图像和文字
            loadRectArc();
            loadWord();
            setPaintRectDefault();
            isDragging = false;
            resetZoomer();

            Iterator it = rectArcList.iterator();
            int num = -1;
            while(it.hasNext()){
                num++;
                it.next();
            }
            if(num >= 0){
                System.out.println("num = " + num);
                wordListView.getSelectionModel().select(num);
                wordListView.getFocusModel().focus(num);
            }

        }
    }
    /**
     * 鼠标点击检测
     * */
    @FXML
    void mClicked(MouseEvent event) {
        if(mode.equals("s")) {
            int i = getRectIndex(event.getSceneX() - markAbsoluteX, event.getSceneY() - markAbsoluteY);
            System.out.println("Rect Selected : " + i);
            if(i >= 0) {
                wordListView.getSelectionModel().select(i);
                wordListView.getFocusModel().focus(i);
                //rectIndexCurrent = i;
            }
        }

    }
    /**
     * 鼠标移动检测
     * */
    @FXML
    void mMoved(MouseEvent event) {
        if(mode.equals("v")) {

            if (event.getSceneX() < actualX1 + markAbsoluteX) {
                endX = markAbsoluteX + actualX1;
            } else if (event.getSceneX() > actualX2 + markAbsoluteX) {
                endX = markAbsoluteX + actualX2;
            } else {
                endX = event.getSceneX();
            }

            if (event.getSceneY() < actualY1 + markAbsoluteY) {
                endY = markAbsoluteY + actualY1;
            } else if (event.getSceneY() > actualY2 + markAbsoluteY) {
                endY = markAbsoluteY + actualY2;
            } else {
                endY = event.getSceneY();
            }

            setZoomer(endX, endY);

        }

    }
    /**
     * 清除按钮
     * */
    @FXML
    void ClearFunc(ActionEvent event){
        if(isEditing)
            endEdit();
        if(rectIndexCurrent > 0){
            isRebooting = true;

            rectArcList.get(rectIndexCurrent - 1).setWord("");
            if(currentFilePair != null)//在清空时自动保存
                RectArcList2Xml(actualX2 - actualX1, actualY2 - actualY1, actualX1, actualY1, currentFilePair, rectArcList);
            wordTextArea.setText("");
            fitImage();
            rectIndexCurrent = -1;

            isRebooting = false;

        }
    }
    /**
     * 删除按钮
     * */
    @FXML
    void DeleteFunc(ActionEvent event){
        if(isEditing)
            endEdit();
        if(rectIndexCurrent > 0){
            isRebooting = true;

            rectArcList.get(rectIndexCurrent - 1).remove();
            if(currentFilePair != null)//在清空时自动保存
                RectArcList2Xml(actualX2 - actualX1, actualY2 - actualY1, actualX1, actualY1, currentFilePair, rectArcList);
            wordTextArea.setText("");
            fitImage();
            rectIndexCurrent = -1;

            isRebooting = false;
        }
    }
    /**
     * 打开文件夹按钮
     * */
    @FXML
    void OpenDirFunc(ActionEvent event) {
        if(isEditing)
            endEdit();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择目标文件夹");
        File dirFile = directoryChooser.showDialog(mainFrame.mainStage);

        if(dirFile.exists()){
            fileDeploy(dirFile);//文件列表装载
            picListDeploy();//显示列表装载
            conditionLabel.setText("文件装载就绪");
        }
        else{
            conditionLabel.setText("请选择正确的文件夹");
        }


    }
    /**
     * 保存按钮
     * */
    @FXML
    void SaveFunc(ActionEvent event) {
        if(currentFilePair != null)//手动保存
            RectArcList2Xml(actualX2 - actualX1, actualY2 - actualY1, actualX1, actualY1, currentFilePair, rectArcList);
    }
    /**
     * 编辑按钮
     * */
    @FXML
    void EditFunc(ActionEvent event) {
        if(isEditing){
            endEdit();
        }
        else
            if(rectIndexCurrent > 0){
                startEdit();
            }
    }
    /**
     * 帮助按钮
     * */
    @FXML
    void HelpFunc(ActionEvent event) throws IOException {
        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler https://blog.csdn.net/qq_46391766/article/details/123209968");//使用默认浏览器打开url
    }

    /**
     * 完成按钮
     * */
    @FXML
    void FinishFunc(ActionEvent event) {
        if(isEditing)
            endEdit();
        if(describe1 != null) {
            //除旧
            isRebooting = true;
                    System.out.println("Word List Item Changed");
            System.out.println(describe2);
            if (rectIndexCurrent > 0) {
                rectArcList.get(rectIndexCurrent - 1).setWord(wordTextArea.getText());
                if(currentFilePair != null)//在换页时自动保存
                    RectArcList2Xml(actualX2 - actualX1, actualY2 - actualY1, actualX1, actualY1, currentFilePair, rectArcList);
                //wordListView.getItems().set(rectIndexCurrent - 1, rectIndexCurrent + "." + wordTextArea.getText());
                fitImage();
            }
            isRebooting = false;
        }
    }
    /**
     * 颜色更改按钮
     * */
    @FXML
    void ColorSetFunc(ActionEvent event) {
        if(isEditing){
            rectArcList.get(rectIndexCurrent - 1).color = colorPicker.getValue();
            save();
        }
        else
            defaultColor = colorPicker.getValue();
    }
    /**
     * 移动动作按钮组
     * */
    @FXML
    void DDFunc(ActionEvent event) {
        if(mode.equals("e") && rectIndexCurrent > 0){
            int a = rectIndexCurrent;
            rectArcList.get(rectIndexCurrent - 1).Y2 += senseRate;
            rectArcList.get(rectIndexCurrent - 1).resetLines();
            save();
            rectIndexCurrent = a;
            startEdit();
        }
    }
    @FXML
    void DUFunc(ActionEvent event) {
        if(mode.equals("e") && rectIndexCurrent > 0){
            int a = rectIndexCurrent;
            rectArcList.get(rectIndexCurrent - 1).Y2 -= senseRate;
            rectArcList.get(rectIndexCurrent - 1).resetLines();
            save();
            rectIndexCurrent = a;
            startEdit();
        }
    }
    @FXML
    void LLFunc(ActionEvent event) {
        if(mode.equals("e") && rectIndexCurrent > 0){
            int a = rectIndexCurrent;
            rectArcList.get(rectIndexCurrent - 1).X1 -= senseRate;
            rectArcList.get(rectIndexCurrent - 1).resetLines();
            save();
            rectIndexCurrent = a;
            startEdit();
        }
    }
    @FXML
    void LRFunc(ActionEvent event) {
        if(mode.equals("e") && rectIndexCurrent > 0){
            int a = rectIndexCurrent;
            rectArcList.get(rectIndexCurrent - 1).X1 += senseRate;
            rectArcList.get(rectIndexCurrent - 1).resetLines();
            save();
            rectIndexCurrent = a;
            startEdit();
        }
    }
    @FXML
    void RLFunc(ActionEvent event) {
        if(mode.equals("e") && rectIndexCurrent > 0){
            int a = rectIndexCurrent;
            rectArcList.get(rectIndexCurrent - 1).X2 -= senseRate;
            rectArcList.get(rectIndexCurrent - 1).resetLines();
            save();
            rectIndexCurrent = a;
            startEdit();
        }
    }
    @FXML
    void RRFunc(ActionEvent event) {
        if(mode.equals("e") && rectIndexCurrent > 0){
            int a = rectIndexCurrent;
            rectArcList.get(rectIndexCurrent - 1).X2 += senseRate;
            rectArcList.get(rectIndexCurrent - 1).resetLines();
            save();
            rectIndexCurrent = a;
            startEdit();
        }
    }
    @FXML
    void UDFunc(ActionEvent event) {
        if(mode.equals("e") && rectIndexCurrent > 0){
            int a = rectIndexCurrent;
            rectArcList.get(rectIndexCurrent - 1).Y1 += senseRate;
            rectArcList.get(rectIndexCurrent - 1).resetLines();
            save();
            rectIndexCurrent = a;
            startEdit();
        }
    }
    @FXML
    void UUFunc(ActionEvent event) {
        if(mode.equals("e") && rectIndexCurrent > 0){
            int a = rectIndexCurrent;
            rectArcList.get(rectIndexCurrent - 1).Y1 -= senseRate;
            rectArcList.get(rectIndexCurrent - 1).resetLines();
            save();
            rectIndexCurrent = a;
            startEdit();
        }
    }
    /**
     * 开始编辑
     * */
    public void startEdit(){
        colorPicker.setValue(rectArcList.get(rectIndexCurrent - 1).color);
        editButton.setText("完成");
        isEditing = true;
    }
    /**
     * 保存
     * */
    public void save(){
        if(currentFilePair != null)//保存
            RectArcList2Xml(actualX2 - actualX1, actualY2 - actualY1, actualX1, actualY1, currentFilePair, rectArcList);
        fitImage();
    }
    /**
     * 结束编辑
     * */
    public void endEdit(){
        colorPicker.setValue(defaultColor);
        isEditing = false;
    }
    /**
     * 绘制临时方框
     * */
    public void paintRect(){
        double x1 = startX - markAbsoluteX;
        double x2 = endX - markAbsoluteX;
        double y1 = startY - markAbsoluteY;
        double y2 = endY - markAbsoluteY;
        setLineData(lu, x1, x2, y1, y1, defaultColor);
        setLineData(ld, x1, x2, y2, y2, defaultColor);
        setLineData(ll, x1, x1, y1, y2, defaultColor);
        setLineData(lr, x2, x2, y1, y2, defaultColor);
    }
    /**
     * 设定线条参数
     * */
    public static void setLineData(Line l, double x1, double x2, double y1, double y2, Color color){
        if(l != null){
            l.setStartX(x1);
            l.setStartY(y1);
            l.setEndX(x2);
            l.setEndY(y2);
            l.setStroke(color);
        }
    }
    /**
     * 固定刷新窗口坐标数据，防止位置跑偏
     * 每次在按下鼠标时执行，防止期间用户拖动改变窗口大小
     * 窗口宽度数据aboveSpiltPane.getWidth()不会在initialize中更新，必须放至此
     * */
    public void locationUpdate(){
        Bounds layoutBounds = markAnchorPane.getLayoutBounds();
        Point2D localToScene = markAnchorPane.localToScene(layoutBounds.getMinX(), layoutBounds.getMinY());
        markAbsoluteX = localToScene.getX();
        markAbsoluteY = localToScene.getY();
    }
    /**
     * 将文件数据装载到fileList
     * */
    public void fileDeploy(File dirFile){
        //获得文件总列表
        File allFiles[] = dirFile.listFiles();

        //获得图片列表和配置文件列表（过滤掉子文件夹和其他文件类型）
        ArrayList<File> picList = new ArrayList<>();
        ArrayList<File> xmlList = new ArrayList<>();
        for (int i = 0; i < allFiles.length; i++) {
            File fs = allFiles[i];
            if (fs.isDirectory()) {
                System.out.println(fs.getName() + " [目录]");
            } else {
                //获得扩展名，如“.png”
                String suffix = fs.getAbsolutePath().
                        substring(fs.getAbsolutePath().lastIndexOf("."));
                //获得文件名（不包含扩展名）
                String name = fs.getName().
                        substring(0, fs.getName().lastIndexOf("."));
                System.out.println(suffix + "   " + name);

                if(suffix.equalsIgnoreCase(".png")
                        || suffix.equalsIgnoreCase(".jpg")
                        || suffix.equalsIgnoreCase(".jpeg")){
                    picList.add(fs);
                }
                else if(suffix.equalsIgnoreCase(".xml")){
                    xmlList.add(fs);
                }

            }
        }

        /**
         * 各列表初始化
         * */
        {
            if(fileList == null) {fileList = new HashMap<>();}
            else {fileList.clear();}

            if (donePicNameList == null) {donePicNameList = new ArrayList<>();}
            else {donePicNameList.clear();}

            if (undonePicNameList == null){undonePicNameList = new ArrayList<>();}
            else{undonePicNameList.clear();}

            if (allPicNameList == null){allPicNameList = new ArrayList<>();}
            else{allPicNameList.clear();}
        }

        //填充fileList、allPicNameList、donePicNameList、undonePicNameList
        Iterator picIt = picList.iterator();
        Iterator xmlIt;

        while(picIt.hasNext()) {
            File picFile = (File) picIt.next();
            boolean createFlag = true;  //当为true时，外循环需要创建一个没有xml的FilePair
            String picName = picFile.getName().substring(0, picFile.getName().lastIndexOf("."));
            xmlIt = xmlList.iterator();

            while (xmlIt.hasNext()) {
                File xmlFile = (File) xmlIt.next();
                String xmlName = xmlFile.getName().substring(0, xmlFile.getName().lastIndexOf("."));
                //创建齐全的FilePair
                if (picName.equals(xmlName)) {
                    allPicNameList.add(picName);
                    donePicNameList.add(picName);
                    FilePair filePair = new FilePair(picFile.getAbsolutePath(), xmlFile.getAbsolutePath(), picName);
                    fileList.put(picName, filePair);
                    xmlList.remove(xmlFile);
                    createFlag = false;
                    break;
                }
            }

            //创建缺省xml配置的FilePair
            if (createFlag) {
                allPicNameList.add(picName);
                undonePicNameList.add(picName);
                FilePair filePair = new FilePair(picFile.getAbsolutePath(), picName);
                fileList.put(picName, filePair);
            }
        }


    }
    /**
     * 将文件列表fileList装载到三个PicListView
     * */
    public void picListDeploy(){
        Iterator iterator = allPicNameList.iterator();
        while(iterator.hasNext()){
            allPicListView.getItems().add(iterator.next());
        }
        iterator = donePicNameList.iterator();
        while(iterator.hasNext()){
            donePicListView.getItems().add(iterator.next());
        }

        iterator = undonePicNameList.iterator();
        while(iterator.hasNext()){
            undonePicListView.getItems().add(iterator.next());
        }


    }
    /**
     * 从xml装载或调整当前图片，以适应当前窗口状态，同时载入文字列表
     * 调用时机：
     * 1.在切换图片时
     * 2.在窗口尺寸变化时
     * */
    public void fitImage(){
        if(markImage != null){
            double maxWidth = markAnchorPane.getWidth();
            double maxHeight = markAnchorPane.getHeight();
            double imgWidth = markImage.getWidth();
            double imgHeight = markImage.getHeight();

            double ratio;//缩放比例

            System.out.println("显示窗口尺寸： (" + maxWidth + ", " + maxHeight + ")");


            markImageView.setImage(markImage);
            if(maxWidth/maxHeight > imgWidth/imgHeight){//这是一个相对高的图片
                /**策略：
                * 缩减宽度，适应高度，头顶顶天，两边留白
                */
                ratio = maxHeight / imgHeight;
                offsetX = maxWidth / 2 - imgWidth * ratio / 2;
                offsetY = 0;
                usedWidth = imgWidth * ratio;
                usedHeight = maxHeight;
                markImageView.setFitHeight(usedHeight);
                markImageView.setX(offsetX);
                markImageView.setY(0);
            }
            else{//这是一个相对宽的图片
                /**策略：
                 * 缩减高度，适应宽度，两边顶天，上下留白
                 * */
                ratio = maxWidth / imgWidth;
                offsetX = 0;
                offsetY = maxHeight / 2 - imgHeight * ratio / 2;
                usedWidth = maxWidth;
                usedHeight = imgHeight * ratio;
                markImageView.setFitWidth(usedWidth);
                markImageView.setX(0);
                markImageView.setY(offsetY);
            }

            //定义画笔范围
            actualX1 = offsetX;
            actualY1 = offsetY;
            actualX2 = offsetX + usedWidth;
            actualY2 = offsetY + usedHeight;

            //消除上一次加载的影响
            resetRectArc();
            resetWord();
            //从xml装载rectArcList
            XmlAccessor.Xml2RectArcList(usedWidth, usedHeight, offsetX, offsetY, this.currentFilePair, this.rectArcList);
            //从rectArcList中载入图像和文字
            loadRectArc();
            loadWord();


        }



//      一个比较失败的窗口最大化测试
//        if(!hasInitialized){
//            rootVBox.getScene().getWindow().focusedProperty().addListener(new ChangeListener<Boolean>() {
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    //scene.getWindow().setFocused(true);//避免打开黑屏
//                    System.out.println("Window Change Checked");
//                    fitImage();
//                }
//            });
//            hasInitialized = true;
//        }
    }
    public void fitImage2(){
        if(markImage != null){
            double maxWidth = markAnchorPane.getWidth();
            double maxHeight = markAnchorPane.getHeight();
            double imgWidth = markImage.getWidth();
            double imgHeight = markImage.getHeight();

            double ratio;//缩放比例

            System.out.println("显示窗口尺寸： (" + maxWidth + ", " + maxHeight + ")");


            markImageView.setImage(markImage);
            if(maxWidth/maxHeight > imgWidth/imgHeight){//这是一个相对高的图片
                /**策略：
                 * 缩减宽度，适应高度，头顶顶天，两边留白
                 */
                ratio = maxHeight / imgHeight;
                offsetX = maxWidth / 2 - imgWidth * ratio / 2;
                offsetY = 0;
                usedWidth = imgWidth * ratio;
                usedHeight = maxHeight;
                markImageView.setFitHeight(usedHeight);
                markImageView.setX(offsetX);
                markImageView.setY(0);
            }
            else{//这是一个相对宽的图片
                /**策略：
                 * 缩减高度，适应宽度，两边顶天，上下留白
                 * */
                ratio = maxWidth / imgWidth;
                offsetX = 0;
                offsetY = maxHeight / 2 - imgHeight * ratio / 2;
                usedWidth = maxWidth;
                usedHeight = imgHeight * ratio;
                markImageView.setFitWidth(usedWidth);
                markImageView.setX(0);
                markImageView.setY(offsetY);
            }

            //定义画笔范围
            actualX1 = offsetX;
            actualY1 = offsetY;
            actualX2 = offsetX + usedWidth;
            actualY2 = offsetY + usedHeight;

            //消除上一次加载的影响
            resetRectArc2();
            resetWord();
            //从xml装载rectArcList
            XmlAccessor.Xml2RectArcList(usedWidth, usedHeight, offsetX, offsetY, this.currentFilePair, this.rectArcList);
            //从rectArcList中载入图像和文字
            loadRectArc();
            loadWord();


        }



//      一个比较失败的窗口最大化测试
//        if(!hasInitialized){
//            rootVBox.getScene().getWindow().focusedProperty().addListener(new ChangeListener<Boolean>() {
//                @Override
//                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                    //scene.getWindow().setFocused(true);//避免打开黑屏
//                    System.out.println("Window Change Checked");
//                    fitImage();
//                }
//            });
//            hasInitialized = true;
//        }
    }
    /**
     * 将所有的矩形框显示，并载入文字列表
     * */
    public void loadRectArc(){
        Iterator it = this.rectArcList.iterator();
        int count = 0;
        while(it.hasNext()){
            count++;
            if(count == rectIndexCurrent){
                flag = 1;
            }
            else{
                flag = 0;
            }
            RectArc rectArc = (RectArc)it.next();
            rectArc.initializeLines(markAnchorPane,flag);
        }
    }
    /**
     * 重设输入框
     * */
    public void resetWord(){
        rectIndex = 1;
        wordListView.getItems().clear();
    }
    /**
     * 装载所有框和文字信息到右侧列表
     * */
    public void loadWord(){
        Iterator it = this.rectArcList.iterator();
        while(it.hasNext()){
            RectArc rectArc = (RectArc)it.next();
            rectArc.initializeWord(wordListView, rectIndex);
            rectIndex++;
        }
    }
    /**
     * 将画面上所有的痕迹删除，清空文字列表
     * */
    public void resetRectArc(){
        if(rectArcList != null){
            Iterator it = rectArcList.iterator();
            while (it.hasNext()){
                RectArc rac = (RectArc)it.next();
                rac.removeLines(markAnchorPane);
                //rac.remove();
                System.out.println("removed");
            }
            wordListView.getItems().clear();
            rectArcList.clear();//问题在这

        }
        else{
            rectArcList = new ArrayList<>();
        }
    }
    public void resetRectArc2(){
        if(rectArcList != null){
            Iterator it = rectArcList.iterator();
            while (it.hasNext()){
                RectArc rac = (RectArc)it.next();
                rac.removeLines(markAnchorPane);
                //rac.remove();
                System.out.println("removed");
            }
            wordListView.getItems().clear();
//            rectArcList.clear();

        }
        else{
            rectArcList = new ArrayList<>();
        }
    }
    /**
     * 清除绘图模块的矩形至初始状态
     * */
    public void setPaintRectDefault(){
        setLineDefault(lu);
        setLineDefault(ld);
        setLineDefault(ll);
        setLineDefault(lr);
    }
    /**
     * 清除单一线条至初始状态
     * */
    public static void setLineDefault(Line l){
        if(l != null){
            l.setStartX(0);
            l.setStartY(0);
            l.setEndX(0);
            l.setEndY(0);
        }
    }
    /**
     * 将当前图片以固定的倍率 根据鼠标位置 显示在放大面板上
     * 传入鼠标当前位置
     * */
    public void setZoomer(double mouseX, double mouseY){

        double zoomHeight = usedHeight * zoomRate;
        double zoomWidth = usedWidth * zoomRate;
        double zoomOffsetX = (mouseX - markAbsoluteX - offsetX) * zoomRate - 175;
        double zoomOffsetY = (mouseY - markAbsoluteY - offsetY) * zoomRate - 100;
        zoomerImageView.setVisible(true);
        zoomerImageView.setImage(markImage);
        zoomerImageView.setFitWidth(zoomWidth);
        zoomerImageView.setFitHeight(zoomHeight);
        zoomerImageView.setX(-zoomOffsetX);
        zoomerImageView.setY(-zoomOffsetY);


    }
    /**
     * 重设放大面板
     * */
    public void resetZoomer(){
        zoomerImageView.setVisible(false);
    }
    /**
     * 重设状态变量
     * */
    public void resetMode(){
        rectModeRadio.setSelected(true);
        rectModeRadio.requestFocus();
    }
    /**
     * 获取对应鼠标位置的框序号，没有则返回-1
     * */
    public int getRectIndex(double mx, double my){
        Iterator it = rectArcList.iterator();
        int RID = -1;
        boolean hasSelected = false;
        while(it.hasNext()){
            RID++;
            if(isInRect((RectArc)(it.next()), mx, my)) {
                hasSelected = true;
                break;
            }
        }
        if(hasSelected)
            return RID;
        else
            return -1;
    }
    /**
     * 判定指定坐标位置是否属于指定框中
     * */
    public boolean isInRect(RectArc ra, double mx, double my){
        boolean xin = false;
        boolean yin = false;
        System.out.println("Rect : (" + ra.X1 + ", " + ra.Y1 + ") ~ (" + ra.X2 + ", " + ra.Y2 + ")");
        System.out.println("Mouse : (" + mx + ", " + my + ")");
        if((mx - ra.X1) * (mx - ra.X2) <= 0)
            xin = true;
        if((my - ra.Y1) * (my - ra.Y2) <= 0)
            yin = true;
        return (xin && yin);

    }
    /**
     * 内部监听类，实现列表点击的图片切换
     * 实现功能：
     * 1.保存上一次的标记
     * 2.清除上一次的绘图痕迹
     * 3.载入下一张图片及配置文件
     * */
    private class imageListItemChangeListener implements ChangeListener<Object> {

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            //除旧
            resetMode();
//            if(isEditing)
//                endEdit();
            System.out.println(oldValue);

            if(rectIndexCurrent > 0){//文字存盘
                rectArcList.get(rectIndexCurrent - 1).setWord(wordTextArea.getText());
                wordListView.getItems().set(rectIndexCurrent - 1, rectIndexCurrent + "." + wordTextArea.getText());
            }
            wordTextArea.setText("");
            rectIndex = 1;
            rectIndexCurrent = 0;

            if(currentFilePair != null)//在换页时自动保存
                RectArcList2Xml(actualX2 - actualX1, actualY2 - actualY1, actualX1, actualY1, currentFilePair, rectArcList);


            //迎新
            System.out.println(newValue);
            FilePair fp = fileList.get(newValue);
            currentFilePair = fp;
            if(fp != null){
                /*
                 *经过实测，以下这种方法初始化Image会导致内存驻留，在高速切换图片时内存爆炸
                 * markImage = new Image("file:" + fp.picURL);
                 * 故改用IO流的方式输入
                 * */
                try {
                    markImage = new Image(Files.newInputStream(Paths.get(fp.picURL)));
                } catch (IOException e) {e.printStackTrace();}
                fitImage();
            }
            setPaintRectDefault();


        }

    }
    /**
     * 内部监听类，实现列表点击的标注文字切换（即选择框的切换）
     * 实现功能：
     * 1.实时保存标注文字
     * 2.改变文字输入框中的内容
     * */
    private class wordListItemChangeListener implements ChangeListener<Object> {

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
//            if(isEditing)
//                endEdit();
            endEdit();

            if(!isRebooting && newValue != null) {
                //除旧
                System.out.println("Word List Item Changed");
                System.out.println(oldValue);
                if (rectIndexCurrent > 0) {
                    rectArcList.get(rectIndexCurrent - 1).setWord(wordTextArea.getText());
                    wordListView.getItems().set(rectIndexCurrent - 1, rectIndexCurrent + "." + wordTextArea.getText());

                }


                //迎新
                System.out.println(newValue);
                String indexStr = ((String) newValue).substring(0, ((String) newValue).lastIndexOf("."));
                rectIndexCurrent = Integer.parseInt(indexStr);
                wordTextArea.setText(rectArcList.get(rectIndexCurrent - 1).getWord());
                fitImage();
                System.out.println(rectIndexCurrent);

            }



        }

    }
    private class resizeChangeListener implements ChangeListener<Object> {

        @Override
        public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
            if(isEditing)
                endEdit();
            fitImage();
        }
    }
    /**
     * 工具函数，将字符串转换为ASCII码值
     * */
    public static String string2Ascii(String value) {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        return sbu.toString();
    }
}

