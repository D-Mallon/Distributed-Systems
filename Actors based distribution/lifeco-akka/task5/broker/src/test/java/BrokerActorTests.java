import java.time.Duration;
import java.util.Arrays;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import service.BrokerActor;
import service.core.ClientInfo;
import service.message.ClientMessage;
import service.message.OfferMessage;
import service.message.ClientInfoMessage;

public class BrokerActorTests {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void brokerTest() {
        final Props props = Props.create(BrokerActor.class);
        final ActorRef subject = system.actorOf(props);
        final TestKit probe = new TestKit(system);

        // Create a ClientMessage
        ClientMessage clientMessage = new ClientMessage(1L,
                new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49, 1.5494, 80, false, false));

        // Wrap the ClientMessage in a ClientInfoMessage
        ClientInfoMessage clientInfoMessage = new ClientInfoMessage(Arrays.asList(clientMessage), probe.getRef());

        // Send the ClientInfoMessage to the BrokerActor
        subject.tell(clientInfoMessage, probe.getRef());

        // Expect an OfferMessage in response
        probe.expectMsgClass(Duration.ofSeconds(10), OfferMessage.class);
    }
}
