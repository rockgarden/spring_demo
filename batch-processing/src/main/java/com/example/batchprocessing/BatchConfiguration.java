package com.example.batchprocessing;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
// import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
// import org.springframework.batch.item.file.mapping.DefaultLineMapper;
// import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
// import org.springframework.jdbc.core.JdbcTemplate;

// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	// end::setup[]

	// tag::readerwriterprocessor[]
	/**
	 * Creates an ItemReader. It looks for a file called sample-data.csv and parses
	 * each line item with enough information to turn it into a Person.
	 * 
	 * @return
	 */
	@Bean
	public FlatFileItemReader<Person> reader() {
		return new FlatFileItemReaderBuilder<Person>().name("personItemReader")
				.resource(new ClassPathResource("sample-data.csv")).delimited()
				.names(new String[] { "firstName", "lastName" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Person>() {
					{
						setTargetType(Person.class);
					}
				}).build();
	}

	/**
	 * Creates an instance of the PersonItemProcessor.
	 * 
	 * @return
	 */
	@Bean
	public PersonItemProcessor processor() {
		return new PersonItemProcessor();
	}

	/**
	 * Creates an ItemWriter. This one is aimed at a JDBC destination and
	 * automatically gets a copy of the dataSource created
	 * by @EnableBatchProcessing. It includes the SQL statement needed to insert a
	 * single Person, driven by Java bean properties.
	 * 
	 * @param dataSource
	 * @return
	 */
	@Bean
	public JdbcBatchItemWriter<Person> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Person>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO people (first_name, last_name) VALUES (:firstName, :lastName)").dataSource(dataSource)
				.build();
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	/**
	 * Defines the job, Jobs are built from steps.
	 * 
	 * In this job definition, you need an incrementer, because jobs use a database
	 * to maintain execution state. You then list each step, (though this job has
	 * only one step). The job ends, and the Java API produces a perfectly
	 * configured job.
	 * 
	 * @param listener
	 * @param step1
	 * @return
	 */
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob").incrementer(new RunIdIncrementer()).listener(listener).flow(step1)
				.end().build();
	}

	/**
	 * Defines a single step, each step can involve a reader, a processor, and a
	 * writer.
	 * 
	 * Define how much data to write at a time. In this case, it writes up to ten
	 * records at a time. Next, you configure the reader, processor, and writer by
	 * using the beans injected earlier.
	 * 
	 * chunk() is prefixed <Person,Person> because it is a generic method. This
	 * represents the input and output types of each “chunk” of processing and lines
	 * up with ItemReader<Person> and ItemWriter<Person>.
	 * 
	 * @param writer
	 * @return
	 */
	@Bean
	public Step step1(JdbcBatchItemWriter<Person> writer) {
		return stepBuilderFactory.get("step1").<Person, Person>chunk(10).reader(reader()).processor(processor())
				.writer(writer).build();
	}
	// end::jobstep[]
}
