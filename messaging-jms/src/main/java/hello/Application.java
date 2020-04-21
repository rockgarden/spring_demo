
package hello;

import javax.jms.ConnectionFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

/**
 * @EnableJms triggers the discovery of methods annotated with @JmsListener,
 *            creating the message listener container under the covers.
 */
@SpringBootApplication
@EnableJms
public class Application {

	/**
	 * This provides all boot's default to this factory, including the message
	 * converter.
	 * 
	 * defined a myFactory bean that is referenced in the JmsListener annotation of
	 * the receiver. Because we use the DefaultJmsListenerContainerFactoryConfigurer
	 * infrastructure provided by Spring Boot, that JmsMessageListenerContainer will
	 * be identical to the one that boot creates by default.
	 * 
	 * You could still override some of Boot's default if necessary.
	 * 
	 * @param connectionFactory
	 * @param configurer
	 * @return
	 */
	@Bean
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory,
			DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		configurer.configure(factory, connectionFactory);
		return factory;
	}

	/**
	 * Serialize message content to json using TextMessage.
	 * 
	 * Spring Boot will detect the presence of a MessageConverter and will associate
	 * it to both the default JmsTemplate and any JmsListenerContainerFactory
	 * created by DefaultJmsListenerContainerFactoryConfigurer.
	 * 
	 * @return
	 */
	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}

	public static void main(String[] args) {
		// Launch the application
		ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
		JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
		// Send a message with a POJO - the template reuse the message converter
		System.out.println("Sending an email message.");
		jmsTemplate.convertAndSend("mailbox", new Email("info@example.com", "Hello"));
	}

}
