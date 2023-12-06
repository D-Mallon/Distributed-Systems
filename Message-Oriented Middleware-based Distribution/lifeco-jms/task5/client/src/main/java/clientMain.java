import java.text.NumberFormat;

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

import org.apache.activemq.ActiveMQConnectionFactory;

import service.core.ClientInfo;
import service.core.Quotation;
import service.message.ClientMessage;
import service.message.OfferMessage;

public class clientMain {
    public static void main(String[] args) {
        try {
            // 1. Create a connection to the broker
            // ConnectionFactory factory = new
            // ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // 2. Set up a MessageProducer for the APPLICATIONS topic
            Topic applicationsTopic = session.createTopic("APPLICATIONS");
            MessageProducer producer = session.createProducer(applicationsTopic);

            // 3. Set up a MessageConsumer for the OFFERS queue
            Queue offersQueue = session.createQueue("OFFERS");
            MessageConsumer consumer = session.createConsumer(offersQueue);

            connection.start();

            // Loop through the test clients and process them one by one
            for (ClientInfo clientInfo : clients) {
                long token = System.nanoTime();
                ClientMessage clientMessage = new ClientMessage(token, clientInfo);

                // Display the client's profile
                displayProfile(clientInfo);

                // 4. Publish the ClientMessage to the APPLICATIONS topic
                ObjectMessage objMessage = session.createObjectMessage(clientMessage);
                producer.send(objMessage);

                // 5. Receive the corresponding offer for the client
                Message message = consumer.receive();
                if (message instanceof ObjectMessage) {
                    ObjectMessage receivedObjMsg = (ObjectMessage) message;

                    if (receivedObjMsg.getObject() instanceof OfferMessage) {
                        OfferMessage offer = (OfferMessage) receivedObjMsg.getObject();
                        for (Quotation quotation : offer.getQuotations()) {
                            displayQuotation(quotation);
                        }
                    }
                }
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    // // public static void main(String[] args) {
    // try

    // {
    // // 1. Create a connection to the broker
    // ConnectionFactory factory = new
    // ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
    // Connection connection = factory.createConnection();
    // Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    // // 2. Set up a MessageProducer for the APPLICATIONS topic
    // Topic applicationsTopic = session.createTopic("APPLICATIONS");
    // MessageProducer producer = session.createProducer(applicationsTopic);

    // // 3. Set up a MessageConsumer for the OFFERS queue
    // Queue offersQueue = session.createQueue("OFFERS");
    // MessageConsumer consumer = session.createConsumer(offersQueue);

    // // Loop through the test clients and send them one by one
    // for (ClientInfo clientInfo : clients) {
    // long token = System.nanoTime();
    // ClientMessage clientMessage = new ClientMessage(token, clientInfo);

    // // Display the client's profile
    // displayProfile(clientInfo);

    // // 4. Publish the ClientMessage to the QUOTATIONS topic
    // ObjectMessage objMessage = session.createObjectMessage(clientMessage);
    // producer.send(objMessage);
    // }

    // connection.start();

    // // 5. Listen to the OFFERS queue for any incoming offers
    // new Thread(() -> {
    // while (true) {
    // try {
    // Message message = consumer.receive();
    // if (message instanceof ObjectMessage) {
    // ObjectMessage receivedObjMsg = (ObjectMessage) message;

    // // Debug Point 1: Check the type of the received message
    // System.out.println(
    // "Received message of type: " +
    // receivedObjMsg.getObject().getClass().getName());

    // if (receivedObjMsg.getObject() instanceof OfferMessage) {
    // OfferMessage offer = (OfferMessage) receivedObjMsg.getObject();

    // // Debug Point 2: Check the number of quotations in the received offer
    // System.out
    // .println("Number of quotations in the offer: " +
    // offer.getQuotations().size());

    // for (Quotation quotation : offer.getQuotations()) {
    // displayQuotation(quotation);
    // }
    // }
    // }
    // } catch (JMSException e) {
    // e.printStackTrace();
    // }
    // }
    // }).start();

    // }catch(
    // JMSException e)
    // {
    // e.printStackTrace();
    // }
    // }

    public static void displayProfile(ClientInfo info) {
        System.out.println(
                "|=================================================================================================================|");
        System.out.println(
                "|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.name) +
                        " | Gender: " + String.format("%1$-27s", (info.gender == ClientInfo.MALE ? "Male" : "Female")) +
                        " | Age: " + String.format("%1$-30s", info.age) + " |");
        System.out.println(
                "| Weight/Height: " + String.format("%1$-20s", info.weight + "kg/" + info.height + "m") +
                        " | Smoker: " + String.format("%1$-27s", info.smoker ? "YES" : "NO") +
                        " | Medical Problems: " + String.format("%1$-17s", info.medicalIssues ? "YES" : "NO") + " |");
        System.out.println(
                "|                                     |                                     |                                     |");
        System.out.println(
                "|=================================================================================================================|");
    }

    /**
     * Display a quotation nicely - note that the assumption is that the quotation
     * will follow
     * immediately after the profile (so the top of the quotation box is missing).
     * 
     * @param quotation
     */
    public static void displayQuotation(Quotation quotation) {
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.company) +
                        " | Reference: " + String.format("%1$-24s", quotation.reference) +
                        " | Price: "
                        + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price)) + " |");
        System.out.println(
                "|=================================================================================================================|");
    }

    /**
     * Test Data
     */
    public static final ClientInfo[] clients = {
            new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49, 1.5494, 80, false, false),
            new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 1.6, 100, true, true),
            new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 21, 1.78, 65, false, false),
            new ClientInfo("Rem Collier", ClientInfo.MALE, 49, 1.8, 120, false, true),
            new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 1.9, 75, true, false),
            new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 0.45, 1.6, false, false)
    };
}
