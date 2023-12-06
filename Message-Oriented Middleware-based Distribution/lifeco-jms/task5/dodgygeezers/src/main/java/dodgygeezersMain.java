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

import service.core.Quotation;
import service.dodgygeezers.DGQService;
import service.message.ClientMessage;
import service.message.QuotationMessage;

public class dodgygeezersMain {
    private static DGQService service = new DGQService();
    private Session session;
    private MessageProducer producer;

    public static void main(String[] args) {
        new dodgygeezersMain().run();
    }

    public void run() {
        Connection connection = null;
        MessageConsumer consumer = null;

        try {
            // Get the broker host from the environment variable or default to localhost
            String brokerHost = System.getenv("BROKER_HOST");
            if (brokerHost == null || brokerHost.isEmpty()) {
                brokerHost = "localhost";
            }

            // Use the brokerHost in the connection factory
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://" + brokerHost + ":61616");
            connection = factory.createConnection();
            connection.setClientID("dodgygeezers");

            // try {
            // ConnectionFactory factory = new
            // ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            // connection = factory.createConnection();
            // connection.setClientID("dodgygeezers");

            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            Queue queue = session.createQueue("QUOTATIONS");
            Topic topic = session.createTopic("APPLICATIONS");
            consumer = session.createConsumer(topic);
            producer = session.createProducer(queue);

            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        ClientMessage request = (ClientMessage) ((ObjectMessage) message).getObject();
                        Quotation quotation = service.generateQuotation(request.getInfo());

                        Message response = session
                                .createObjectMessage(new QuotationMessage(request.getToken(), quotation));
                        producer.send(response);
                        message.acknowledge();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            connection.start();
            System.out.println("Dodgy Geezers Service is running...");

            // Sleep in loop to keep service running
            while (true) {
                Thread.sleep(10000); // Sleep for 10 seconds
            }

        } catch (

        JMSException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        } finally {
            cleanupResources(producer, consumer, session, connection);
        }
    }

    private void cleanupResources(MessageProducer producer, MessageConsumer consumer, Session session,
            Connection connection) {
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
