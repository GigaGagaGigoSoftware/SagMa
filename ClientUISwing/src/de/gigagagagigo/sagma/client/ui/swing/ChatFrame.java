package de.gigagagagigo.sagma.client.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.packets.ChatMessagePacket;

public class ChatFrame extends JFrame {

	private final WindowManager manager;
	private final String partner;

	private JTextPane chat = new JTextPane();
	private JTextField text = new JTextField();
	private JButton send = new JButton("Senden");

	public ChatFrame(WindowManager manager, String partner) {
		this.manager = manager;
		this.partner = partner;

		chat.setEditable(false);
		text.addActionListener(e -> sendMessage());
		send.addActionListener(e -> sendMessage());

		JPanel controls = new JPanel(new BorderLayout(5, 5));
		controls.add(text, BorderLayout.CENTER);
		controls.add(send, BorderLayout.EAST);

		JPanel content = new JPanel(new BorderLayout(5, 5));
		content.add(new JScrollPane(chat), BorderLayout.CENTER);
		content.add(controls, BorderLayout.SOUTH);

		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(content);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				manager.closeChatFrame(partner);
			}
		});

		setTitle(partner + " - SagMa [" + manager.getUsername() + "]");
		setSize(500, 700);
		setMinimumSize(new Dimension(200, 300));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void sendMessage() {
		String messageText = text.getText();
		text.setText("");

		ChatMessagePacket message = new ChatMessagePacket();
		message.username = partner;
		message.message = messageText;
		manager.sendPacket(message);

		appendMessage(manager.getUsername(), messageText);
	}

	public void handleChatMessage(ChatMessagePacket message) {
		appendMessage(message.username, message.message);
	}

	private void appendMessage(String author, String message) {
		String text = chat.getText();
		text += author + ": " + message + "\n";
		chat.setText(text);
	}

}
