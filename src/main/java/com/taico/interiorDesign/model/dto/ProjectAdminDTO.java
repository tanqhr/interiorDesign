package com.taico.interiorDesign.model.dto;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
    public class ProjectAdminDTO {

        private Long id;

        private String title;

        private String clientName;

        private String serviceType;

        private String status;

        private BigDecimal price;

    }

