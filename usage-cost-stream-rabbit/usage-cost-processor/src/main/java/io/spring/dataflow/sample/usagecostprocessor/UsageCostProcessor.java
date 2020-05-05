package io.spring.dataflow.sample.usagecostprocessor;

import io.spring.dataflow.sample.domain.UsageCostDetail;
import io.spring.dataflow.sample.domain.UsageDetail;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

@EnableBinding(Processor.class)
public class UsageCostProcessor {

	private double ratePerSecond = 0.1;

	private double ratePerMB = 0.05;

	/**
	 * @StreamListener annotation binds the application's `input` channel to the
	 *                 `processUsageCost` method by converting the incoming JSON
	 *                 into `UsageDetail` object.
	 * 
	 * @SendTo annotation sends the `processUsageCost` method's output to the
	 *         application's `output` channel, which is, in turn, sent to the a
	 *         RabbitMQ message broker by using a `TopicExchange`.
	 * 
	 * @param usageDetail
	 * @return
	 */
	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public UsageCostDetail processUsageCost(UsageDetail usageDetail) {
		UsageCostDetail usageCostDetail = new UsageCostDetail();
		usageCostDetail.setUserId(usageDetail.getUserId());
		usageCostDetail.setCallCost(usageDetail.getDuration() * this.ratePerSecond);
		usageCostDetail.setDataCost(usageDetail.getData() * this.ratePerMB);
		return usageCostDetail;
	}
}
