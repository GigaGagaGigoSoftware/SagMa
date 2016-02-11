package de.gigagagagigo.sagma.client.ui.swing;

import static javax.swing.GroupLayout.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.packets.LogInReplyPacket;
import de.gigagagagigo.sagma.packets.LogInRequestPacket;

public class StartFrame extends JFrame implements ActionListener {

	private JTextField server = new JTextField(20);
	private JTextField username = new JTextField(20);
	private JButton ok = new JButton("OK");

	public StartFrame() {
		ok.addActionListener(this);

		JLabel serverLabel = new JLabel("Server:");
		JLabel usernameLabel = new JLabel("Benutzername:");

		JPanel content = new JPanel();
		setContentPane(content);
		GroupLayout layout = new GroupLayout(content);
		layout.setAutoCreateGaps(true);
		content.setLayout(layout);
		content.setBorder(new EmptyBorder(5, 5, 5, 5));

		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(serverLabel)
				.addComponent(server))
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(usernameLabel)
				.addComponent(username))
			.addComponent(ok));
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(serverLabel, Alignment.TRAILING)
					.addComponent(usernameLabel, Alignment.TRAILING))
				.addGroup(layout.createParallelGroup()
					.addComponent(server)
					.addComponent(username)))
			.addComponent(ok, PREFERRED_SIZE, PREFERRED_SIZE, Integer.MAX_VALUE));

		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("SagMa");
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ok.setEnabled(false);
		SagMaClient client = new SagMaClient();
		client.setPacketHandler(p -> {
			if (p instanceof LogInReplyPacket) {
				LogInReplyPacket reply = (LogInReplyPacket) p;
				if (reply.success) {
					SwingUtilities.invokeLater(() -> {
						setVisible(false);
						dispose();
						new ListFrame(client);
					});
				} else {
					SwingUtilities.invokeLater(() -> ok.setEnabled(true));
				}
			} else {
				SwingUtilities.invokeLater(() -> ok.setEnabled(true));
			}
		});
		client.start(server.getText());
		LogInRequestPacket request = new LogInRequestPacket();
		request.username = username.getText();
		client.sendPacket(request);
	}

}
