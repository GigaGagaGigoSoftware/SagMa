package de.gigagagagigo.sagma.client.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import de.gigagagagigo.sagma.client.SagMaClient;

public class ListFrame extends JFrame {

	private final SagMaClient client;

	private JList<String> list;
	private JButton update;

	public ListFrame(SagMaClient client) {
		this.client = client;
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
		new SwingWorker<String[], Void>() {
			@Override
			protected String[] doInBackground() throws Exception {
				return client.getUserList();
			}

			@Override
			protected void done() {
				try {
					DefaultListModel<String> model = new DefaultListModel<>();
					String[] users = get();
					for (String user : users) {
						model.addElement(user);
					}
					list.setModel(model);
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				} finally {
					update.setEnabled(true);
				}
			};

		}.execute();
	}

}
