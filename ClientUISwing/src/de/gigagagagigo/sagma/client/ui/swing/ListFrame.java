package de.gigagagagigo.sagma.client.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.UserListReplyPacket;
import de.gigagagagigo.sagma.packets.UserListRequestPacket;

public class ListFrame extends JFrame {

	private final SagMaClient client;

	private JList<String> list;
	private JButton update;

	public ListFrame(SagMaClient client) {
		this.client = client;
		client.setPacketHandler(this::handlePacket);
		list = new JList<>();
		update = new JButton("Aktualisieren");
		update.addActionListener(e -> update());

		JPanel content = new JPanel(new BorderLayout(5, 5));
		content.setBorder(new EmptyBorder(5, 5, 5, 5));
		content.add(list, BorderLayout.CENTER);
		content.add(update, BorderLayout.SOUTH);
		setContentPane(content);

		setTitle("SagMa");
		setSize(300, 500);
		setMinimumSize(new Dimension(200, 300));
		setLocationRelativeTo(null);
		setVisible(true);

		update();
	}

	private void update() {
		update.setEnabled(false);
		client.sendPacket(new UserListRequestPacket());
	}

	private void handlePacket(Packet packet) {
		if (packet instanceof UserListReplyPacket) {
			UserListReplyPacket reply = (UserListReplyPacket) packet;
			SwingUtilities.invokeLater(() -> {
				DefaultListModel<String> model = new DefaultListModel<>();
				for (String user : reply.users)
					model.addElement(user);
				list.setModel(model);
				update.setEnabled(true);
			});
		}
	}

}
