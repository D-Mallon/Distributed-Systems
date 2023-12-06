package service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import service.message.ClientInfoMessage;
import service.message.ClientMessage;
import service.message.OfferMessage;
import service.message.QuotationMessage;
import service.message.RegisterMessage;
import service.message.TimeoutMessage;

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
                    // String quoterName = quoterRef.path().name();
                    quoterRegistry.put(msg.getQuoterName(), quoterRef);
                })

                .match(ClientInfoMessage.class, infoMsg -> {
                    System.out.println("DEBUGGING Q5: Received ClientInfoMessage");
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

}
