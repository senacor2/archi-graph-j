package com.bmw.archigraph.read;

import com.bmw.archigraph.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class Reader {

    public static final String COMPONENTS = "components";
    private final String compFile;
    private final String appsFile;
    private final String flowsFile;

    public Reader(String compFile, String appsFile, String flowsFile) {
        this.compFile = compFile;
        this.appsFile = appsFile;
        this.flowsFile = flowsFile;
    }

    public Model readModels() throws IOException {
        return new Model()
                .components(readComponents(compFile))
                .applications(readApplications(appsFile))
                .informationFlows(readInformationFlows(flowsFile));
    }

    private List<Component> mapComponents(JsonNode node, int level) {
        var result = new LinkedList<Component>();
        for (JsonNode n : node.get(COMPONENTS)) {
            var comp = new Component(node.get("name").textValue(),
                    node.get("x").intValue(),
                    node.get("y").intValue(),
                    node.get("w").intValue(),
                    node.get("h").intValue(),
                    level);
            if (node.has(COMPONENTS)) {
                comp.setComponents(mapComponents(node.get(COMPONENTS), level + 1));
            } else {
                comp.setComponents(List.of());
            }
            result.add(comp);
        }
        return result;
    }

    private List<Component> readComponents(String compFileName) throws IOException {
        var mapper = new ObjectMapper();
        var componentModel = mapper.readTree(new File(compFileName));
        return mapComponents(componentModel, 1);
    }

    private List<Application> readApplications(String appsFileName) throws IOException {
        var result = new LinkedList<Application>();
        try (java.io.Reader in = new FileReader(appsFileName)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
            for (CSVRecord record : records) {
                var id = record.get(0);
                var name = record.get(1);
                var compName = record.get(2);
                var attr1 = record.get(3);
                var attr2 = record.get(4);
                var attr3 = record.get(5);
                result.add(new Application(id, name, compName, attr1, attr2, attr3));
            }
        }
        return result;
    }

    private List<InformationFlow> readInformationFlows(String flowsFileName) throws IOException {
        var result = new LinkedList<InformationFlow>();
        try (java.io.Reader in = new FileReader(flowsFileName)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.parse(in);
            for (CSVRecord record : records) {
                var sourceName = record.get(0);
                var destName = record.get(1);
                var id = record.get(2);
                var bo = record.get(3);
                var dir = record.get(4).equals("oneway") ? Direction.ONE_WAY : Direction.TWO_WAY;
                result.add(new InformationFlow(id, sourceName, destName, bo, dir));
            }
        }
        return result;
    }

}
