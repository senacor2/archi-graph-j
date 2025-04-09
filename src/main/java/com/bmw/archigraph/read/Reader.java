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
        var model = new Model();
        readComponentModel(model);
        model.setApplications(readApplications());
        model.setInformationFlows(readInformationFlows());
        return model;
    }

    private List<Component> mapComponents(JsonNode node, int level) {
        var result = new LinkedList<Component>();
        for (JsonNode n : node.get(COMPONENTS)) {
            var comp = new Component(n.get("name").textValue(),
                    n.get("x").intValue(),
                    n.get("y").intValue(),
                    n.get("w").intValue(),
                    n.get("h").intValue(),
                    level);
            if (n.has(COMPONENTS)) {
                comp.setComponents(mapComponents(n.get(COMPONENTS), level + 1));
            }
            result.add(comp);
        }
        return result;
    }

    void readComponentModel(Model model) throws IOException {
        log.debug("Reading components from {}", compFile);
        var mapper = new ObjectMapper();
        var jsonModel = mapper.readTree(new File(compFile));
        model.setL1Components(mapComponents(jsonModel, 1));
        model.setName(jsonModel.get("system").textValue());
        log.debug("Reading components: Model {} read", model.getName());
    }

    List<Application> readApplications() throws IOException {
        log.debug("Reading applications from {}", appsFile);
        var result = new LinkedList<Application>();
        try (java.io.Reader in = new FileReader(appsFile)) {
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
        log.debug("Reading applications: {} apps read", result.size());
        return result;
    }

    List<InformationFlow> readInformationFlows() throws IOException {
        log.debug("Reading information flows from {}", flowsFile);
        var result = new LinkedList<InformationFlow>();
        try (java.io.Reader in = new FileReader(flowsFile)) {
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
        log.debug("Reading information flows: {} information flows read", result.size());
        return result;
    }

}
