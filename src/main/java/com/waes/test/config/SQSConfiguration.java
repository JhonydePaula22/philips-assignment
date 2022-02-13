package com.waes.test.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;

/**
 * Configuration class to instantiate the AWS SQS {@link Bean} and its dependencies.
 *
 * @author jonathanadepaula
 */
@Configuration
public class SQSConfiguration {

    @Value("${cloud.aws.region.static}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKeyId;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretAccessKey;

    @Value("${cloud.aws.end-point.uri}")
    private String sqsUrl;

    /**
     * Builds {@link AmazonSQSAsync} client based on the properties provided.
     *
     * @return {@link AmazonSQSAsync}
     */
    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(sqsUrl, region))
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
                .build();
    }

    /**
     * Builds a {@link QueueMessagingTemplate}.
     *
     * @param amazonSQSAsync
     * @return {@link QueueMessagingTemplate}
     */
    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }

    /**
     * Builds the {@link MessageConverter} needed to convert the messages.
     *
     * @param objectMapper
     * @return {@link MessageConverter}
     */
    @Bean
    protected MessageConverter messageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(objectMapper);
        converter.setSerializedPayloadClass(String.class);
        converter.setStrictContentTypeMatch(false);
        return converter;
    }
}
