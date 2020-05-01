import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class ProcessorPrinter {

    private final Logger logger = Logger.getLogger(ProcessorPrinter.class.getName());

    private int clockCycle = 0;

    public void onClockCycleFinished(@NotNull Processor processor) {
        logger.info("clockCycle = " + clockCycle);
        clockCycle++;
    }
}
