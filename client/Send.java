package chat.client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

class Send {
    Socket socket = null;
    PrintWriter printWriter = null;
    String message = "";
    String name;
    File file;
    FileInputStream fis;
    DataOutputStream das;
    client clientWindows;
    JTextArea messageText;

    Send(Socket socket, String name, client clientWindows) throws IOException {
        this.socket = socket;
        this.name = name;
        this.clientWindows = clientWindows;
        messageText = clientWindows.getMessageArea();
        printWriter = new PrintWriter(socket.getOutputStream());
        printWriter.println(name);
        printWriter.flush();
    }

    synchronized void requestGetUserList() {
        printWriter.println("getUserList");
        printWriter.flush();
    }

    synchronized void close() {
        printWriter.println(name+":close");
        printWriter.flush();
    }

    synchronized void sendFile(String filePath) throws IOException {
        File file=new File(filePath);
        printWriter.println("FILE:"+clientWindows.getAddressee());
        printWriter.flush();
        fis=new FileInputStream(file);
        das=new DataOutputStream(socket.getOutputStream());
        das.writeUTF(file.getName());
        das.flush();
        das.writeLong(file.length());
        das.flush();
        System.out.println("======== 开始传输文件 ========");
        byte[] bytes = new byte[1024];
        int length = 0;
        while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
            das.write(bytes, 0, length);
            das.flush();
        }
        System.out.println("======== 文件传输成功 ========");
    }


    synchronized public void sendMessage() {
        message = messageText.getText();
        if (!message.equals("")) {
            clientWindows.addMessage("我发信息给"+clientWindows.getAddressee() +"   " + message);
            printWriter.println(clientWindows.getAddressee() + ":" + message);
            printWriter.flush();
        }
    }
    synchronized public void sendErrno(String message)
    {printWriter.println(message);
    printWriter.close();
    }
}
