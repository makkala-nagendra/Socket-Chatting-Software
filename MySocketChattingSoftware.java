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
import java.awt.*;
import java.awt.geom.*;

class WriteIP {
    int minPortID = 50000;
    // IP = 000.000.0.00
    // Enter string ="Ip1, Ip2, Ip3, Ip4,.........., IPn"
    String s = "localhost";

    // It is for write Users IP's List
    WriteIP() throws IOException {
        String myName = System.getProperty("user.name");
        File file = new File("C:\\Users\\" + myName + "\\AppData\\Local\\MySocketChattingSoftware\\usersIPAddress.txt");
        FileWriter writer = new FileWriter(file);
        String[] str = s.split(", ");
        for (int i = 0; i < str.length; i++) {
            writer.write(
                    "  " + "System_" + i + "  " + "/" + "  " + str[i] + "  " + "/" + "  " + minPortID + "  " + "\n");
            writer.flush();
            minPortID++;
        }
        writer.close();
    }

}

class Users {
    String userName;
    String ipAddress;
    int portNumber;

    Users(String name, String ip, int i) {
        this.userName = name;
        this.ipAddress = ip;
        this.portNumber = i;
    }
}

public class MySocketChattingSoftware {
    ArrayList<Users> usersIPList = new ArrayList<Users>();
    private ArrayList<JPanel> usersChat = new ArrayList<JPanel>();
    private ArrayList<JButton> usersButtonList = new ArrayList<JButton>();
    JPanel chatBox;
    JFrame mainFrame;
    JPanel sideBar;
    int minPortID = 50000;
    int currentUser = 0;
    String myIP;
    String myName;
    int myPortNumber;

    // Creating Interface
    interface myInterface {
        void connect();

        void disConnect();

        void sendMessage();
    }

    MySocketChattingSoftware() {
        // Reading the Users IP & PortNumber usersIPAddress.txt
        try {
            myIP = InetAddress.getLocalHost().getHostAddress();
            myName = System.getProperty("user.name");
            File file = new File(
                    "C:\\Users\\" + myName + "\\AppData\\Local\\MySocketChattingSoftware\\usersIPAddress.txt");
            if (!file.exists())
                file.getParentFile().mkdirs();
            if (file.length() == 0) {
                WriteIP writeIP = new WriteIP();
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                String str = st.replaceAll("\\s+", "");
                String[] strList = str.split("/");
                usersIPList.add(new Users(strList[0], strList[1], Integer.parseInt(strList[2])));
                System.out.println(strList[2]);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        currentUser = usersIPList.get(0).portNumber;
        AbstractBorder border = new TextBubbleBorder(Color.BLACK, 2, 8, 0);
        // Main Frame
        mainFrame = new JFrame("Chatting Software");
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setMinimumSize(new Dimension(900, 600));
        // Users IP&PortNumber List
        for (int i = 0; i < usersIPList.size(); i++) {
            JPanel jp = myChatBoxContainer(i, usersIPList.get(i).userName, usersIPList.get(i).ipAddress,
                    usersIPList.get(i).portNumber);
            minPortID++;
            usersChat.add(jp);
        }
        // Main Chat Box
        chatBox = usersChat.get(0);
        for (int i = 0; i < usersIPList.size(); i++) {
            int j = i;
            String current = usersIPList.get(i).ipAddress;
            System.out.println(current);
            if (!myIP.equals(current)) {
                JButton userIPButton = new JButton(usersIPList.get(i).userName);
                userIPButton.setBorder(border);
                userIPButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        mainFrame.remove(chatBox);
                        chatBox = usersChat.get(j);
                        mainFrame.add(chatBox, BorderLayout.CENTER);
                        mainFrame.validate();
                        mainFrame.repaint();
                        currentUser = j;
                    }
                });
                usersButtonList.add(userIPButton);
            } else {
                myPortNumber = usersIPList.get(i).portNumber;
                System.out.println(myName + " = " + current + " : " + myPortNumber);
            }
            minPortID++;
        }

        System.out.println(myName + " = " + myIP + " : " + myPortNumber);

        if (myPortNumber == 0)
            myPortNumber = 50000;
        sideBar = sideBarContainer();
        mainFrame.add(sideBar, BorderLayout.WEST); // NORTH, SOUTH, CENTER, WEST, EAST
        mainFrame.add(chatBox, BorderLayout.CENTER);
        mainFrame.setVisible(true);
    }

    public JPanel sideBarContainer() {
        JPanel sideBar = new JPanel();
        JPanel usersList = new JPanel();
        JLabel jLabel = new JLabel("<html><h3>" + "Devloped By M.Nagendra" + "</h3></html>");
        JScrollPane scroll = new JScrollPane();
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sideBar.setLayout(new BoxLayout(sideBar, BoxLayout.Y_AXIS));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        scroll.setPreferredSize(new Dimension((int) (screenSize.width / 8), (int) (screenSize.height / 1.15)));

        usersList.setLayout(new BoxLayout(usersList, BoxLayout.Y_AXIS));
        for (int i = 0; i < usersButtonList.size(); i++) {
            JPanel jPanel = new JPanel(new GridLayout(1, 1));
            jPanel.setMaximumSize(new Dimension(250, 40));
            JButton button = usersButtonList.get(i);
            button.setMaximumSize(new Dimension(200, 40));
            button.setBackground(Color.LIGHT_GRAY);
            jPanel.add(button);
            usersList.add(jPanel);
        }
        JPanel emptyJPanel = new JPanel();
        emptyJPanel.setSize(100, 100);
        scroll.setViewportView(usersList);
        sideBar.add(jLabel);
        sideBar.add(scroll);
        sideBar.add(emptyJPanel);

        return sideBar;
    }

    JPanel myChatBoxContainer(int index, String userName, String ipAddres, int portID) {
        AbstractBorder border = new TextBubbleBorder(Color.BLACK, 2, 6, 0);
        JPanel chatBoxJPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        JPanel statusPanel = new JPanel();
        JPanel chatContainer = new JPanel();
        JLabel connectionJLabel = new JLabel("");
        JButton conectButton = new JButton(" Connect ");
        JButton disconectButton = new JButton(" Disconnect ");

        conectButton.setBorder(border);
        disconectButton.setBorder(border);

        JPanel bottomBar = new JPanel();
        bottomBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        JTextArea textArea = new JTextArea(4, 50);
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
            Thread messageReaderThread = new Thread();

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
                disconnectFlag = false;
                serverFlag = !serverFlag;
                if (messageReaderThread.isAlive()) {
                    messageReaderThread.interrupt();
                    messageReaderThread.stop();
                }
                connectionJLabel.setText(" Disconnected...");
            };

            public void sendMessage() {
                if (socket != null)
                    messageSender();
            };

            Thread connectionThreadCode() {
                return new Thread() {
                    public void run() {
                        try {
                            if (serverFlag == true) {
                                if (messageReaderThread.isAlive()) {
                                    messageReaderThread.interrupt();
                                    messageReaderThread.stop();
                                }
                                serverSocket = new ServerSocket(portID);
                                connectionJLabel.setText("Waiting for " + userName + " get in Online...");
                                messageReaderThread = messageReader();
                                socket = serverSocket.accept();
                                connectionJLabel.setText(userName + " is in " + "Online");
                                messageReaderThread.start();
                                usersButtonList.get(index).setBackground(Color.green);
                            } else {
                                if (messageReaderThread.isAlive()) {
                                    messageReaderThread.interrupt();
                                    messageReaderThread.stop();
                                }
                                connectionJLabel.setText("Searching for " + userName + " connection...");
                                socket = new Socket(ipAddres, myPortNumber);
                                messageReaderThread = messageReader();
                                messageReaderThread.start();
                                connectionJLabel.setText(userName + " is in " + "Online");
                                usersButtonList.get(index).setBackground(Color.green);
                            }
                        } catch (Exception ex) {
                            System.out.println(ex);
                            serverFlag = !serverFlag;
                            interrupt();
                            usersButtonList.get(index).setBackground(Color.LIGHT_GRAY);
                            run();
                        }
                    }

                    public void interrupt() {
                        try {
                            if (socket != null) {
                                socket.close();
                                dInput.close();
                                dOutput.close();
                            }
                            if (serverSocket != null)
                                serverSocket.close();
                            socket = null;
                            serverSocket = null;

                            if (messageReaderThread.isAlive()) {
                                messageReaderThread.interrupt();
                                messageReaderThread.stop();
                            }
                            usersButtonList.get(index).setBackground(Color.LIGHT_GRAY);
                            connectionJLabel.setText("( Offline or Disconnected ) Try again...");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                };
            };

            void messageSender() {
                try {
                    dOutput.writeUTF(textArea.getText());
                    dOutput.flush();
                    senderPanel = new JPanel();
                    // Border blackline = BorderFactory.createLineBorder(Color.BLACK);
                    AbstractBorder border = new TextBubbleBorder(Color.BLACK, 2, 16, 0);
                    senderTextArea = new JTextArea(textArea.getText());
                    senderTextArea.setEditable(false);
                    senderTextArea.setLineWrap(true);
                    senderTextArea.setVisible(true);
                    senderTextArea.setAlignmentY(0);
                    senderTextArea.setSize(500, 700);
                    senderPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                    senderPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                    senderTextArea.setBorder(border);
                    senderPanel.add(senderTextArea, BorderLayout.CENTER);
                    chatContainer.add(senderPanel);
                    chatContainer.revalidate();
                    chatContainer.repaint();
                    textArea.setText("");
                } catch (Exception e) {
                    System.out.println(e);
                }

            };

            Thread messageReader() {
                return new Thread() {
                    public void run() {
                        try {
                            dInput = new DataInputStream(socket.getInputStream());
                            dOutput = new DataOutputStream(socket.getOutputStream());
                            while (!disconnectFlag) {
                                message = dInput.readUTF();
                                reciverPanel = new JPanel();
                                reciverTextArea = new JTextArea();
                                // Border blackline = BorderFactory.createLineBorder(Color.black);
                                AbstractBorder border = new TextBubbleBorder(Color.BLACK, 2, 16, 0);
                                reciverTextArea.setText(message);
                                reciverTextArea.setEditable(false);
                                reciverTextArea.setLineWrap(true);
                                reciverTextArea.setVisible(true);
                                // reciverTextArea.setBackground(new Color(66, 152, 95));
                                reciverTextArea.setAlignmentY(0);
                                reciverTextArea.setSize(500, 700);
                                reciverPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                                reciverPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
                                reciverTextArea.setBorder(border);
                                reciverPanel.add(reciverTextArea, BorderLayout.CENTER);
                                chatContainer.add(reciverPanel);
                                chatContainer.revalidate();
                                chatContainer.repaint();
                            }
                            dInput.close();
                            dOutput.close();
                            socket.close();
                        } catch (Exception e) {
                            System.out.println(e);
                            connectionThread.interrupt();
                            connectionThread.stop();
                        }
                    }

                    public void interrupt() {
                        try {
                            if (socket != null) {
                                socket.close();
                                dInput.close();
                                dOutput.close();
                            }
                            if (serverSocket != null)
                                serverSocket.close();
                            socket = null;
                            serverSocket = null;
                            connectionJLabel.setText("Disconnected...");
                        } catch (Exception e) {
                            System.out.println(e);
                        }
                    }
                };
            };
        };

        AbstractBorder border1 = new TextBubbleBorder(Color.BLACK, 2, 20, 0);
        JButton jButton = new JButton(" Send ");
        jButton.setBorder(border1);
        jButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (textArea.getText() != "" && textArea.getText() != " ")
                    connectionInterface.sendMessage();

            }
        });
        bottomBar.add(scroll);
        bottomBar.add(jButton);

        connectionJLabel.setText(userName + " /port:" + portID);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        chatBoxJPanel.setPreferredSize(new Dimension((int) (screenSize.width / 1.3), (int) (screenSize.height / 1.2)));
        chatContainer.setLayout(new BoxLayout(chatContainer, BoxLayout.Y_AXIS));
        scrollPane.setViewportView(chatContainer);
        chatBoxJPanel.setLayout(new BorderLayout());
        conectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectionInterface.connect();
            }
        });

        disconectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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

class TextBubbleBorder extends AbstractBorder {

    private Color color;
    private int thickness = 4;
    private int radii = 8;
    private int pointerSize = 7;
    private Insets insets = null;
    private BasicStroke stroke = null;
    private int strokePad;
    private int pointerPad = 4;
    RenderingHints hints;

    TextBubbleBorder(
            Color color) {
        new TextBubbleBorder(color, 4, 8, 7);
    }

    TextBubbleBorder(Color color, int thickness, int radii, int pointerSize) {
        this.thickness = thickness;
        this.radii = radii;
        this.pointerSize = pointerSize;
        this.color = color;

        stroke = new BasicStroke(thickness);
        strokePad = thickness / 2;

        hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int pad = radii + strokePad;
        int bottomPad = pad + pointerSize + strokePad;
        insets = new Insets(pad, pad, bottomPad, pad);
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        return getBorderInsets(c);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        Graphics2D g2 = (Graphics2D) g;

        int bottomLineY = height - thickness - pointerSize;

        RoundRectangle2D.Double bubble = new RoundRectangle2D.Double(0 + strokePad, 0 + strokePad, width - thickness,
                bottomLineY, radii, radii);

        Polygon pointer = new Polygon();

        // left point
        pointer.addPoint(strokePad + radii + pointerPad, bottomLineY);
        // right point
        pointer.addPoint(strokePad + radii + pointerPad + pointerSize, bottomLineY);
        // bottom point
        pointer.addPoint(strokePad + radii + pointerPad + (pointerSize / 2), height - strokePad);

        Area area = new Area(bubble);
        area.add(new Area(pointer));

        g2.setRenderingHints(hints);

        Area spareSpace = new Area(new Rectangle(0, 0, width, height));
        spareSpace.subtract(area);
        g2.setClip(spareSpace);
        g2.clearRect(0, 0, width, height);
        g2.setClip(null);

        g2.setColor(color);
        g2.setStroke(stroke);
        g2.draw(area);
    }
}
