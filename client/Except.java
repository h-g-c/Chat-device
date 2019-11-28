package chat.client;



import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class Except implements  Runnable
{
    Socket socket;
    String message="";
    BufferedReader readerMessage;
    client clientWindows;
    List<String> userList;
    String name;
    Except(String name, Socket socket, client clientWindows)
    {   this.name=name;
        this.socket=socket;
        this.clientWindows=clientWindows;
        userList=new ArrayList<>();
    }
    @Override
    public void run() {
        try{
            readerMessage=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while(true) {
                message = readerMessage.readLine();
                if (message.equals(name + " : close"))
                    break;
                if (message.equals("exceptFile"))
                    exceptFile();
                else {
                    String[] messages = message.split(":");
                    if (messages[0].equals("getUserList")) {
                        userList.add(messages[1]);
                    } else {
                        clientWindows.addMessage(message);
                    }
                }
            }
            socket.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }finally {
            try {
                readerMessage.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    synchronized List<String> getUserList()
    { List<String> result=new ArrayList<>(userList);
       userList.clear();
        return result;
    }
    synchronized void exceptFile() throws IOException {
        String sendName=readerMessage.readLine();
        JOptionPane.showMessageDialog(null, "接受来自于"+sendName+"的文件", "提示",JOptionPane.PLAIN_MESSAGE);
        FileOutputStream fileOutputStream;
        DataInputStream das = new DataInputStream(socket.getInputStream());
        String fileName=das.readUTF();
        Long fileLength=das.readLong();
        int option = -1;
        File filepath = null;
        while(option!=JFileChooser.APPROVE_OPTION) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setVisible(true);
             option = fileChooser.showOpenDialog(clientWindows);
            if (option == JFileChooser.APPROVE_OPTION) {
                filepath = fileChooser.getSelectedFile();;
            }
        }
        File file=new File(filepath.getAbsolutePath()+File.separatorChar+fileName);
        fileOutputStream =new FileOutputStream(file);
        System.out.println("======== 开始接收文件 ========");
        byte[] bytes = new byte[1024];
        int length = 0;
        while(true) {
            length = das.read(bytes, 0, bytes.length);
            fileOutputStream.write(bytes, 0, length);
            fileOutputStream.flush();
            if(fileLength == file.length())
                break;
        }
        System.out.println("======== 文件接受成功 ========");
        clientWindows.addMessage("收到来自于"+sendName+"的文件");
    }
}

