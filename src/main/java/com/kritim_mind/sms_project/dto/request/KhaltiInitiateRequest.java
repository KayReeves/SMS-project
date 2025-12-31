package com.kritim_mind.sms_project.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhaltiInitiateRequest {

    @NotNull(message = "Amount is required")
    @Min(value = 10000, message = "Minimum amount is Rs. 100 (10000 paisa)")
    private Integer amount;

    @NotBlank(message = "Purchase order ID is required")
    @JsonProperty("purchase_order_id")
    private String purchase_order_id;

    @NotBlank(message = "Purchase order name is required")
    @JsonProperty("purchase_order_name")
    private String purchase_order_name;

    @NotBlank(message = "Return URL is required")
    @JsonProperty("return_url")
    private String return_url;

    @NotBlank(message = "Website URL is required")
    @JsonProperty("website_url")
    private String website_url;

    @NotEmpty(message = "Amount breakdown is required")
    @JsonProperty("amount_breakdown")
    private List<AmountBreakdownItem> amount_breakdown;


    @NotEmpty(message = "Product details are required")
    @JsonProperty("product_details")
    private List<ProductDetail> product_details;

    @NotNull(message = "Customer info is required")
    @JsonProperty("customer_info")
    private CustomerInfo customer_info;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AmountBreakdownItem {
        @NotBlank(message = "Label is required")
        private String label;

        @NotNull(message = "Amount is required")
        @Min(0)
        private Integer amount;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDetail {
        @NotBlank(message = "Identity is required")
        private String identity;

        private String name;

        @NotNull(message = "Total price is required")
        @JsonProperty("total_price")
        @Min(0)
        private Integer total_price;

        @Min(1)
        private Integer quantity = 1;

        @JsonProperty("unit_price")
        @Min(0)
        private Integer unit_price;
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        private String name;
        private String email;
        private String phone;
    }
}