import service.dodgygeezers.DGQService;
import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import service.core.Quotation;
import service.message.ClientMessage;
import service.message.QuotationMessage;
import akka.actor.ActorSystem;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.time.Duration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import service.core.ClientInfo;
import service.dodgygeezers.Quoter;


public class DGQTest {
    static ActorSystem system;
    @BeforeClass public static void setup() {system = ActorSystem.create(); } 
    @AfterClass public static void teardown() {
        TestKit.shutdownActorSystem(system); system = null; 
    }

    @Test public void quoterTest() {
        final Props props = Props.create(Quoter.class); 
        final ActorRef subject = system.actorOf(props); 
        final TestKit probe = new TestKit(system); 
        subject.tell(
            new ClientMessage(1l,
            new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49, 1.5494, 80, false, false)),
            probe.getRef());
            probe.expectMsgClass(Duration.ofSeconds(2), QuotationMessage.class);
        } 
    }