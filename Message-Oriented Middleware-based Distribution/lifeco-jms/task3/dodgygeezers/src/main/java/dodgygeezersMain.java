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

    public static void main(String[] args) {
        try {
            // Create a connection to the ActiveMQ broker
            ConnectionFactory factory = new ActiveMQConnectionFactory("failover://tcp://localhost:61616");
            Connection connection = factory.createConnection();
            connection.setClientID("dodgygeezers");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            // Connect to the relevant queues and topics
            Queue queue = session.createQueue("QUOTATIONS");
            Topic topic = session.createTopic("APPLICATIONS");
            MessageConsumer consumer = session.createConsumer(topic);
            MessageProducer producer = session.createProducer(queue);

            // Set the message listener
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
