import org.jetbrains.annotations.NotNull;

import java.util.SortedSet;
import java.util.TreeSet;

public class ProcessorLogger {

    private final StringBuilder stringBuilder = new StringBuilder();

    private int clockCycle = 1;

    public void onClockCycleFinished(@NotNull Processor processor) {
        appendLine("CC" + clockCycle + ":");
        appendLine("");
        logRegister(processor);
        appendLine("");
        logDataMemory(processor);
        appendLine("=================================================================");
        clockCycle++;
    }

    private void logRegister(@NotNull Processor processor) {
        SortedSet<Integer> sortedAddresses = new TreeSet<>(processor.getWrittenRegisterAddresses());
        appendLine("Registers:");
        sortedAddresses.forEach((address) -> appendLine("$" + address + ": " + processor.readRegister(address)));
    }

    private void logDataMemory(@NotNull Processor processor) {
        SortedSet<Integer> sortedAddresses = new TreeSet<>(processor.getWrittenDataMemoryAddresses());
        appendLine("Data memory:");
        sortedAddresses.forEach((address) ->
                appendLine(String.format("0x%02X: %d", address, processor.readDataMemory(address)))
        );
    }

    public String getLog() {
        return stringBuilder.toString();
    }

    private void appendLine(String str) {
        stringBuilder.append(str).append(System.lineSeparator());
    }
}