package service.client;

import java.text.NumberFormat;
import java.util.LinkedList;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import service.core.ClientInfo;
import service.core.Quotation;
import service.message.OfferMessage;

public class ClientActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder().match(OfferMessage.class, this::handleOfferMessage).build();
    }

    private void handleOfferMessage(OfferMessage offerMessage) {
        ClientInfo clientInfo = offerMessage.getInfo();
        displayProfile(clientInfo);
        LinkedList<Quotation> quotations = offerMessage.getQuotations();
        for (Quotation quotation : quotations) {
            displayQuotation(quotation);
        }
    }

    // adding standard display info from other projects used to date
    private void displayProfile(ClientInfo info) {
        System.out.println(
                "|=================================================================================================================|");
        System.out.println(
                "|                                     |                                     |                                     |");
        System.out.println(
                "| Name: " + String.format("%1$-29s", info.name) +
                        " | Gender: " + String.format("%1$-27s", (info.gender == ClientInfo.MALE ? "Male" : "Female")) +
                        " | Age: " + String.format("%1$-30s", info.age) + " |");
        System.out.println(
                "| Weight/Height: " + String.format("%1$-20s", info.weight + "kg/" + info.height + "m") +
                        " | Smoker: " + String.format("%1$-27s", info.smoker ? "YES" : "NO") +
                        " | Medical Problems: " + String.format("%1$-17s", info.medicalIssues ? "YES" : "NO") + " |");
        System.out.println(
                "|                                     |                                     |                                     |");
        System.out.println(
                "|=================================================================================================================|");
    }

    private void displayQuotation(Quotation quotation) {
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.company) +
                        " | Reference: " + String.format("%1$-24s", quotation.reference) +
                        " | Price: "
                        + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price)) + " |");
        System.out.println(
                "|=================================================================================================================|");
    }
}
