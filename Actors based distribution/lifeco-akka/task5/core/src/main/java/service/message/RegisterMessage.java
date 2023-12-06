package service.message;

import akka.actor.ActorRef;

public class RegisterMessage implements MySerializable {
    private final String quoterName;
    private final ActorRef quoter;

    public RegisterMessage(String quoterName, ActorRef quoter) {
        this.quoterName = quoterName;
        this.quoter = quoter;
    }

    public String getQuoterName() {
        return quoterName;
    }

    public ActorRef getQuoter() {
        return quoter;
    }
}
