
package ServerBox;

import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatUser extends JFrame {
    private static final long serialVersionUID = 1L;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton enterButton;
    private String username, password;

    public ChatUser() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        enterButton = new JButton("Enter");
    	
        // Prompt the user to enter their username
        username = JOptionPane.showInputDialog(this, "Enter your username:", "Username", JOptionPane.PLAIN_MESSAGE);
 
        if(username.equalsIgnoreCase("Admin") || username.equalsIgnoreCase("Administrator")){
        	password = JOptionPane.showInputDialog(this, "Enter your password:", "Password", JOptionPane.WARNING_MESSAGE);
        	if(password.equals("Password")) {
        		username = "Admin";
        	}
        	else {
        		username = "Guest";  
        		messageField.setText("Admin login failed");
        	}
        }
        if (username == null || username.isEmpty()) {
            username = "Anonymous";
        }

        // Set up the GUI
        setTitle("Chat Client - " + username);
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        chatArea = new JTextArea("");
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        // ActionListener for sending messages via button or Enter key
        ActionListener sendMessageAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.trim().isEmpty()) {
                    writer.println(username + ": " + message);
                    chatArea.append("Me: " + message + "\n");
                    messageField.setText("");
                }
            }
        };
        messageField.addActionListener(sendMessageAction);
        enterButton.addActionListener(sendMessageAction);

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(enterButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);

        try {
            socket = new Socket("localhost", 5000);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Listen for messages from the server
            while (true) {
                String message = reader.readLine();
                if (message != null) {
                    if (message.equals("clear_chat") || message.equals("/purge")) {
                        chatArea.setText(""); // Clear chat if server triggers the "clear" command
                    } else {
                        chatArea.append(message + "\n");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ChatUser();
    }
}