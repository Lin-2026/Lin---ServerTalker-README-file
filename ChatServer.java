package ServerBox;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatServer extends JFrame {
    private static final long serialVersionUID = 1L;
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton enterButton, clearChatButton, showHistory;
    private ArrayList<String> chatHistory;
    
    public ChatServer() {
        // Set up the GUI
        setTitle("Chat Server");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        enterButton = new JButton("Enter");
        clearChatButton = new JButton("Clear Chat");

        // ActionListener for sending messages via button or Enter key
        ActionListener sendMessageAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                if (!message.trim().isEmpty()) {
                    writer.println("Server: " + message);
                    chatArea.append("Me: " + message + "\n");
                    messageField.setText("");
                }
            }
        };
        messageField.addActionListener(sendMessageAction);
        enterButton.addActionListener(sendMessageAction);

        // ActionListener for clearing the chat
        clearChatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show confirmation dialog
                int result = JOptionPane.showConfirmDialog(ChatServer.this,
                        "Are you sure you want to clear the chat history?",
                        "Clear Chat Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION) {
                    // Clear chat for server and notify client to clear its chat
                    chatArea.setText("");
                    writer.println("clear_chat"); // Special message to clear chat on client side
                }
            }
        });

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(enterButton, BorderLayout.EAST);
        inputPanel.add(clearChatButton, BorderLayout.WEST); // Add the Clear Chat button to the left
        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
        
        chatHistory = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(5000);
            chatArea.append("Server started. Waiting for clients...\n");
            socket = serverSocket.accept();
            chatArea.append("Client connected!\n");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            // Listen for messages from the client
            while (true) {
                String message = reader.readLine();
                if (message != null) {
                    if (message.equals("clear_chat") || message.equals("/purge")) {
                        chatArea.setText(""); // Clear chat if client triggers the "clear" command
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
        new ChatServer();
    }
}
