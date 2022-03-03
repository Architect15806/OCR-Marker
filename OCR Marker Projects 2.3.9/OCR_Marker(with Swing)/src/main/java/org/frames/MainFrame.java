package org.frames;
import org.listeners.MyMouseListener;
import org.structures.FilePair;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainFrame extends JFrame implements ActionListener{
    public MyMouseListener myMouseListener;
    public static PicPanel picPanel;

    public static final int DEFAULT_MAX_WIDTH = 1200;
    public static final int DEFAULT_MAX_HEIGHT = 900;
    public static final int INIT_WIDTH = 400;
    public static final int INIT_HEIGHT = 300;

    public JMenuBar jMenuBar;
    public JMenu jMenu1;
    public JMenuItem jMenuItem_Open, jMenuItem_OpenDir;
    public JButton jButton_Switch;
    public JPanel buttonPanel, listPanel, defaultBlankPanel;
    public JList overlookList;
    public ListModel overlookListModel;
    public JScrollPane scrollPane;

    public static int modeFlag = 0;
    public static boolean initFlag = true;
    public static ArrayList<FilePair> fileList;

    public MainFrame() throws HeadlessException {
        myMouseListener = new MyMouseListener();
            //初始化鼠标监听

        {
            jMenuBar = new JMenuBar();
            jMenu1 = new JMenu("文件");
            jMenuBar.add(jMenu1);
            this.getContentPane().add(jMenuBar, BorderLayout.NORTH);

            jMenuItem_Open = new JMenuItem("打开");
            jMenuItem_Open.addActionListener(this);
            jMenuItem_Open.setActionCommand("open");
            jMenu1.add(jMenuItem_Open);

            jMenuItem_OpenDir = new JMenuItem("打开文件夹");
            jMenuItem_OpenDir.addActionListener(this);
            jMenuItem_OpenDir.setActionCommand("openDir");
            jMenu1.add(jMenuItem_OpenDir);
        }//界面元素初始化：顶部菜单栏

        {
            jButton_Switch = new JButton("当前：选择模式");
            jButton_Switch.addActionListener(this);
            jButton_Switch.setActionCommand("switchMode");

            buttonPanel = new JPanel();
            buttonPanel.add(jButton_Switch);
            buttonPanel.setLayout(new GridLayout(1, 2));
            //buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        }//界面元素初始化：按钮

        {
            listPanel = new JPanel();
            listPanel.setPreferredSize(new Dimension(150, this.getHeight()));
            overlookList = new JList();
            overlookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            //设置scrollPane滚动面板的大小与listPanel同步
            scrollPane = new JScrollPane(){
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(listPanel.getWidth(), listPanel.getHeight());//括号内参数，可以根据需要更改
                }
            };
            //scrollPane.setPreferredSize(new Dimension(135, INIT_HEIGHT - 20));
            scrollPane.setViewportView(overlookList);
            listPanel.add(new JLabel("图片列表"), BorderLayout.NORTH);
            listPanel.add(scrollPane, BorderLayout.CENTER);
            //在双击overlookList中的图片时启动新的画布
            overlookList.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(e.getClickCount() == 2){
                        System.out.println("new one");
                        JList myList = (JList) e.getSource();
                        setPicPanel(fileList.get(myList.getSelectedIndex()));
                    }
                }
            });
            //listPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

            this.getContentPane().add(listPanel, BorderLayout.EAST);
        }//界面元素初始化：图片选择菜单

        {
            defaultBlankPanel = new JPanel();
            defaultBlankPanel.setPreferredSize(new Dimension(INIT_WIDTH, INIT_HEIGHT));
            //defaultBlankPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            this.getContentPane().add(defaultBlankPanel, BorderLayout.CENTER);
        }//起始页初始化

        this.setTitle("OCR Marker");
        this.setLocation(300, 5);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        this.pack();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals("open")){	            //主屏指令：打开
            Action_Open();
        }

        else if(e.getActionCommand().equals("openDir")){        //主屏指令：打开文件夹
            Action_OpenDir();
        }

        else if(e.getActionCommand().equals("switchMode")){     //按钮指令：选择0/框选1模式切换
            Action_ModeSwitch();
        }
    }

    public void Action_Open(){
        JFileChooser jFileChooser = new  JFileChooser(new File("."));

        int status=jFileChooser.showOpenDialog(this);
        //没有选打开按钮结果提示
        if(status!=JFileChooser.APPROVE_OPTION){
            System.out.println("没有选中文件");
        }
        else{
            File file = jFileChooser.getSelectedFile();
            String suffix = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
            System.out.println(suffix);
            if(suffix.equalsIgnoreCase(".png")
                    || suffix.equalsIgnoreCase(".jpg")
                    || suffix.equalsIgnoreCase(".jpeg")){
                setPicPanel(new FilePair(file.getAbsolutePath()));
            }
            else
                JOptionPane.showMessageDialog(null, "请选择JPEG或PNG格式的文件", "警告",JOptionPane.WARNING_MESSAGE);

        }
    }

    public void Action_OpenDir(){
        JFileChooser jFileChooser = new  JFileChooser(new File("."));
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int status = jFileChooser.showOpenDialog(this);
        //没有选打开按钮结果提示

        if(status!=JFileChooser.APPROVE_OPTION){
            System.out.println("没有选中文件");
        }
        else{
            File file = jFileChooser.getSelectedFile();
            String filePath = jFileChooser.getSelectedFile().getPath();
            System.out.println(filePath);

            if (!file.exists()) {
                System.out.println(file.getAbsolutePath() + " not exists");
                JOptionPane.showMessageDialog(null, "请选择正确的文件夹", "警告",JOptionPane.WARNING_MESSAGE);
            }

            //获得文件总列表
            File allFiles[] = file.listFiles();

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

            //填充fileList
            if(fileList == null)
                fileList = new ArrayList<>();
            else
                fileList.clear();

            Iterator picIt = picList.iterator();
            Iterator xmlIt;

            while(picIt.hasNext()){
                File picFile = (File)picIt.next();
                boolean createFlag = true;  //当为true时，外循环需要创建一个没有xml的FilePair
                String picName = picFile.getName().substring(0, picFile.getName().lastIndexOf("."));
                xmlIt = xmlList.iterator();

                while(xmlIt.hasNext()){
                    File xmlFile = (File)xmlIt.next();
                    String xmlName = xmlFile.getName().substring(0, xmlFile.getName().lastIndexOf("."));
                    if(picName.equals(xmlName)){//创建齐全的FilePair
                        FilePair filePair = new FilePair(picFile.getAbsolutePath(), xmlFile.getAbsolutePath());
                        fileList.add(filePair);
                        xmlList.remove(xmlFile);
                        createFlag = false;
                        break;
                    }
                }
                if(createFlag){//创建缺省xml配置的FilePair
                    FilePair filePair = new FilePair(picFile.getAbsolutePath());
                    fileList.add(filePair);
                }
            }

            //装载到overList中，界面展示
            Iterator iterator = fileList.iterator();
            String[] overlookContent = new String[fileList.size() + 1];
            int olc = 0;
            while (iterator.hasNext()){
                FilePair filePair = (FilePair)iterator.next();
                System.out.println(filePair);//打印fileList
                overlookContent[olc] = filePair.picture.getName();
                olc++;
            }

            overlookListModel = new DefaultComboBoxModel(overlookContent);
            overlookList.setModel(overlookListModel);


        }
    }

    public void Action_ModeSwitch(){
        if(modeFlag == 0){
            modeFlag = 1;
            jButton_Switch.setText("当前：框选模式");
        }
        else{
            modeFlag = 0;
            jButton_Switch.setText("当前：选择模式");
        }
    }

    //装载指定FilePair的图层
    public void setPicPanel(FilePair filePair){

        BufferedImage pic = null;
        try {pic = ImageIO.read(filePair.picture); } catch (IOException e) { e.printStackTrace();}
        int width = pic.getWidth();
        int height = pic.getHeight();

        //调整width和height使得它们适合画布大小
        if(width > DEFAULT_MAX_WIDTH && height > DEFAULT_MAX_HEIGHT){
            if(width/DEFAULT_MAX_WIDTH >= height/DEFAULT_MAX_HEIGHT){
                height = DEFAULT_MAX_WIDTH * height / width ;
                width = DEFAULT_MAX_WIDTH;
            }
            else {
                width = DEFAULT_MAX_HEIGHT * width / height;
                height = DEFAULT_MAX_HEIGHT;
            }
        }
        else if(width > DEFAULT_MAX_WIDTH && height <= DEFAULT_MAX_HEIGHT){
            height = DEFAULT_MAX_WIDTH * height / width;
            width = DEFAULT_MAX_WIDTH;
        }
        else if(width <= DEFAULT_MAX_WIDTH && height > DEFAULT_MAX_HEIGHT){
            width = DEFAULT_MAX_HEIGHT * width / height;
            height = DEFAULT_MAX_HEIGHT;
        }

        //重构画布picPanel
        if(picPanel == null)
            picPanel = new PicPanel(myMouseListener, width, height, pic, filePair);
        else
            picPanel.resetPicPanel(width, height, pic, filePair);

        if(initFlag) {
            initFlag = false;
            this.remove(defaultBlankPanel);
        }

        picPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        picPanel.setPreferredSize(new Dimension(width, height));
        this.getContentPane().add(picPanel, BorderLayout.CENTER);
        this.pack();
    }
}

