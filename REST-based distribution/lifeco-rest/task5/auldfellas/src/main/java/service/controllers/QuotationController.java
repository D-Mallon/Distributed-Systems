package service.controllers;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.UnknownHostException;

import service.auldfellas.AFQService;
import service.core.ClientInfo;
import service.core.Quotation;

@RestController
public class QuotationController {

   private Map<String, Quotation> quotations = new TreeMap<>();
   private AFQService service = new AFQService();

   @PostMapping(value = "/quotations", consumes = "application/json")
   public ResponseEntity<Quotation> createQuotation(@RequestBody ClientInfo info) {
      Quotation quotation = service.generateQuotation(info);
      quotations.put(quotation.reference, quotation);

      String url = "http://" + getHost() + "/quotations/" + quotation.reference;

      return ResponseEntity
            .status(HttpStatus.CREATED)
            .header("Location", url)
            .header("Content-Location", url)
            .body(quotation);
   }

   @GetMapping(value = "/quotations", produces = "application/json")
   public ResponseEntity<ArrayList<String>> getQuotations() {
      ArrayList<String> list = new ArrayList<>();
      for (Quotation quotation : quotations.values()) {
         list.add("http:" + getHost() + "/quotations/" + quotation.reference);
      }

      return ResponseEntity.status(HttpStatus.OK).body(list);
   }

   @Value("${server.port}")
   private int port;

   private String getHost() {
      try {
         return InetAddress.getLocalHost().getHostAddress() + ":" + port;
      } catch (UnknownHostException e) {
         return "localhost:" + port;
      }
   }

   @GetMapping(value = "/quotations/{id}", produces = { "application/json" })
   public ResponseEntity<Quotation> getQuotation(@PathVariable String id) {
      Quotation quotation = quotations.get(id);
      if (quotation == null) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
      return ResponseEntity.status(HttpStatus.OK).body(quotation);
   }

}
