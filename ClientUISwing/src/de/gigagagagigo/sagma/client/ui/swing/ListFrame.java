package de.gigagagagigo.sagma.client.ui.swing;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.packets.GroupListUpdatePacket;
import de.gigagagagigo.sagma.packets.UserListUpdatePacket;

public class ListFrame extends JFrame {

	private final DefaultListModel<String> userModel;
	private final DefaultListModel<String> groupModel;
	private final JList<String> users;
	private final JList<String> groups;
	private final JTextField groupName;
	private final JButton createGroup;

	public ListFrame(WindowManager manager) {
		userModel = new DefaultListModel<>();
		groupModel = new DefaultListModel<>();
		users = new JList<>(userModel);
		groups = new JList<>(groupModel);

		users.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !users.getSelectionModel().isSelectionEmpty()) {
					manager.showChatFrame(users.getSelectedValue());
				}
			}
		});

		groups.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !groups.getSelectionModel().isSelectionEmpty()) {
					manager.showGroupFrame(groups.getSelectedValue());
				}
			}
		});

		groupName = new JTextField();
		createGroup = new JButton("Gruppe erstellen");
		createGroup.addActionListener(e -> {
			if (groupName.getText().trim().isEmpty()) {
				JOptionPane.showMessageDialog(
					ListFrame.this,
					"Bitte Gruppenname eingeben.",
					"Achtung!",
					JOptionPane.WARNING_MESSAGE);
			} else {
				manager.showGroupFrame(groupName.getText().trim());
				groupName.setText("");
			}
		});

		JPanel userPanel = new JPanel(new BorderLayout(5, 5));
		userPanel.add(new JLabel("Benutzer"), BorderLayout.NORTH);
		userPanel.add(new JScrollPane(users), BorderLayout.CENTER);

		JPanel groupPanel = new JPanel(new BorderLayout(5, 5));
		groupPanel.add(new JLabel("Gruppen"), BorderLayout.NORTH);
		groupPanel.add(new JScrollPane(groups), BorderLayout.CENTER);

		JPanel listPanel = new JPanel(new GridLayout(2, 1, 5, 5));
		listPanel.add(userPanel);
		listPanel.add(groupPanel);

		JPanel createPanel = new JPanel(new BorderLayout(5, 5));
		createPanel.add(groupName, BorderLayout.CENTER);
		createPanel.add(createGroup, BorderLayout.EAST);

		JPanel content = new JPanel(new BorderLayout(5, 5));
		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		content.add(listPanel, BorderLayout.CENTER);
		content.add(createPanel, BorderLayout.SOUTH);
		setContentPane(content);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dispose();
				manager.closeListFrame();
			}
		});

		setTitle("SagMa [" + manager.getUsername() + "]");
		setSize(300, 500);
		setMinimumSize(new Dimension(200, 300));
		setLocationRelativeTo(null);
		setVisible(true);
	}

	public void handleUserListUpdate(UserListUpdatePacket update) {
		if (update.removed != null)
			for (String user : update.removed)
				userModel.removeElement(user);
		if (update.added != null)
			for (String user : update.added)
				userModel.addElement(user);
	}

	public void handleGroupListUpdate(GroupListUpdatePacket update) {
		if (update.removed != null)
			for (String group : update.removed)
				groupModel.removeElement(group);
		if (update.added != null)
			for (String group : update.added)
				groupModel.addElement(group);
	}

}
