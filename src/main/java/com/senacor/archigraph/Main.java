package com.senacor.archigraph;

import ch.qos.logback.classic.Level;
import com.senacor.archigraph.draw.drawio.DrawModelImpl;
import com.senacor.archigraph.model.Model;
import com.senacor.archigraph.render.RenderModel;
import com.senacor.archigraph.validate.LayoutValidator;
import com.senacor.archigraph.validate.SemanticValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

import com.senacor.archigraph.read.Reader;
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
        options.addOption("t", "trace", false, "Turn on trace output");
        options.addOption("a", "apps", true, "The applications file");
        options.addOption("f", "flows", true, "The information flows file");
        options.addOption("r", "rules", true, "The formatting rules file");
        options.addOption("o", "output", true, "Output file name.");
        options.addOption("h", "help", false, "Show help");
        options.addOption("lc", "lenient-comp", false, "Missing components do not cause failure.");
        options.addOption("lf", "lenient-flow", false, "Missing apps in flows do not cause failure.");
        options.addOption("x", "validateOnly", false, "Exit after validation");
        options.addOption("X", "continueWithFailure", false, "Continue even when validation fails");
        return options;
    }

    private static String buildOutputFileName(String compFileName) {
        return compFileName.replaceFirst("\\.json$", ".drawio.xml");
    }

    static void main(String[] args) {
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
            var outputFile = cmdLine.getOptionValue("o");
            var rulesFile = cmdLine.getOptionValue("r");
            var lenientComp = cmdLine.hasOption("lenient-comp");
            var lenientFlow = cmdLine.hasOption("lenient-flow");
            var exitAfterValidate = cmdLine.hasOption("x");
            var exitAfterFailure = !cmdLine.hasOption("X");
            ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger("com.bmw.archigraph");
            if (cmdLine.hasOption("d") || cmdLine.hasOption("t")) {
                if (cmdLine.hasOption("t")) {
                    root.setLevel(Level.TRACE);
                } else {
                    root.setLevel(Level.DEBUG);
                }
            } else {
                root.setLevel(Level.INFO);
            }
            var reader = new Reader(compFile, appsFile, flowsFile, rulesFile);
            if (outputFile == null) {
                outputFile = buildOutputFileName(compFile);
            }
            var model = reader.readModels();
            validate(lenientComp, lenientFlow, exitAfterValidate, exitAfterFailure, model);
            var renderModel = new RenderModel();
            renderModel.setRuleBase(reader.getRuleBase());
            renderModel.render(model);
            new DrawModelImpl().draw(renderModel).write(outputFile);
        } catch (ParseException pe) {
            System.err.println("Could not parse command line: " + pe.getMessage());
            usage(options);
        } catch (IOException ioe) {
            System.err.println("Error reading or writing:" + ioe.getMessage());
            System.exit(1);
        }
    }

    private static void validate(boolean lenientComp, boolean lenientFlow, boolean exitAfterValidate,
                                 boolean exitAfterFailure, Model model) {
        var issues = new LayoutValidator().validate(model);
        issues.addAll(new SemanticValidator().validate(model, lenientComp, lenientFlow));
        for (var i : issues) {
            log.error(i.description());
            System.err.println(i.description());
        }
        if (exitAfterValidate || (!issues.isEmpty() && exitAfterFailure)) {
            System.exit(issues.isEmpty() ? 0 : 1);
        }
    }
}
