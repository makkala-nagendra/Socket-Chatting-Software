import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.border.EmptyBorder;
import java.util.*;
import java.io.*;
import javax.swing.border.*;
import java.net.*;

class WriteIP {
    int minPortID = 49152;
    //IP = 000.000.0.00
    // Enter string ="Ip1, Ip2, Ip3, Ip4,.........., IPn"
    String s = "localhost";

    WriteIP() throws IOException {
        File file = new File("usersIPAddress.txt");
        FileWriter writer = new FileWriter(file);
        String[] str = s.split(", ");
        for (int i = 0; i < str.length; i++) {
            writer.write(str[0] + " " + minPortID + "\n");
            writer.flush();
            minPortID++;
        }
        writer.close();
    }

}

class Users {
    String ipAddress;
    int portNumber;

    Users(String ip, int i) {
        this.ipAddress = ip;
        this.portNumber = i;
    }
}

class MySocketChattingSoftware {
    ArrayList<Users> usersIPList = new ArrayList<Users>();
    private ArrayList<JPanel> usersChat = new ArrayList<JPanel>();
    private ArrayList<JButton> usersButtonList = new ArrayList<JButton>();
    JPanel chatBox;
    JFrame mainFrame;
    int minPortID = 49152;

    interface myInterface {
        void connect();

        void disConnect();

        void sendMessage();
    }

    MySocketChattingSoftware() {
        try {

            File file = new File("usersIPAddress.txt");
            if (!file.exists()) {
                WriteIP writeIP = new WriteIP();
                System.out.println("Users File created");
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                usersIPList.add(new Users(st.split(" ")[0], Integer.parseInt(st.split(" ")[1])));
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        mainFrame = new JFrame("My Chating Software");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Users List
        for (int i = 0; i < usersIPList.size(); i++) {
            JPanel jp = myChatBoxContainer(i, usersIPList.get(i).ipAddress, usersIPList.get(i).portNumber);
            minPortID++;
            usersChat.add(jp);
        }

        chatBox = usersChat.get(0);

        for (int i = 0; i < usersIPList.size(); i++) {
            int j = i;
            JButton userIPButton = new JButton(usersIPList.get(i).ipAddress + " " + usersIPList.get(i).portNumber);
            userIPButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    mainFrame.remove(chatBox);
                    chatBox = usersChat.get(j);
                    mainFrame.add(chatBox, BorderLayout.CENTER);
                    mainFrame.validate();
                    mainFrame.repaint();
                }
            });
            usersButtonList.add(userIPButton);
            minPortID++;
        }

        JPanel sideBar = sideBarContainer();
        mainFrame.add(sideBar, BorderLayout.WEST); // NORTH, SOUTH, CENTER, WEST, EAST
        mainFrame.add(chatBox, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    public JPanel sideBarContainer() {
        JPanel sideBar = new JPanel();
        JPanel usersList = new JPanel();
        sideBar.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane();
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setBackground(new Color(131, 144, 97));
        sideBar.setLayout(new FlowLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        scroll.setPreferredSize(new Dimension((int) (screenSize.width / 6.5), (int) (screenSize.height / 1.05)));

        usersList.setLayout(new BoxLayout(usersList, BoxLayout.Y_AXIS));
        System.out.println(usersButtonList.size());
        for (int i = 0; i < usersButtonList.size(); i++) {
            usersList.add(usersButtonList.get(i));
        }

        scroll.setViewportView(usersList);
        sideBar.add(scroll, BorderLayout.CENTER);
        return sideBar;
    }

    JPanel myChatBoxContainer(int index, String ipAddres, int portID) {
        JPanel chatBoxJPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        JPanel statusPanel = new JPanel();
        JPanel chatContainer = new JPanel();
        JLabel connectionJLabel = new JLabel("");
        JButton conectButton = new JButton(" Connect ");
        JButton disconectButton = new JButton(" Disconnect ");

        JPanel bottomBar = new JPanel();
        bottomBar.setBackground(new Color(91, 154, 212));
        bottomBar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JTextArea textArea = new JTextArea(3, 60);
        textArea.setLineWrap(true);
        textArea.setEditable(true);
        textArea.setVisible(true);
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        myInterface connectionInterface = new myInterface() {
            Socket socket;
            ServerSocket serverSocket;
            boolean serverFlag = false;
            boolean disconnectFlag = false;
            DataInputStream dInput = null;
            String message = "";
            JPanel reciverPanel, senderPanel;
            JTextArea reciverTextArea, senderTextArea;
            DataOutputStream dOutput = null;
            Thread connectionThread = new Thread();
            Thread clientReaderThread = new Thread();
            Thread serverReaderThread = new Thread();

            public void connect() {
                if (connectionThread.isAlive())
                    connectionThread.stop();
                if (!connectionThread.isAlive()) {
                    connectionThread = connectionThreadCode();
                    connectionThread.start();
                }
            };

            public void disConnect() {
                disconnectFlag = true;
                connectionThread.interrupt();
                connectionThread.stop();
                connectionJLabel.setText(" Disconnected... ");
                disconnectFlag = false;
                serverFlag = !serverFlag;
            };

            public void sendMessage() {
                serverWriterSender();
            }

            Thread connectionThreadCode() {
                return new Thread() {
                    public void run() {
                        try {
                            if (serverFlag == true) {
                                serverSocket = new ServerSocket(portID);
                                connectionJLabel.setText("Waiting for connect to the Client... " + portID);
                                serverReaderThread = serverReaderThreadCode();
                                socket = serverSocket.accept();
                                connectionJLabel.setText("Connected to the Client" + portID);
                                serverReaderThread.start();
                            } else {
                                connectionJLabel.setText("Waiting for connect to the Server... " + portID);
                                socket = new Socket(ipAddres, portID);
                                clientReaderThread = clientReaderThreadCode();
                                clientReaderThread.start();
                                connectionJLabel.setText("Connected to the Server" + portID);
                            }
                        } catch (Exception ex) {
                            System.out.println(ex);
                            serverFlag = true;
                            connectionJLabel.setText("Try again to connecte... " + portID);

                        }
                    }

                    public void interrupt() {
                        try {
                            connectionJLabel.setText(" Disconnected... ");
                            if (socket != null) {
                                socket.close();
                                dInput.close();
                                dOutput.close();
                            }
                            if (serverSocket != null)
                                serverSocket.close();
                            if (serverReaderThread.isAlive())
                                serverReaderThread.stop();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                };
            }

            Thread serverReaderThreadCode() {
                return new Thread() {
                    public void run() {
                        try {
                            dInput = new DataInputStream(socket.getInputStream());
                            dOutput = new DataOutputStream(socket.getOutputStream());
                            while (!disconnectFlag) {
                                message = dInput.readUTF();
                                reciverPanel = new JPanel();
                                reciverTextArea = new JTextArea();
                                Border blackline = BorderFactory.createLineBorder(Color.black);
                                // reciverPanel.setLayout(new BoxLayout(lisJPanel, BoxLayout.Y_AXIS));
                                reciverTextArea.setText("\n" + message + "\n");
                                reciverTextArea.setEditable(false);
                                reciverTextArea.setLineWrap(true);
                                reciverTextArea.setVisible(true);
                                // reciverTextArea.setBackground(new Color(66, 152, 95));
                                reciverTextArea.setAlignmentY(0);
                                reciverTextArea.setSize(500, 700);
                                reciverPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                                reciverTextArea.setBorder(blackline);
                                reciverPanel.add(reciverTextArea, BorderLayout.CENTER);
                                chatContainer.add(reciverPanel);
                                chatContainer.revalidate();
                                chatContainer.repaint();
                            }
                            dInput.close();
                            serverSocket.close();
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                };
            }

            void serverWriterSender() {
                try {
                    dOutput.writeUTF(textArea.getText());
                    dOutput.flush();
                    senderPanel = new JPanel();
                    Border blackline = BorderFactory.createLineBorder(Color.black);
                    // senderPanel.setLayout(new BoxLayout(lisJPanel, BoxLayout.Y_AXIS));
                    senderTextArea = new JTextArea("\n" + textArea.getText() + "\n");
                    senderTextArea.setEditable(false);
                    senderTextArea.setLineWrap(true);
                    senderTextArea.setVisible(true);
                    senderTextArea.setBackground(new Color(66, 152, 95));
                    senderTextArea.setAlignmentY(0);
                    senderTextArea.setSize(500, 700);
                    senderPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    senderTextArea.setBorder(blackline);
                    senderPanel.add(senderTextArea, BorderLayout.CENTER);
                    chatContainer.add(senderPanel);
                    chatContainer.revalidate();
                    chatContainer.repaint();
                    textArea.setText("");
                } catch (Exception e) {
                    System.out.println(e);
                }

            }

            Thread clientReaderThreadCode() {
                return new Thread() {
                    public void run() {
                        try {
                            dInput = new DataInputStream(socket.getInputStream());
                            dOutput = new DataOutputStream(socket.getOutputStream());
                            while (!disconnectFlag) {
                                message = dInput.readUTF();
                                reciverPanel = new JPanel();
                                reciverTextArea = new JTextArea();
                                Border blackline = BorderFactory.createLineBorder(Color.black);
                                // reciverPanel.setLayout(new BoxLayout(lisJPanel, BoxLayout.Y_AXIS));
                                reciverTextArea.setText("\n" + message + "\n");
                                reciverTextArea.setEditable(false);
                                reciverTextArea.setLineWrap(true);
                                reciverTextArea.setVisible(true);
                                // reciverTextArea.setBackground(new Color(66, 152, 95));
                                reciverTextArea.setAlignmentY(0);
                                reciverTextArea.setSize(500, 700);
                                reciverPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                                reciverTextArea.setBorder(blackline);
                                reciverPanel.add(reciverTextArea, BorderLayout.CENTER);
                                chatContainer.add(reciverPanel);
                                chatContainer.revalidate();
                                chatContainer.repaint();
                            }
                            dInput.close();
                            socket.close();
                        } catch (Exception e) {
                            System.out.println(e);
                            connectionThread.interrupt();
                            connectionThread.stop();
                        }
                    }
                };
            }

        };

        JButton jButton = new JButton(" Send ");
        jButton.setBounds(50, 100, 95, 30);
        jButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (textArea.getText() != "" && textArea.getText() != " ")
                    connectionInterface.sendMessage();
            }
        });
        bottomBar.add(scroll);
        bottomBar.add(jButton);

        connectionJLabel.setText("ID: " + portID);
        scrollPane.setBackground(new Color(66, 152, 95));
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        chatBoxJPanel.setPreferredSize(new Dimension((int) (screenSize.width / 1.3), (int) (screenSize.height / 1.2)));
        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
        chatContainer.setBackground(new Color(66, 152, 95));
        scrollPane.setViewportView(chatContainer);
        chatBoxJPanel.setLayout(new BorderLayout());
        conectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectionJLabel.setText(" Connected... " + index);
                connectionInterface.connect();
            }
        });

        disconectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectionJLabel.setText(" Disconnected... " + index);
                connectionInterface.disConnect();
            }
        });

        statusPanel.add(connectionJLabel);
        statusPanel.add(conectButton);
        statusPanel.add(disconectButton);

        chatBoxJPanel.add(statusPanel, BorderLayout.NORTH);
        chatBoxJPanel.add(scrollPane, BorderLayout.CENTER);
        chatBoxJPanel.add(bottomBar, BorderLayout.SOUTH);
        return chatBoxJPanel;
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MySocketChattingSoftware();
            }
        });
    }
}
