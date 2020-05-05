package io.spring.dataflow.sample.usagedetailsender;

import java.util.Random;

import io.spring.dataflow.sample.domain.UsageDetail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
/**
 * @EnableBinding annotation indicates that bind your application to messaging
 *                middleware. The annotation takes one or more interfaces as a
 *                parameter — in this case, the Source interface that defines an
 *                output channel named output.
 */
@EnableBinding(Source.class)
public class UsageDetailSender {

	@Autowired
	private Source source;

	private String[] users = { "Glenn", "Sabby", "Mark", "Janne", "Ilaya" };

	/**
	 * @Scheduled with the specified fixedDelay of 1 second. The sendEvents method
	 *            constructs a UsageDetail object and then sends it to the the
	 *            output channel by accessing the Source object's output().send()
	 *            method.
	 */
	@Scheduled(fixedDelay = 1000)
	public void sendEvents() {
		UsageDetail usageDetail = new UsageDetail();
		usageDetail.setUserId(this.users[new Random().nextInt(5)]);
		usageDetail.setDuration(new Random().nextInt(300));
		usageDetail.setData(new Random().nextInt(700));
		this.source.output().send(MessageBuilder.withPayload(usageDetail).build());
	}
}
