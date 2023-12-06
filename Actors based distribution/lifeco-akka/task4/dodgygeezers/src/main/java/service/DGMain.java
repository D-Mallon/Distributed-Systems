import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import service.dodgygeezers.Quoter;
import service.message.RegisterMessage;

public class DGMain {
    static ActorSystem system;

    public static void main(String[] args) {
        system = ActorSystem.create();
        ActorSelection broker = system.actorSelection("akka.tcp://default@localhost:2550/user/broker");
        ActorRef quoter = system.actorOf(Props.create(Quoter.class), "DGQ");
        broker.tell(new RegisterMessage("DGQ", quoter), quoter);
    }
}