package chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class userList extends JFrame {
    List<JButton> buttonList;
    client myClient;

    userList(client client) {
        this.myClient = client;
        buttonList = new ArrayList<>();
        this.setSize(800, 600);
        int x, y;
        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
        x = (size.width - 800) / 4;
        y = (size.height - 600) / 2;
        setLocation(x, y);//让程序界面显示在屏幕中央
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                removeButton();
            }
        });

    }

    void init(List<String> list) {
        int number = list.size();

        this.setLayout(new GridLayout(3, number / 3 + 1));
        for (String name : list) {
            JButton button = new JButton(name);
            button.addActionListener(e -> {
                myClient.setAddress(name);
                removeButton();
                this.dispose();
            });
            buttonList.add(button);
        }
        this.setTitle("在线人数为" + number);
        for (JButton button : buttonList)
            this.add(button);
        setVisible(true);
    }

    void removeButton() {
        for (JButton jButton : buttonList)
            remove(jButton);
        buttonList.clear();
    }
}

