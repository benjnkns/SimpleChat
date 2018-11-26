
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class GUI {

	ClientConsole CC;
    String      appName     = "Colt Chat v0.1";
    GUI     	mainGUI;
    JFrame      newFrame    = new JFrame(appName);
    JButton     sendMessage;
    JTextField  messageBox;
    JTextArea   chatBox;
    JTextField  usernameChooser;
    JTextField 	passwordChooser;
    JTextField	hostChooser;
    JTextField	portChooser;
    JFrame      preFrame;
    JComboBox<String> commands;
    JRadioButton status;
    
    public GUI(){
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager
                            .getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                GUI mainGUI = new GUI();
                mainGUI.preDisplay();
            }
        });
    }

    
    public void preDisplay() {
        newFrame.setVisible(false);
        preFrame = new JFrame(appName);
        usernameChooser = new JTextField(15);
        passwordChooser = new JTextField(15);
        hostChooser = new JTextField( "localhost");
        portChooser = new JTextField("5555");
        JLabel chooseUsernameLabel = new JLabel("Username:");
        JLabel choosePasswordLabel = new JLabel("Password:");
        JLabel chooseHostLabel = new JLabel("Host:");
        JLabel choosePortLabel = new JLabel("Port:");
        JButton enterServer = new JButton("Enter Chat Server");
        enterServer.addActionListener(new enterServerButtonListener());
        JPanel prePanel = new JPanel(new GridBagLayout());

        GridBagConstraints preRight = new GridBagConstraints();
        preRight.insets = new Insets(0, 0, 0, 10);
        preRight.anchor = GridBagConstraints.EAST;
        GridBagConstraints preLeft = new GridBagConstraints();
        preLeft.anchor = GridBagConstraints.WEST;
        preLeft.insets = new Insets(0, 10, 0, 10);
        // preRight.weightx = 2.0;
        preRight.fill = GridBagConstraints.HORIZONTAL;
        preRight.gridwidth = GridBagConstraints.REMAINDER;

        prePanel.add(chooseUsernameLabel, preLeft);
        prePanel.add(usernameChooser, preRight);
        prePanel.add(choosePasswordLabel, preLeft);
        prePanel.add(passwordChooser, preRight);
        prePanel.add(chooseHostLabel, preLeft);
        prePanel.add(hostChooser, preRight);
        prePanel.add(choosePortLabel, preLeft);
        prePanel.add(portChooser, preRight);
        preFrame.add(BorderLayout.CENTER, prePanel);
        preFrame.add(BorderLayout.SOUTH, enterServer);
        preFrame.setSize(300, 300);
        preFrame.setVisible(true);

    }

    public void display() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        southPanel.setBackground(Color.BLUE);
        southPanel.setLayout(new GridBagLayout());

        messageBox = new JTextField(30);
        messageBox.requestFocusInWindow();

        sendMessage = new JButton("Send Message");
        sendMessage.addActionListener(new sendMessageButtonListener());

        chatBox = new JTextArea();
        chatBox.setEditable(false);
        chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
        chatBox.setLineWrap(true);
        
        String[] commandList = { "Private Message", "Quit","Logoff", "Set Host", "Get Host","Set Port", "Get Port", "Login", "Block", "Unblock", "Who I Block", "Who Blocks Me", "Status", "Start Forwarding", "Cancel Forwarding", "Create Channel", "Join Channel", "Leave Channel"};
      //  String[] statusList = {"Available", "Unavailable"};
        commands = new JComboBox<String>(commandList);
        commands.addActionListener(new commandListener());
        commands.setVisible(true);
        mainPanel.add(commands, BorderLayout.LINE_END);
        mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);
        
        status = new JRadioButton("Available", true);
        status.setForeground(Color.ORANGE);
        status.addActionListener(new statusListener());
        status.setVisible(true);
        //mainPanel.add(usersgroups, BorderLayout.LINE_END);
        //mainPanel.add(new JScrollPane(chatBox), BorderLayout.CENTER);

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.CENTER;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;
        
        GridBagConstraints righter = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;

        southPanel.add(messageBox, left);
        southPanel.add(sendMessage, right);
        southPanel.add(status, righter);

        mainPanel.add(BorderLayout.SOUTH, southPanel);

        newFrame.add(mainPanel);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setSize(470, 300);
        newFrame.setVisible(true);
        
    }

//    ActionListener taskPerformer = new ActionListener() {
        public void updateChatBox(String message) {
            chatBox.append(message + "\n");
        }
//    };
    
    class sendMessageButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            if (messageBox.getText().length() < 1) {
                // do nothing
            } else if (messageBox.getText().equals(".clear")) {
                chatBox.setText("Cleared all messages\n");
                messageBox.setText("");
            } else {
//                chatBox.append("<" + username + ">:  " + messageBox.getText()
//                        + "\n");
//            	chatBox.append(messageBox.getText());
            	CC.handleMessage(messageBox.getText());
            	
                messageBox.setText("");
          
            }
            messageBox.requestFocusInWindow();
        }
    }
    class statusListener implements ActionListener{
    	public void actionPerformed(ActionEvent event){
    		if (status.isSelected()){
    			CC.handleMessage("#available");
    		}
    		else{ CC.handleMessage("#unavailable");}
    	}
    }
    class commandListener implements ActionListener{
    	public void actionPerformed(ActionEvent event){
    		messageBox.setText("");
    		String command = (String) commands.getSelectedItem();
    		switch(command){
    		
    		case "Private Message": command = "#private"; break;
    		case "Quit": command = "#quit"; CC.handleMessage("#quit"); return;
    		case "Logoff": command = "#logoff"; CC.handleMessage("#logoff"); return;
    		case "Set Host": command = "#sethost"; break;
    		case "Get Host": command = "#gethost"; CC.handleMessage("#gethost"); return;
    		case "Set Port": command = "#setport"; break;
    		case "Get Port": command = "#getport"; CC.handleMessage("#getport"); return;
    		case "Login": command = "#login"; break;
    		case "Block": command = "#block"; break;
    		case "Unblock": command = "#unblock"; break;
    		case "Who I Block": command = "#whoiblock"; CC.handleMessage("#whoiblock"); return;
    		case "Who Blocks Me": command = "#whoblocksme"; CC.handleMessage("#whoblocksme"); return;
    		case "Status": command = "#status"; break;
    		case "Start Forwarding": command = "#startforwarding"; break;
    		case "Cancel Forwarding": command = "#cancelforwarding"; break;
    		case "Create Channel": command = "#createchannel"; break;
    		case "Join Channel": command = "#channel"; break;
    		case "Leave Channel": command = "#leave"; break;
    		}		
					
    		messageBox.setText(command);
    	}
    }

    String  username, password, host, port;

    class enterServerButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            username = usernameChooser.getText();
            password = passwordChooser.getText();
            host = hostChooser.getText();
            port = portChooser.getText();
            if (username.length() < 1 || password.length() < 1 || host.length() < 1 || port.length() < 1) {
                System.out.println("A field is not valid");
            } else {
                preFrame.setVisible(false);
                display();
                CC = new ClientConsole(username, host, password, Integer.parseInt(port), GUI.this);
            }
        }

    }
}

//Some code taken and modified from http://codereview.stackexchange.com/questions/25461/simple-chat-room-swing-gui