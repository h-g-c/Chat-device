package chat.server;


import java.io.*;
import java.net.Socket;

class Send implements Runnable {
    Socket socket = null;
    PrintWriter printWriter = null;
    String message;
    BufferedReader cout;
    String name;
    DataInputStream getFile;
    DataOutputStream outFile;


    Send(Socket socket) throws IOException {
        this.socket = socket;
        cout = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        printWriter = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            name = cout.readLine();
            System.out.println("昵称为　" + name + " 的用户已连接");
            Server.getSocketMap().put(name, socket);
            message = cout.readLine();
            while (message != null) {
                if(message.equals("getUserList"))
                {  for (String user:Server.getSocketMap().keySet())
                {
                    printWriter.println("getUserList:"+user);
                    printWriter.flush();
                }

                }
               else {
                    String[] messages = message.split(":");
                    if(messages[0].equals("FILE"))
                        getFile(messages[1]);
                    else if ((Server.getSocketMap().get(messages[0]) != null)) {
                        PrintWriter send = new PrintWriter(Server.getSocketMap().get(messages[0]).getOutputStream());
                        send.println(name + " : " + messages[1]);
                        send.flush();
                    }
                }
                message = cout.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Server.getSocketMap().remove(name);
            System.out.println(name+" 退出连接");
            printWriter.close();
        }

    }

    void getFile(String sendUser) throws IOException {
        FileOutputStream fileOutputStream;
        DataInputStream das = new DataInputStream(socket.getInputStream());
        String fileName=das.readUTF();
        Long fileLength=das.readLong();
        File serviceDirectory=new File("serviceDirectory");
        if(!serviceDirectory.exists())
        serviceDirectory.mkdir();
        File file=new File(serviceDirectory.getAbsolutePath()+File.separatorChar+fileName);
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
        PrintWriter send = new PrintWriter(Server.getSocketMap().get(sendUser).getOutputStream());
        send.println("exceptFile");
        send.flush();
        send.println(name);
        send.flush();
            FileInputStream getFile=new FileInputStream(file);
            DataOutputStream outFile=new DataOutputStream(Server.getSocketMap().get(sendUser).getOutputStream());
            outFile.writeUTF(file.getName());
            outFile.flush();
            outFile.writeLong(file.length());
            outFile.flush();
            System.out.println("======== 开始传输文件 ========");
            bytes = new byte[1024];
            while ((length = getFile.read(bytes, 0, bytes.length)) != -1) {
                outFile.write(bytes, 0, length);
                outFile.flush();
            }
            System.out.println("======== 文件传输成功 ========");


    }
    }


