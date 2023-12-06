import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import service.auldfellas.AFQService;
import service.core.Quotation;
import service.message.ClientMessage;
import service.message.QuotationMessage;

public class auldfellasMain {

    private static AFQService service = new AFQService();
    private Session session;
    private MessageProducer producer;

    public static void main(String[] args) {
        new auldfellasMain().run();
    }

    public void run() {
        Connection connection = null;
        MessageConsumer consumer = null;

        try {
            // Create a connection to the ActiveMQ broker
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            connection = factory.createConnection();
            connection.setClientID("auldfellas");

            // Create the session
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            // Connect to the relevant queues and topics
            Queue queue = session.createQueue("QUOTATIONS");
            Topic topic = session.createTopic("APPLICATIONS");
            consumer = session.createConsumer(topic);
            producer = session.createProducer(queue);

            // Set the message listener
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        ClientMessage request = (ClientMessage) ((ObjectMessage) message).getObject();
                        Quotation quotation = service.generateQuotation(request.getInfo());

                        // Debug Point: Check the generated quotation
                        System.out.println("Generated Quotation for Auldfellas: " + quotation);

                        Message response = session
                                .createObjectMessage(new QuotationMessage(request.getToken(), quotation));
                        producer.send(response);
                        message.acknowledge();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            connection.start(); // Start the connection to begin listening
            System.out.println("Auldfellas Service is running. Press enter to shutdown...");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            try {
                if (producer != null)
                    producer.close();
                if (consumer != null)
                    consumer.close();
                if (session != null)
                    session.close();
                if (connection != null)
                    connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
