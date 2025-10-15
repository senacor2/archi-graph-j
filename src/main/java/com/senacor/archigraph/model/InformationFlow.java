package com.senacor.archigraph.model;

import lombok.Data;

@Data
public class InformationFlow {

    private String id;

    String sourceId;

    String destId;

    private Application source;

    private Application destination;

    private String businessObject;

    private Direction direction;

    public InformationFlow(String id, String sourceId, String destId, String businessObject, Direction dir) {
        this.id = id;
        this.sourceId = sourceId;
        this.destId = destId;
        this.businessObject = businessObject;
        this.direction = dir;
    }
}
