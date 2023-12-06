import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import service.girlsallowed.Quoter;
import service.message.RegisterMessage;

public class GAMain {
    static ActorSystem system;

    public static void main(String[] args) {
        system = ActorSystem.create();
        // ActorSelection broker =
        // system.actorSelection("akka.tcp://default@localhost:2550/user/broker");
        ActorSelection broker = system.actorSelection("akka.tcp://broker@broker:2550/user/broker");
        ActorRef quoter = system.actorOf(Props.create(Quoter.class), "GAQ");
        broker.tell(new RegisterMessage("GAQ", quoter), quoter);
    }
}