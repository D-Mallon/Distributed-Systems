import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import service.auldfellas.Quoter;
import service.message.RegisterMessage;

public class AFMain {
    static ActorSystem system;

    public static void main(String[] args) {
        system = ActorSystem.create();
        ActorSelection broker = system.actorSelection("akka.tcp://default@localhost:2550/user/broker");
        ActorRef quoter = system.actorOf(Props.create(Quoter.class), "AFQ");
        broker.tell(new RegisterMessage("AFQ", quoter), quoter);

    }
}