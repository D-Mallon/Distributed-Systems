package service;

import akka.actor.ActorRef;
import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import service.message.ClientInfoMessage;
import service.message.OfferMessage;
import service.message.QuotationMessage;
import service.message.RegisterMessage;
import service.message.TimeoutMessage;
import service.message.ClientMessage;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BrokerActor extends AbstractActor {
    private final Map<String, ActorRef> quoterRegistry = new HashMap<>();
    private final Map<Long, OfferMessage> clientOfferMessages = new HashMap<>();
    private final Map<Long, ActorRef> clientSenders = new HashMap<>();

    private long tokenCounter = 0;

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()

                .match(RegisterMessage.class, msg -> {
                    ActorRef quoterRef = msg.getQuoter();
                    String quoterName = quoterRef.path().name();
                    quoterRegistry.put(msg.getQuoterName(), quoterRef);
                    System.out.println("************************************");
                    System.out.println("Registered Actor: " + quoterName);
                    System.out.println("************************************");
                    System.out.println("Contents of quoter registry" + quoterRegistry);
                })

                .match(ClientInfoMessage.class, infoMsg -> {
                    System.out.println("************************************");
                    System.out.println("Received client info message");
                    System.out.println("************************************");
                    List<ClientMessage> clientMessages = infoMsg.getClientMessages();
                    ActorRef senderRef = infoMsg.getActorRef();

                    for (ClientMessage msg : clientMessages) {
                        long token = msg.getToken();

                        // Create an OfferMessage with an empty list
                        OfferMessage offerMessage = new OfferMessage(msg.getInfo(), new LinkedList<>());

                        // Store the OfferMessage with the client message token
                        clientOfferMessages.put(token, offerMessage);
                        clientSenders.put(token, senderRef);

                        // Send ClientMessage to each registered quoter actor
                        for (ActorRef quoter : quoterRegistry.values()) {
                            quoter.tell(msg, getSelf());
                        }

                        // Schedule a TimeoutMessage to be sent to itself after a delay
                        getContext().system().scheduler().scheduleOnce(
                                java.time.Duration.ofSeconds(2),
                                getSelf(),
                                new TimeoutMessage(token),
                                getContext().dispatcher(),
                                getSelf());
                    }
                })

                .match(TimeoutMessage.class, msg -> {
                    long token = msg.getToken();
                    ActorRef senderRef = clientSenders.get(token);

                    // Iterate over the linked list of tokens for the current clientActor
                    OfferMessage offerMessage = clientOfferMessages.get(token);
                    if (offerMessage != null) {
                        System.out.println("************************************");
                        System.out.println("Sending offer to client");
                        System.out.println("************************************");
                        senderRef.tell(offerMessage, getSelf());
                    }
                })

                .match(QuotationMessage.class, msg -> {
                    // get the token from the quotation message
                    long token = msg.getToken();
                    OfferMessage relevantOffer = clientOfferMessages.get(token);

                    // Retrieve the corresponding OfferMessage using the token
                    relevantOffer.getQuotations().add(msg.getQuotation());
                })

                .build();
    }

    private long generateToken() {
        return tokenCounter++;
    }
}

// package service;

// import akka.actor.ActorRef;
// import akka.actor.AbstractActor;
// import akka.japi.pf.ReceiveBuilder;
// import service.message.OfferMessage;
// import service.message.QuotationMessage;
// import service.message.RegisterMessage;
// import service.message.TimeoutMessage;
// import service.message.ClientMessage;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.LinkedList;
// import java.util.List;
// import java.util.Map;

// public class Broker_actor extends AbstractActor {
// private final Map<String, ActorRef> quoterRegistry = new HashMap<>();
// private final Map<Long, ActorRef> clientTokens = new HashMap<>();
// private final Map<Long, OfferMessage> clientOfferMessages = new HashMap<>();
// private long tokenCounter = 0;

// @Override
// public Receive createReceive() {
// return ReceiveBuilder.create()

// .match(RegisterMessage.class, msg -> {
// ActorRef quoterRef = msg.getQuoter();
// String quoterName = quoterRef.path().name();
// quoterRegistry.put(msg.getQuoterName(), quoterRef);
// System.out.println("************************************");
// System.out.println("Registered Quoter: " + quoterName);
// System.out.println("************************************");
// })

// // handle client messages
// .match(ClientMessage.class, msg -> {
// long token = generateToken();
// System.out.println("************************************");
// System.out.println("Generating a token for client");
// System.out.println("************************************");
// clientTokens.put(token, getSender());
// System.out.println("************************************");
// System.out.println("Storing client info: " + clientTokens);
// System.out.println("************************************");

// List<QuotationMessage> quotations = new LinkedList<>();
// for (ActorRef quoter : quoterRegistry.values()) {
// quoter.tell(msg, getSelf());
// }

// // Create an OfferMessage with an empty list
// OfferMessage offerMessage = new OfferMessage(msg.getInfo(), new
// LinkedList<>());

// // Schedule a TimeoutMessage to be sent to itself after a delay
// getContext().system().scheduler().scheduleOnce(
// java.time.Duration.ofSeconds(10),
// getSelf(),
// new TimeoutMessage(token),
// getContext().dispatcher(),
// getSelf()
// );
// })

// // send after 2 seconds
// .match(TimeoutMessage.class, msg -> {
// long token = msg.getToken();
// ActorRef clientActor = clientTokens.get(token);
// System.out.println("Content of client tokens" + clientTokens);
// System.out.println("************************************");
// System.out.println("Received timeout message");
// System.out.println("************************************");
// if (clientActor != null) {
// // Handle the TimeoutMessage and retrieve the associated OfferMessage
// OfferMessage offerMessage = clientOfferMessages.get(token);
// if (offerMessage != null) {
// System.out.println("************************************");
// System.out.println("Sending offer to client");
// System.out.println("************************************");
// clientActor.tell(offerMessage, getSelf());
// }
// }
// })
// .match(QuotationMessage.class, msg -> {
// long token = msg.getToken();
// ActorRef clientActor = clientTokens.get(token);
// System.out.println("************************************");
// System.out.println("Received quotation message");
// System.out.println("************************************");
// if (clientActor != null) {
// OfferMessage offerMessage = clientOfferMessages.get(token);
// if (offerMessage != null) {
// // Handle the QuotationMessage and add the provided Quotation to the
// OfferMessage
// offerMessage.getQuotations().add(msg.getQuotation());
// System.out.println("************************************");
// System.out.println("Adding quotation to offer message");
// System.out.println("************************************");
// // Update the OfferMessage in the map
// clientOfferMessages.put(token, offerMessage);

// }
// }
// })
// .build();
// }

// private long generateToken() {
// return tokenCounter++;
// }
// }
