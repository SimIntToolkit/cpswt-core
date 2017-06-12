package c2w.hla;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Parser for Federate parameters
 */
public class FederateParameterParser {
    Options cliOptions;

    /**
     * Default constructor
     */
    public FederateParameterParser() {
        this.cliOptions = new Options();
    }

    /**
     * Instantiate a parser with pre-defined CLI options.
     * @param cliOptions
     */
    public FederateParameterParser(Options cliOptions) {
        this.cliOptions = cliOptions;
    }

    /**
     * Add more CLI options to the existing ones.
     * @param options
     */
    public void addCLIOptions(Options options) {
        for(Option opt : options.getOptions()) {
            this.cliOptions.addOption(opt);
        }
    }

    // public <T extends FederateParameter> parseArgs(final String[] args, Class<T> type) {
    //    return type.cast()
    //}
}
