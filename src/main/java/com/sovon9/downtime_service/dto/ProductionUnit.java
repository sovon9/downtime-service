package com.sovon9.downtime_service.dto;

import jakarta.persistence.*;

// Stub entity — maps to production_unit table in the same shared DB.
// Only the PK is needed here; the full entity is owned by mes-service.
// The federation gateway resolves the full ProductionUnit graph from mes-service.
@Entity
@Table(name = "production_unit")
public class ProductionUnit {

    @Id
    @Column(name = "id")
    private Long productionUnitId;

    public Long getProductionUnitId() {
        return productionUnitId;
    }

    public void setProductionUnitId(Long productionUnitId) {
        this.productionUnitId = productionUnitId;
    }
}