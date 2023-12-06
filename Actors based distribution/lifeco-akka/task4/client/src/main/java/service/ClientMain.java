import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import service.client.ClientActor;
import service.core.ClientInfo;
import service.message.ClientInfoMessage;
import service.message.ClientMessage;
import service.message.MySerializable;

public class ClientMain implements MySerializable {

    static ActorSystem system;

    public static void main(String[] args) {
        system = ActorSystem.create();
        List<ClientInfo> clientInfos = Arrays.asList(clients);

        // Create ClientMessages from client profiles with incremental tokens
        List<ClientMessage> clientMessages = clientInfos.stream()
                .map(clientInfo -> new ClientMessage(generateToken(), clientInfo))
                .collect(Collectors.toList());

        // Create the broker actor selection - ensure that this works as this was
        // causing issues throughout Task 4
        ActorSelection broker = system.actorSelection("akka.tcp://default@localhost:2550/user/broker");

        // Create the initial ClientActor and sending the ClientInfoMessage to the
        // broker
        ActorRef clientActor = system.actorOf(Props.create(ClientActor.class),
                "initial_actor");
        ClientInfoMessage clientInfoMessage = new ClientInfoMessage(clientMessages,
                clientActor);
        broker.tell(new ClientInfoMessage(clientMessages, clientActor), clientActor);
    }

    public static final ClientInfo[] clients = {
            new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49, 1.5494, 80, false, false),
            new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 1.6, 100, true, true),
            new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 21, 1.78, 65, false, false),
            new ClientInfo("Rem Collier", ClientInfo.MALE, 49, 1.8, 120, false, true),
            new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 1.9, 75, true, false),
            new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 0.45, 1.6, false, false)
    };

    // The token counter for generating incremental tokens
    private static long tokenCounter = 1;

    // Generate a new token
    private static long generateToken() {
        return tokenCounter++;
    }
}
