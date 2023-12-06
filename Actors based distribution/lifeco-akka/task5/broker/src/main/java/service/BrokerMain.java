import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import service.BrokerActor;
import service.message.ClientMessage;
import service.message.MySerializable;
import service.message.ClientInfoMessage;
import service.core.ClientInfo;
import java.time.Duration;
import java.util.List;
import java.util.Arrays;

public class BrokerMain implements MySerializable {
    static ActorSystem system;

    public static void main(String[] args) {
        system = ActorSystem.create("broker");

        // Create the Broker actor
        ActorRef broker = system.actorOf(Props.create(BrokerActor.class), "broker");
        System.out.println("DEBUGGING Q5: Creating broker actor from BrokerMain.java");
    }
}
