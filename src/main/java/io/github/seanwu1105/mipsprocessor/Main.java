package io.github.seanwu1105.mipsprocessor;

import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

class Main {

    public static void main() {
        @NotNull final var instructions = List.of(
                new Instruction("000101 01001 01000 0000000000000001"),   // bne $9, $8, 2
                new Instruction("000000 00001 00010 00011 00000 100000"), // add $3, $1, $2
                new Instruction("000000 00011 00100 00101 00000 100010"), // add $4, $6, $7
                new Instruction("000000 00011 00100 00101 00000 100010")  // sub $5, $3, $4
        );
        @NotNull final var initRegisterValues = Map.of(
                0, 0,
                1, 9,
                2, 8,
                3, 7,
                4, 1,
                5, 2,
                6, 3,
                7, 4,
                8, 5,
                9, 6
        );
        @NotNull final var initDataMemoryValues = Map.of(
                0x00, 5,
                0x04, 9,
                0x08, 4,
                0x0C, 8,
                0x10, 7
        );

        // Create a MIPS pipelined processor with the given instructions, initial register values and data memory values.
        @NotNull final var processor = new Processor.Builder()
                .setInstructions(instructions)
                .setRegisterValues(initRegisterValues)
                .setDataMemoryValues(initDataMemoryValues)
                .build();

        // Create and add a logger to log the variables in the processor.
        @NotNull final var logger = new ProcessorLogger();
        processor.addLogger(logger);

        // Start the pipeline.
        processor.run();

        // Get and print the log.
        logger.getLog();
    }
}
