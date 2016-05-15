package de.gigagagagigo.sagma.client;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.BiConsumer;

import de.gigagagagigo.sagma.packet.Packet;

class HandlerRef {

	private static class Message<T> {
		private final T object;
		private final BiConsumer<Handler, T> applyFunction;

		public Message(T object, BiConsumer<Handler, T> applyFunction) {
			this.object = object;
			this.applyFunction = applyFunction;
		}

		public void apply(Handler handler) {
			applyFunction.accept(handler, object);
		}
	}

	private final Queue<Message<?>> outstanding = new LinkedList<>();
	private final RunnableExecutor executor;
	private Handler handler = null;

	public HandlerRef(RunnableExecutor executor) {
		this.executor = executor;
	}

	public synchronized void submitPacket(Packet packet) {
		if (handler != null)
			executor.execute(() -> handler.handlePacket(packet));
		else
			outstanding.add(new Message<>(packet, Handler::handlePacket));
	}

	public synchronized void submitException(Exception exception) {
		if (handler != null)
			executor.execute(() -> handler.handleException(exception));
		else
			outstanding.add(new Message<>(exception, Handler::handleException));
	}

	public synchronized void setHander(Handler handler) {
		this.handler = handler;
		for (Message<?> m : outstanding)
			executor.execute(() -> m.apply(handler));
		outstanding.clear();
	}

}
