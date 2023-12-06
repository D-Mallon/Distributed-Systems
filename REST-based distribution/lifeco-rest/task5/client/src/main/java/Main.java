import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.text.NumberFormat;
import okhttp3.*;
import service.core.ClientInfo;
import service.core.Quotation;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class Main {
    public static void main(String[] args) {
        // Set up OkHttpClient with timeouts and a connection pool
        OkHttpClient client = new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool())
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();

        try {
            // Loop through each client to generate and display quotations
            for (ClientInfo clientInfo : clients) {
                // Prepare JSON data for POST request
                String requestBody = prepareJsonRequestBody(clientInfo);

                // Prepare and execute the POST request
                Request postRequest = new Request.Builder()
                        .url("http://localhost:8083/applications")
                        .post(RequestBody.create(
                                MediaType.parse("application/json"),
                                requestBody))
                        .build();

                // Make the API call
                Response response = client.newCall(postRequest).execute();

                // Check if the API call was successful
                if (response.isSuccessful()) {
                    String quotationsResponse = response.body().string();
                    // Display profile and quotations
                    displayProfile(clientInfo);
                    displayQuotationsFromResponse(quotationsResponse);
                } else {
                    System.out.println("POST request for quotations failed for client: " + clientInfo.name);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.dispatcher().executorService().shutdown();
            try {
                client.dispatcher().executorService().awaitTermination(15, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                // Log or handle the interruption
            }
            client.connectionPool().evictAll();
        }
    }
    // resolving warnings from the compiler
    // } finally {
    // // Clean up resources
    // client.dispatcher().executorService().shutdown();
    // client.connectionPool().evictAll();
    // }
    // }

    public static void displayProfile(ClientInfo info) {
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

    /**
     * Display a quotation nicely - note that the assumption is that the quotation
     * will follow
     * immediately after the profile (so the top of the quotation box is missing).
     * 
     * @param quotation
     */
    public static void displayQuotation(Quotation quotation) {
        System.out.println(
                "| Company: " + String.format("%1$-26s", quotation.company) +
                        " | Reference: " + String.format("%1$-24s", quotation.reference) +
                        " | Price: "
                        + String.format("%1$-28s", NumberFormat.getCurrencyInstance().format(quotation.price)) + " |");
        System.out.println(
                "|=================================================================================================================|");
    }

    public static void displayQuotationsFromResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            JSONArray quotations = json.getJSONArray("quotations");
            for (int i = 0; i < quotations.length(); i++) {
                JSONObject quotationData = quotations.getJSONObject(i);
                Quotation quotation = new Quotation(
                        quotationData.getString("company"),
                        quotationData.getString("reference"),
                        quotationData.getDouble("price"));
                displayQuotation(quotation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static String prepareJsonRequestBody(ClientInfo clientInfo) {
        return "{ " +
                "\"name\": \"" + clientInfo.name + "\", " +
                "\"gender\": \"" + clientInfo.gender + "\", " +
                "\"age\": " + clientInfo.age + ", " +
                "\"height\": " + clientInfo.height + ", " +
                "\"weight\": " + clientInfo.weight + ", " +
                "\"smoker\": " + clientInfo.smoker + ", " +
                "\"medicalIssues\": " + clientInfo.medicalIssues + " " +
                "}";
    }

    /**
     * Test Data
     */
    public static final ClientInfo[] clients = {
            new ClientInfo("Niki Collier", ClientInfo.FEMALE, 49, 1.5494, 80, false, false),
            new ClientInfo("Old Geeza", ClientInfo.MALE, 65, 1.6, 100, true, true),
            new ClientInfo("Hannah Montana", ClientInfo.FEMALE, 21, 1.78, 65, false, false),
            new ClientInfo("Rem Collier", ClientInfo.MALE, 49, 1.8, 120, false, true),
            new ClientInfo("Jim Quinn", ClientInfo.MALE, 55, 1.9, 75, true, false),
            new ClientInfo("Donald Duck", ClientInfo.MALE, 35, 0.45, 1.6, false, false)
    };

}
