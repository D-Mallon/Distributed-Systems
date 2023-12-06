import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
// import java.util.Queue;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import service.core.Quotation;
import service.message.ClientMessage;
import service.message.OfferMessage;
import service.message.QuotationMessage;

public class brokerMain {

    private static Map<Long, OfferMessage> offerCache = new HashMap<>();

    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            Connection connection = factory.createConnection();
            connection.setClientID("broker");
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            // Setting up consumers for APPLICATIONS and QUOTATIONS
            Topic applicationsTopic = session.createTopic("APPLICATIONS");
            MessageConsumer applicationsConsumer = session.createConsumer(applicationsTopic);

            Queue quotationsQueue = session.createQueue("QUOTATIONS");
            MessageConsumer quotationsConsumer = session.createConsumer(quotationsQueue);

            Queue offersQueue = session.createQueue("OFFERS");
            MessageProducer offersProducer = session.createProducer(offersQueue);

            connection.start();

            new Thread(() -> {
                while (true) {
                    handleClientMessages(applicationsConsumer, session, offersProducer);
                }
            }).start();

            new Thread(() -> {
                while (true) {
                    handleQuotationMessages(quotationsConsumer);
                }
            }).start();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientMessages(MessageConsumer applicationsConsumer, Session session,
            MessageProducer offersProducer) {
        try {
            Message message = applicationsConsumer.receive();
            if (message instanceof ObjectMessage) {
                ObjectMessage objMessage = (ObjectMessage) message;
                if (objMessage.getObject() instanceof ClientMessage) {
                    ClientMessage clientMsg = (ClientMessage) objMessage.getObject();
                    OfferMessage offer = new OfferMessage(clientMsg.getInfo(), new LinkedList<Quotation>());
                    offerCache.put(clientMsg.getToken(), offer);

                    new Thread(() -> {
                        try {
                            Thread.sleep(300);
                            sendOfferMessage(clientMsg.getToken(), session, offersProducer);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private static void handleQuotationMessages(MessageConsumer quotationsConsumer) {
        try {
            Message quotationMsg = quotationsConsumer.receive();
            if (quotationMsg instanceof ObjectMessage) {
                ObjectMessage objQuotationMessage = (ObjectMessage) quotationMsg;
                if (objQuotationMessage.getObject() instanceof QuotationMessage) {
                    QuotationMessage qMsg = (QuotationMessage) objQuotationMessage.getObject();
                    if (offerCache.containsKey(qMsg.getToken())) {
                        OfferMessage offer = offerCache.get(qMsg.getToken());
                        offer.getQuotations().add(qMsg.getQuotation());
                    }
                    quotationMsg.acknowledge();
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private static void sendOfferMessage(long token, Session session, MessageProducer offersProducer) {
        OfferMessage offer = offerCache.get(token);
        if (offer != null) {
            try {
                ObjectMessage offerMessage = session.createObjectMessage(offer);
                offersProducer.send(offerMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}