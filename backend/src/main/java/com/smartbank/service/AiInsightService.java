package com.smartbank.service;

import com.fasterxml.jackson.databind.*;
import com.smartbank.repository.BankTransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.util.*;

@Service
public class AiInsightService {
    private final BankTransactionRepository transactions;
    private final String apiKey, baseUrl, model;
    private final RestClient restClient = RestClient.create();
    private final ObjectMapper mapper = new ObjectMapper();
    public AiInsightService(BankTransactionRepository transactions,
        @Value("${app.ai.api-key}") String apiKey, @Value("${app.ai.base-url}") String baseUrl,
        @Value("${app.ai.model}") String model) {
        this.transactions=transactions; this.apiKey=apiKey; this.baseUrl=baseUrl; this.model=model;
    }
    public Map<String,Object> insights(String email) {
        var rows=transactions.findAllByAccountCustomerIdOrderByCreatedAtDesc(
            // The repository query uses customer id, so resolve via transaction data is not possible from email here.
            // The controller supplies the authenticated customer ID through the overloaded method.
            -1L);
        return Map.of("message","Use customer ID overload");
    }
    public Map<String,Object> insightsForCustomer(Long customerId) {
        var rows=transactions.findAllByAccountCustomerIdOrderByCreatedAtDesc(customerId);
        if (rows.isEmpty()) return Map.of("insights", List.of("No transactions yet. Make a deposit or transaction to generate insights."));
        Map<String,BigDecimal> totals=new LinkedHashMap<>();
        for (var t:rows) {
            String key=t.getType();
            totals.merge(key,t.getAmount(),BigDecimal::add);
        }
        List<Map<String,Object>> summary=new ArrayList<>();
        totals.forEach((k,v)->summary.add(Map.of("transactionType",k,"totalAmount",v)));
        if (apiKey==null || apiKey.isBlank()) return Map.of("summary",summary,"insights",List.of("AI is not configured. Add AI_API_KEY to backend environment to enable personalized insights."));
        String prompt="You are a cautious financial education assistant. Based only on these aggregate transaction totals, provide 3-5 concise budgeting observations. Do not claim to be a financial advisor, do not make investment recommendations, and mention that this is educational, not professional financial advice. Data: "+summary;
        try {
            Map<String,Object> body=Map.of("model",model,"messages",List.of(
                Map.of("role","system","content","Give general, cautious personal budgeting insights. Never ask for secrets."),
                Map.of("role","user","content",prompt)),"temperature",0.4);
            JsonNode response=restClient.post().uri(baseUrl+"/chat/completions")
                .header("Authorization","Bearer "+apiKey).body(body).retrieve().body(JsonNode.class);
            String content=response.path("choices").path(0).path("message").path("content").asText("No AI response received.");
            return Map.of("summary",summary,"insights",content);
        } catch (Exception ex) {
            return Map.of("summary",summary,"insights",List.of("AI provider request failed. Verify API key, model and provider configuration."));
        }
    }
}
