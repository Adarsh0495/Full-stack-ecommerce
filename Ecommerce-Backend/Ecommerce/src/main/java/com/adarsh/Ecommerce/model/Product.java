    package com.adarsh.Ecommerce.model;

    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.math.BigDecimal;

    @Entity
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Product
    {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int productId;
        private String productName;
        private String description;
        private String category;
        private BigDecimal productPrice;
        private int quantity;
        private String image;



    }
