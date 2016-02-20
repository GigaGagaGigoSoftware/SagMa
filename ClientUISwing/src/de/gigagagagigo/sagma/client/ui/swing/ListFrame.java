package de.gigagagagigo.sagma.client.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.packets.UserListReplyPacket;
import de.gigagagagigo.sagma.packets.UserListRequestPacket;

public class ListFrame extends JFrame {

	private final WindowManager manager;

	private JList<String> list;
	private JButton update;

	public ListFrame(WindowManager manager) {
		this.manager = manager;
		list = new JList<>();

		update = new JButton("Aktualisieren");
		update.addActionListener(e -> update());

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
		content.add(list, BorderLayout.CENTER);
		content.add(update, BorderLayout.SOUTH);
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

		update();
	}

	private void update() {
		update.setEnabled(false);
		manager.sendPacket(new UserListRequestPacket());
	}

	public void handleUserListReply(UserListReplyPacket reply) {
		DefaultListModel<String> model = new DefaultListModel<>();
		for (String user : reply.users)
			model.addElement(user);
		list.setModel(model);
		update.setEnabled(true);
	}

}
