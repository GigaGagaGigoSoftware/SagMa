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
	private LogInRequestPacket request;

	public StartFrame() {
		server.addActionListener(this);
		username.addActionListener(this);
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
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("SagMa");
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (ok.isEnabled()) {
			ok.setEnabled(false);
			SagMaClient client = new SagMaClient();
			client.setPacketHandler(p -> {
				if (p instanceof LogInReplyPacket) {
					LogInReplyPacket reply = (LogInReplyPacket) p;
					if (reply.success) {
						SwingUtilities.invokeLater(() -> dispose());
						new WindowManager(client, request.username);
					} else {
						SwingUtilities.invokeLater(() -> {
							JOptionPane.showMessageDialog(
								this,
								"Der Benutzername wird bereits verwendet.",
								"Fehler!",
								JOptionPane.ERROR_MESSAGE);
							ok.setEnabled(true);
						});
					}
				} else {
					SwingUtilities.invokeLater(() -> ok.setEnabled(true));
				}
			});
			client.start(server.getText());
			request = new LogInRequestPacket();
			request.username = username.getText();
			client.sendPacket(request);
		}
	}

}
