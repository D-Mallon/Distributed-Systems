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
import service.girlsallowed.GAQService;
import service.message.ClientMessage;
import service.message.QuotationMessage;

public class girlsallowedMain {
    private static GAQService service = new GAQService();
    private Session session;
    private MessageProducer producer;

    public static void main(String[] args) {
        new girlsallowedMain().run();
    }

    public void run() {
        Connection connection = null;
        MessageConsumer consumer = null;

        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            connection = factory.createConnection();
            connection.setClientID("girlsallowed");
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
            System.out.println("Girls Allowed Service is running. Press enter to shutdown...");
            System.in.read();

        } catch (Exception e) {
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
