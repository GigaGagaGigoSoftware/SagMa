package de.gigagagagigo.sagma.server.ui.swing;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetAddress;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.server.SagMaServer;

public class ServerFrame extends JFrame {

	private SagMaServer server;
	private JButton stop;

	public ServerFrame() {
		try {
			server = new SagMaServer();
			server.start();

			JLabel ip = new JLabel(InetAddress.getLocalHost().getHostAddress());
			ip.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 42));
			ip.setHorizontalAlignment(SwingConstants.CENTER);

			stop = new JButton("Stop");
			stop.addActionListener(e -> stop());

			JPanel content = new JPanel(new BorderLayout(5, 5));
			content.setBorder(new EmptyBorder(5, 5, 5, 5));
			content.add(ip, BorderLayout.CENTER);
			content.add(stop, BorderLayout.SOUTH);
			setContentPane(content);

			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					int answer = JOptionPane.showConfirmDialog(
						ServerFrame.this,
						"Jetzt den Server stoppen und das Programm beenden?",
						"Beenden?",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
					if (answer == JOptionPane.YES_OPTION)
						stop();
				}
			});

			setSize(500, 200);
			setMinimumSize(getSize());
			setLocationRelativeTo(null);
			setTitle("SagMa Server");
			setVisible(true);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
				null,
				"Der Server konnte nicht gestartet werden.",
				"Fehler!",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	private void stop() {
		try {
			server.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
		dispose();
	}

}
