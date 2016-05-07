package de.gigagagagigo.sagma.client.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.packets.*;

public class GroupFrame extends JFrame {

	private final WindowManager manager;
	private final String group;

	private DefaultListModel<String> membersModel = new DefaultListModel<>();
	private JList<String> members = new JList<>(membersModel);

	private JTextPane chat = new JTextPane();
	private JTextField text = new JTextField();
	private JButton send = new JButton("Senden");

	public GroupFrame(WindowManager manager, String group) {
		this.manager = manager;
		this.group = group;

		members.setPreferredSize(new Dimension(200, 200));

		chat.setEditable(false);
		text.addActionListener(e -> sendMessage());
		send.addActionListener(e -> sendMessage());

		JPanel controls = new JPanel(new BorderLayout(5, 5));
		controls.add(text, BorderLayout.CENTER);
		controls.add(send, BorderLayout.EAST);

		JPanel chatPanel = new JPanel(new BorderLayout(5, 5));
		chatPanel.add(new JScrollPane(chat), BorderLayout.CENTER);
		chatPanel.add(controls, BorderLayout.SOUTH);

		JPanel content = new JPanel(new BorderLayout(5, 5));
		content.add(chatPanel, BorderLayout.CENTER);
		content.add(new JScrollPane(members), BorderLayout.EAST);


		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(content);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				manager.closeGroupFrame(group);
			}
		});

		setTitle(group + " - SagMa [" + manager.getUsername() + "]");
		setSize(700, 700);
		setMinimumSize(new Dimension(400, 300));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void sendMessage() {
		String messageText = text.getText();
		text.setText("");

		SendMessagePacket message = new SendMessagePacket();
		message.entityName = group;
		message.isGroup = true;
		message.content = messageText;
		manager.sendPacket(message);

		appendMessage(manager.getUsername(), messageText);
	}

	public void handleChatMessage(MessagePacket message) {
		appendMessage(message.userName, message.content);
	}

	private void appendMessage(String author, String message) {
		String text = chat.getText();
		text += author + ": " + message + "\n";
		chat.setText(text);
	}

	public void handleMemberListUpdate(MemberListUpdatePacket update) {
		if (update.removed != null)
			for (String user : update.removed)
				membersModel.removeElement(user);
		if (update.added != null)
			for (String user : update.added)
				membersModel.addElement(user);
	}

}
