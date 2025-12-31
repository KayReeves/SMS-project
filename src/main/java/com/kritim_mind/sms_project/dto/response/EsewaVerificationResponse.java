package com.kritim_mind.sms_project.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EsewaVerificationResponse {

    @JsonProperty("transactionDetails")
    private List<TransactionWrapper> transactionDetails;

    @Data
    public static class TransactionWrapper {
        private String productId;
        private String totalAmount;
        private String code;

        @JsonProperty("transactionDetails")
        private TransactionDetail transactionDetails;
    }

    @Data
    public static class TransactionDetail {
        private String status;
        private String referenceId;
        private String date;
    }

    public boolean isSuccess() {
        return transactionDetails != null &&
                !transactionDetails.isEmpty() &&
                transactionDetails.get(0).getTransactionDetails() != null &&
                "COMPLETE".equalsIgnoreCase(transactionDetails.get(0).getTransactionDetails().getStatus());
    }

    public String getReferenceId() {
        if (isSuccess()) {
            return transactionDetails.get(0).getTransactionDetails().getReferenceId();
        }
        return null;
    }
}