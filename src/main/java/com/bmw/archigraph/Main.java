package com.bmw.archigraph;

import ch.qos.logback.classic.Level;
import com.bmw.archigraph.draw.DrawModelDrawIO;
import com.bmw.archigraph.render.RenderModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import com.bmw.archigraph.read.Reader;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Slf4j
public class Main {

    private static void usage(Options options) {
        var formatter = new HelpFormatter();
        formatter.printHelp("archi-graph-j [options] component-model-file", options);
        System.exit(1);
    }

    private static Options defineOptions() {
        var options = new Options();
        options.addOption("d", "debug", false, "Turn on debug output");
        options.addOption("a", "apps", true, "The applications file");
        options.addOption("f", "flows", true, "The information flows file");
        options.addOption("h", "help", false, "Show help");
        return options;
    }

    private static String buildOutputFileName(String compFileName) {
        return compFileName.replaceFirst("\\.json$", ".drawio.xml");
    }

    public static void main(String[] args) {
        var parser = new DefaultParser();
        var options = defineOptions();
        try {
            var cmdLine = parser.parse(options, args);
            var cmdLineArgs = cmdLine.getArgs();
            if (cmdLineArgs.length == 0) {
                System.err.println("Component model file is missing");
                usage(options);
            }
            if (cmdLine.hasOption("h")) {
                usage(options);
            }
            var compFile = cmdLineArgs[0];
            var appsFile = cmdLine.getOptionValue("a");
            var flowsFile = cmdLine.getOptionValue("f");
            if (cmdLine.hasOption("d")) {
                ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("com.bmw.archigraph");
                root.setLevel(Level.DEBUG);
            }
            var reader = new Reader(compFile, appsFile, flowsFile);
            var outputFile = buildOutputFileName(compFile);
            var model = reader.readModels();
            var renderModel = new RenderModel().render(model);
            new DrawModelDrawIO().draw(renderModel).write(outputFile);
        } catch (ParseException pe) {
            System.err.println("Could not parse command line: " + pe.getMessage());
            usage(options);
        } catch (IOException ioe) {
            System.err.println("Error reading or writing:" + ioe.getMessage());
            System.exit(1);
        }

    }
}
