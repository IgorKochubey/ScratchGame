package com.scratchgame.service;

import lombok.Getter;
import org.apache.commons.cli.*;

@Getter
public class CommandLineService {
    private String configFilePath;
    private int bettingAmount;

    public void parse(String[] args) throws ParseException {
        // Define the command line options
        Options options = new Options();

        Option configOption = new Option("c", "config", true, "Configuration file path");
        configOption.setRequired(true);
        options.addOption(configOption);

        Option bettingAmountOption = new Option("b", "betting-amount", true, "Betting amount");
        bettingAmountOption.setRequired(true);
        options.addOption(bettingAmountOption);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            this.configFilePath = cmd.getOptionValue("config");

            String bettingAmountValue = cmd.getOptionValue("betting-amount");
            this.bettingAmount = Integer.parseInt(bettingAmountValue);
        } catch (ParseException ex) {
            System.out.println(ex.getMessage());
            formatter.printHelp("java -jar <your-jar-file>", options);
            throw ex;
        }
    }
}
