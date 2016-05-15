package de.gigagagagigo.sagma.client.ui.swing;

import static javax.swing.GroupLayout.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.client.Handler;
import de.gigagagagigo.sagma.client.SagMaClient;
import de.gigagagagigo.sagma.packet.Packet;
import de.gigagagagigo.sagma.packets.AuthReplyPacket;
import de.gigagagagigo.sagma.packets.AuthRequestPacket;

public class StartFrame extends JFrame implements Handler {

	private JTextField server = new JTextField(20);
	private JTextField username = new JTextField(20);
	private JPasswordField password = new JPasswordField(20);
	private JButton logIn = new JButton("Einloggen");
	private JButton register = new JButton("Registrieren");

	private SagMaClient client;
	private AuthRequestPacket request;

	public StartFrame() {
		server.addActionListener(e -> username.requestFocus());
		username.addActionListener(e -> password.requestFocus());
		password.addActionListener(e -> authenticate(false));
		logIn.addActionListener(e -> authenticate(false));
		register.addActionListener(e -> authenticate(true));

		JLabel serverLabel = new JLabel("Server:");
		JLabel usernameLabel = new JLabel("Benutzername:");
		JLabel passwordLabel = new JLabel("Passwort:");

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
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(passwordLabel)
				.addComponent(password))
			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
				.addComponent(logIn)
				.addComponent(register)));
		layout.setHorizontalGroup(layout.createParallelGroup()
			.addGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
					.addComponent(serverLabel, Alignment.TRAILING)
					.addComponent(usernameLabel, Alignment.TRAILING)
					.addComponent(passwordLabel, Alignment.TRAILING))
				.addGroup(layout.createParallelGroup()
					.addComponent(server)
					.addComponent(username)
					.addComponent(password)))
			.addGroup(layout.createSequentialGroup()
				.addComponent(logIn, PREFERRED_SIZE, PREFERRED_SIZE, Integer.MAX_VALUE)
				.addComponent(register, PREFERRED_SIZE, PREFERRED_SIZE, Integer.MAX_VALUE)));

		pack();
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("SagMa");
		setVisible(true);
	}

	private void authenticate(boolean doRegister) {
		if (logIn.isEnabled()) {
			logIn.setEnabled(false);
			register.setEnabled(false);
			client = new SagMaClient(SwingUtilities::invokeLater);
			client.setHandler(this);
			client.start(server.getText());
			request = new AuthRequestPacket();
			request.username = username.getText();
			request.password = new String(password.getPassword());
			request.register = doRegister;
			client.sendPacket(request);
		}
	}

	@Override
	public void handlePacket(Packet packet) {
		AuthReplyPacket reply = (AuthReplyPacket) packet;
		if (reply.success) {
			dispose();
			new WindowManager(client, request.username);
		} else {
			JOptionPane.showMessageDialog(
				this,
				"Der Server hat die Anmeldedaten nicht akzeptiert.",
				"Warnung!",
				JOptionPane.WARNING_MESSAGE);
			logIn.setEnabled(true);
			register.setEnabled(true);
		}
	}

	@Override
	public void handleException(Exception exception) {
		JOptionPane.showMessageDialog(
			this,
			"Bei der Verbindungsherstellung ist ein Fehler aufgetreten.",
			"Fehler!",
			JOptionPane.ERROR_MESSAGE);
		logIn.setEnabled(true);
		register.setEnabled(true);
	}

}
