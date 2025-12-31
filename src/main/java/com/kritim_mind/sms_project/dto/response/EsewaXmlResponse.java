package com.kritim_mind.sms_project.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)  // This will ignore any unknown fields
public class EsewaXmlResponse {

    @JsonProperty("transaction_code")
    private String transactionCode;

    @JsonProperty("status")
    private String status;

    @JsonProperty("total_amount")
    private String totalAmount;

    @JsonProperty("transaction_uuid")
    private String transactionUuid;

    @JsonProperty("product_code")
    private String productCode;

    @JsonProperty("signed_field_names")
    private String signedFieldNames;

    @JsonProperty("signature")
    private String signature;

    @JsonProperty("ref_id")
    private String refId;

    // Getters and Setters
    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTransactionUuid() {
        return transactionUuid;
    }

    public void setTransactionUuid(String transactionUuid) {
        this.transactionUuid = transactionUuid;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSignedFieldNames() {
        return signedFieldNames;
    }

    public void setSignedFieldNames(String signedFieldNames) {
        this.signedFieldNames = signedFieldNames;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }
}