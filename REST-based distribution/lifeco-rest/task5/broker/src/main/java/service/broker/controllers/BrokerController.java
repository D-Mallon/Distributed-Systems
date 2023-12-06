package service.broker.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.*;
import service.core.Application;
import service.core.ClientInfo;
import service.core.Quotation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class BrokerController {

    // Declare variables for service ports, RestTemplate, and the application map -
    // changes implemented here for question 5
    private List<String> serviceUrls = new ArrayList<>(Arrays.asList(
            "http://auldfellas:8080/quotations",
            "http://girlsallowed:8081/quotations",
            "http://dodgygeezers:8082/quotations"));

    private final RestTemplate restTemplate;
    private final Map<Integer, Application> applications = new ConcurrentHashMap<>();
    private int applicationCounter = 1000;

    public BrokerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/applications", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Application> createApplications(@RequestBody ClientInfo info) {
        Application application = new Application(info);
        int id = applicationCounter++;
        application.id = id;

        for (String serviceUrl : serviceUrls) {
            processQuotationRequest(serviceUrl, info, application);
        }

        applications.put(id, application);
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    private void processQuotationRequest(String serviceUrl, ClientInfo info, Application application) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ClientInfo> requestEntity = new HttpEntity<>(info, headers);

        try {
            ResponseEntity<Quotation> response = restTemplate.exchange(
                    serviceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Quotation.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                application.quotations.add(response.getBody());
            }

        } catch (ResourceAccessException e) {
            System.out.println("Service at URL " + serviceUrl + " is unavailable.");
        } catch (HttpServerErrorException e) {
            System.out.println("Service at URL " + serviceUrl + " returned an error: " + e.getRawStatusCode());
        }
    }

    @GetMapping(value = "/applications/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Application> getApplication(@PathVariable int id) {
        Application application = applications.get(id);
        return (application != null) ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
    }

    // WIP
    // make sure warnings are resolved
    @PostMapping(value = "/services", consumes = "text/plain")
    public ResponseEntity<Void> addService(@RequestBody String url) {
        serviceUrls.add(url);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/services", produces = "application/json")
    public ResponseEntity<List<String>> getServices() {
        return new ResponseEntity<>(serviceUrls, HttpStatus.OK);
    }
}