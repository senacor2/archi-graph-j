package com.bmw.archigraph;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import com.bmw.archigraph.read.Reader;

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

                // TODO set global debug level
            }
            var reader = new Reader(compFile, appsFile, flowsFile);
            var outputFile = buildOutputFileName(compFile);
            reader.readModels()
                    .render()
                    .draw()
                    .write(outputFile);
        } catch (ParseException pe) {
            System.err.println("Could not parse command line: " + pe.getMessage());
            usage(options);
        } catch (IOException ioe) {
            System.err.println("Error reading or writing:" + ioe.getMessage());
            System.exit(1);
        }

    }
}
