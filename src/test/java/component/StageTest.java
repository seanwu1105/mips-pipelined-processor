package component;

import component.pipeline.InstructionFetch;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import signal.Instruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StageTest {

    @NotNull
    private Memory instructionMemory;

    @BeforeEach
    void buildUp() {
        instructionMemory = new Memory();
        instructionMemory.setMemoryWrite(MainController.MemoryWrite.TRUE);
    }

    @Test
    void testInstructionFetchRun() {
        int initProgramCounter = 0, number = 0;
        List<Instruction> instructions = List.of(
                new Instruction("10001101000000010000000000000011"),
                new Instruction("00000000000000100001100000100000")
        );

        for (Instruction instruction : instructions) {
            instructionMemory.setAddress(initProgramCounter + number * 4);
            instructionMemory.write(instruction);
            number++;
        }

        InstructionFetch instructionFetch = new InstructionFetch(instructionMemory);

        number = 0;
        for (Instruction instruction : instructions) {
            instructionFetch.run();
            assertEquals((number + 1) * 4, instructionFetch.getProgramCounter());
            assertEquals(instruction, instructionFetch.getInstruction());
            number++;
        }
    }

    @Test
    void testInstructionFetchRunOnBranch() {
    }
}
