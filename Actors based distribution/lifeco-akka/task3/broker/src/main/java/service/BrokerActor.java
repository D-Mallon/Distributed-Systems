package service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import service.message.ClientMessage;
import service.message.OfferMessage;
import service.message.QuotationMessage;
import service.message.RegisterMessage;
import service.message.TimeoutMessage;

public class BrokerActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private static Map<Long, OfferMessage> offerCache = new HashMap<>();
    private static Map<String, akka.actor.ActorRef> quoterActors = new HashMap<>();
    private final Map<Long, ActorRef> clientActorRefsMap = new HashMap<>();

    // Static method to create props for an actor
    public static Props props() {
        return Props.create(BrokerActor.class);
    }

    @Override
    public Receive createReceive() {
        System.out.println("BrokerActor: createReceive");
        return receiveBuilder()
                .match(ClientMessage.class, this::onProcessClientMessage)
                .match(QuotationMessage.class, this::onProcessQuotationMessage)
                .match(TimeoutMessage.class, this::onTimeoutMessage)
                .match(RegisterMessage.class, this::onRegisterMessage)
                .build();
    }

    private void onProcessClientMessage(ClientMessage message) {
        System.out.println("BrokerActor: onProcessClientMessage");
        log.info("Received ClientMessage: {}", message);
        long token = message.getToken();
        OfferMessage offer = new OfferMessage(message.getInfo(), new LinkedList<>());
        log.info("Created OfferMessage for token {}: {}", token, offer);
        offerCache.put(token, offer);
        clientActorRefsMap.put(token, getSender());

        quoterActors.values().forEach(quoterActor -> {
            System.out.println("BrokerActor: Sending ClientMessage to QuoterActor");
            quoterActor.tell(message, getSelf());
        });

        getContext().getSystem().scheduler().scheduleOnce(
                scala.concurrent.duration.Duration.create(2, "seconds"),
                getSelf(),
                new TimeoutMessage(token),
                getContext().getDispatcher(),
                getSelf());
    }

    private void onProcessQuotationMessage(QuotationMessage message) {
        System.out.println("BrokerActor: onProcessQuotationMessage");
        log.info("Received QuotationMessage: {}", message);
        if (offerCache.containsKey(message.getToken())) {
            OfferMessage offer = offerCache.get(message.getToken());
            offer.getQuotations().add(message.getQuotation());
        }
    }

    private void onTimeoutMessage(TimeoutMessage message) {
        System.out.println("BrokerActor: onTimeoutMessage");
        log.info("Timeout occurred for token {}", message.getToken());
        long token = message.getToken();
        OfferMessage offer = offerCache.remove(token);
        if (offer != null) {
            ActorRef clientActorRef = getClientActorRefByToken(token);
            if (clientActorRef != null) {
                System.out.println("BrokerActor: Sending OfferMessage to Client");
                clientActorRef.tell(offer, getSelf());
            } else {
                log.warning("Client actor reference not found for token: {}", token);
            }
        }
    }

    private void onRegisterMessage(RegisterMessage message) {
        System.out.println("BrokerActor: onRegisterMessage");
        quoterActors.put(getSender().path().name(), getSender());
    }

    private ActorRef getClientActorRefByToken(long token) {
        return clientActorRefsMap.get(token);
    }
}
