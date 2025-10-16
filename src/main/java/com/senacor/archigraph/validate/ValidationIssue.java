package com.senacor.archigraph.validate;

public record ValidationIssue (
        String compName,
        String appId,
        String description
){
}
