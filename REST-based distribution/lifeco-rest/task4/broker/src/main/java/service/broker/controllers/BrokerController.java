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

    // Declare variables for service ports, RestTemplate and the application map
    private final List<Integer> servicePorts = Arrays.asList(8080, 8081, 8082);
    private final RestTemplate restTemplate;
    private final Map<Integer, Application> applications = new ConcurrentHashMap<>();
    private int applicationCounter = 1000; // Starting ID for applications

    // Constructor to initialise RestTemplate
    public BrokerController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Endpoint for creating new applications
    @PostMapping(value = "/applications", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Application> createApplications(@RequestBody ClientInfo info) {

        Application application = new Application(info);

        int id = applicationCounter++;
        application.id = id;

        // Process quotation requests for each service port
        servicePorts.forEach(servicePort -> processQuotationRequest(servicePort, info, application));

        // Save the completed application in the map
        applications.put(id, application);

        // Return the created application
        return ResponseEntity.status(HttpStatus.CREATED).body(application);
    }

    // Method to process quotation requests
    private void processQuotationRequest(int servicePort, ClientInfo info, Application application) {
        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HTTP entity for the request
        HttpEntity<ClientInfo> requestEntity = new HttpEntity<>(info, headers);

        // Formulate the service URL
        String serviceUrl = "http://localhost:" + servicePort + "/quotations";

        // Send the HTTP request and capture the response
        try {
            ResponseEntity<Quotation> response = restTemplate.exchange(
                    serviceUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Quotation.class);

            // Add the quotation to the application if the status is CREATED
            if (response.getStatusCode() == HttpStatus.CREATED) {
                application.quotations.add(response.getBody());
            }

        } catch (ResourceAccessException | HttpServerErrorException e) {
            // exceptions handling
            System.out.println("Service at port " + servicePort + " encountered an issue: " + e.getMessage());
        }
    }

    // Endpoint for fetching an application by ID
    @GetMapping(value = "/applications/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Application> getApplication(@PathVariable int id) {
        // Retrieve the application from the map
        Application application = applications.get(id);

        // Return the application if found, otherwise return a 404
        return (application != null) ? ResponseEntity.ok(application) : ResponseEntity.notFound().build();
    }
}