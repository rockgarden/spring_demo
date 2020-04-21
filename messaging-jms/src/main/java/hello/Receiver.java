package hello;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

	/**
	 * Defines the name of the Destination that this method should listen to and the
	 * reference to the JmsListenerContainerFactory to use to create the underlying
	 * message listener container.
	 * 
	 * @param email
	 */
	@JmsListener(destination = "mailbox", containerFactory = "myFactory")
	public void receiveMessage(Email email) {
		System.out.println("Received <" + email + ">");
	}

}
