package chat.client;


import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class client extends JFrame implements Runnable {
    String host = "10.164.27.237";
    int port = 8081;
    Socket service;
    String name;
    Thread threadSend;
    private JTextArea messageHistoryArea;
    private JTextArea messageArea;
    JButton sendButton;
    JButton searchUserButton;
    JTextField sendName;
    String addressee;
    chat.client.userList userList;
    Send send;
    Except except;
    JButton sendFile;

    public JTextArea getMessageArea() {
        return messageArea;
    }

    public String getAddressee() {
        return addressee;
    }

    public void setAddress(String name) {
        sendName.setEditable(true);
        addressee = name;
        sendName.setText(name);
        sendName.setEditable(false);
    }

    public client(String name) throws IOException {
        super(name + "的聊天栏");
        userList = new userList(this);
        service = new Socket(host, port);
        this.name = name;
        this.setSize(800, 600);
        int x, y;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        x = (size.width - 800) / 4;
        y = (size.height - 600) / 2;
        setLocation(x, y);//让程序界面显示在屏幕中央
        setLocationRelativeTo(null);
        init();
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                send.close();
            }
        });
        setVisible(true);
    }


    void init() throws IOException {
        messageHistoryArea = new JTextArea(30, 10);
        sendName = new JTextField(50);
        messageHistoryArea.setEditable(false);
        sendName.setEditable(false);
        sendName.setSize(200, 40);
        messageHistoryArea.setDocument(new PlainDocument());//控制输入内容的显示
        JScrollPane jScrollPane = new JScrollPane(messageHistoryArea);//使文本框具有滚动条
        JPanel jPanel = new JPanel();
        JPanel jPanel1 = new JPanel();
        messageArea = new JTextArea(5, 80);

        sendButton = new JButton("发送");
        sendButton.setSize(25, 10);
        sendButton.addActionListener(e -> {
            send.sendMessage();
        });

        searchUserButton = new JButton("查看在线用户");
        searchUserButton.addActionListener(e -> {
            send.requestGetUserList();
            try {
                Thread.sleep(20);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            userList.init(except.getUserList());
        });
        searchUserButton.setSize(25, 10);

        sendFile=new JButton("发送文件");
        sendButton.setSize(25, 10);
        sendFile.addActionListener(e->{
            FileDialog openFile = new FileDialog(this, "打开文件...", FileDialog.LOAD);
            openFile.setVisible(true);
            if(openFile.getDirectory()!=null) {
                String filePath = openFile.getDirectory() + openFile.getFile();
                System.out.println(filePath);

                try {
                    send.sendFile(filePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        });

        JLabel jLabel = new JLabel("聊天对象");
        JLabel jLabel1 = new JLabel("信息栏");

        jPanel1.add(jLabel, BorderLayout.EAST);
        jPanel1.add(sendName, BorderLayout.CENTER);
        jPanel1.add(searchUserButton, BorderLayout.WEST);

        jPanel.add(BorderLayout.EAST, jLabel1);
        jPanel.add(BorderLayout.CENTER, messageArea);
        jPanel.add(BorderLayout.WEST, sendButton);
        jPanel.add(BorderLayout.SOUTH,sendFile);

        this.add(jScrollPane, BorderLayout.NORTH);
        this.add(jPanel1, BorderLayout.CENTER);
        this.add(jPanel, BorderLayout.SOUTH);
    }


    public static void main(String[] args) throws IOException {
        System.out.println("客户端即将启动");
        Scanner cin = new Scanner(System.in);
        String name = cin.nextLine();
        while (true) {
            new Thread(new client(name)).start();
            name = cin.nextLine();
        }

    }

    public void addMessage(String message) {
        messageHistoryArea.setEditable(true);
        messageHistoryArea.append(message + "\n");
        messageHistoryArea.setEditable(false);
    }


    @Override
    public void run() {
        try {
            send = new Send(service, name, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        threadSend = new Thread(except = new Except(name, service, this));
        threadSend.start();
    }
}

