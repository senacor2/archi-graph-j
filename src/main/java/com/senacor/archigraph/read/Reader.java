package com.senacor.archigraph.read;

import com.senacor.archigraph.model.Area;
import com.senacor.archigraph.model.*;
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
    public static final String SYSTEM = "system";
    public static final String APP_AREA = "app-area";
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
        if (appsFile != null) model.setApplications(readApplications());
        if (flowsFile != null) model.setInformationFlows(readInformationFlows());
        log.info("Model size:");
        log.info("Components:        {}", model.getComponentMap().size());
        log.info("Applications:      {}", model.getApplicationMap().size());
        log.info("Information Flows: {}", model.getInformationFlowMap().size());
        return model;
    }

    private List<Component> mapComponents(JsonNode node, int level) {
        var result = new LinkedList<Component>();
        for (JsonNode n : node.get(COMPONENTS)) {
            var comp = new Component(n.get("name").textValue(),
                    n.get("y").intValue(), n.get("x").intValue(),
                    n.get("w").intValue(),
                    n.get("h").intValue(),
                    level);
            if (n.has(APP_AREA)) {
                var areaNode = n.get(APP_AREA);
                var area = new Area(areaNode.get("y").intValue(),
                        areaNode.get("x").intValue(),
                        areaNode.get("w").intValue(),
                        areaNode.get("h").intValue());
                comp.setAppArea(area);
            }
            if (n.has(COMPONENTS)) {
                comp.setComponents(mapComponents(n, level + 1));
            }
            result.add(comp);
        }
        return result;
    }

    private List<String> mapComponentNames(JsonNode node) {
        var result = new LinkedList<String>();
        for (JsonNode n : node.get("component-names")) {
            result.add(n.textValue());
        }
        return result;
    }

    void readComponentModel(Model model) throws IOException {
        log.debug("Reading components from {}", compFile);
        var mapper = new ObjectMapper();
        var jsonModel = mapper.readTree(new File(compFile));
        model.setL1Components(mapComponents(jsonModel, 1));
        model.setName(jsonModel.get(SYSTEM).textValue());
        model.setComponentNames(mapComponentNames(jsonModel));
        log.debug("Reading components: Model {} read", model.getName());
    }

    List<Application> readApplications() throws IOException {
        log.debug("Reading applications from {}", appsFile);
        var result = new LinkedList<Application>();
        try (java.io.Reader in = new FileReader(appsFile)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .get()
                    .parse(in);
            for (CSVRecord record : records) {
                var id = record.get(0);
                var name = record.get(1);
                var compName = record.get(2);
                var headers = record.getParser().getHeaderNames();
                var app = new Application(id, name, compName);
                for (int i = 3; i < 7; i++) {
                    app.setAttribute(headers.get(i), record.get(i));
                }
                result.add(app);
            }
        }
        log.debug("Reading applications: {} apps read", result.size());
        return result;
    }

    List<InformationFlow> readInformationFlows() throws IOException {
        log.debug("Reading information flows from {}", flowsFile);
        var result = new LinkedList<InformationFlow>();
        try (java.io.Reader in = new FileReader(flowsFile)) {
            var csvFormat = CSVFormat.DEFAULT;
            Iterable<CSVRecord> records = csvFormat.parse(in);
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
