package service.message;

import akka.actor.ActorRef;

import java.util.List;

public class ClientInfoMessage implements MySerializable {
    private List<ClientMessage> clientMessages;
    private ActorRef actorRef;

    public ClientInfoMessage(List<ClientMessage> clientMessages, ActorRef actorRef) {
        this.clientMessages = clientMessages;
        this.actorRef = actorRef;
    }

    public List<ClientMessage> getClientMessages() {
        return clientMessages;
    }

    public ActorRef getActorRef() {
        return actorRef;
    }
}
