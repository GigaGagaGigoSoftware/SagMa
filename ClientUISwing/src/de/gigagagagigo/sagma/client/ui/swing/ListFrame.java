package de.gigagagagigo.sagma.client.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.packets.UserListUpdatePacket;

public class ListFrame extends JFrame {

	private final DefaultListModel<String> model;
	private final JList<String> list;

	public ListFrame(WindowManager manager) {
		model = new DefaultListModel<>();
		list = new JList<>(model);

		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && !list.getSelectionModel().isSelectionEmpty()) {
					manager.showChatFrame(list.getSelectedValue());
				}
			}
		});

		JPanel content = new JPanel(new BorderLayout(5, 5));
		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		content.add(new JScrollPane(list), BorderLayout.CENTER);
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
				model.removeElement(user);
		if (update.added != null)
			for (String user : update.added)
				model.addElement(user);
	}

}
