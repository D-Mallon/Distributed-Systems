package service.girlsallowed;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import service.core.Quotation;
import service.message.ClientMessage;
import service.message.QuotationMessage;

public class Quoter extends AbstractActor {
    private GAQService service = new GAQService();

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(ClientMessage.class, msg -> {
            // Generate a quotation based on client information
            Quotation quotation = service.generateQuotation(msg.getInfo());

            // Send the quotation back to the sender
            getSender().tell(new QuotationMessage(msg.getToken(), quotation), getSelf());
        }).build();
    }
}
