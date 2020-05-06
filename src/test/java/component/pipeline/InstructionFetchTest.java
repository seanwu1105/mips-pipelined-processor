package component.pipeline;

import component.Memory;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import signal.Instruction;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstructionFetchTest {

    @NotNull
    private Memory instructionMemory;

    @BeforeEach
    void buildUp() {
        instructionMemory = new Memory();
        instructionMemory.setMemoryWrite(MainController.MemoryWrite.TRUE);
    }

    @Test
    void testInstructionFetchRun() {
        final List<Instruction> instructions = List.of(
                new Instruction("10001101000000010000000000000011"),
                new Instruction("00000000000000100001100000100000")
        );

        setInstructions(instructions);

        final InstructionFetch instructionFetch = new InstructionFetch(instructionMemory);

        int number = 0;
        for (final Instruction instruction : instructions) {
            instructionFetch.run();
            assertEquals((number + 1) * 4, instructionFetch.getProgramCounter());
            assertEquals(instruction, instructionFetch.getInstruction());
            number++;
        }
    }

    @Test
    void testInstructionFetchRunOnBranch() {
        final List<Instruction> instructions = List.of(
                new Instruction("00010000010000100000000000000001"),
                new Instruction("00000000000000100001100000100000"),
                new Instruction("00010000000000100000000000000110"),
                new Instruction("00000000000000100001100000100000")
        );

        setInstructions(instructions);

        // XXX: This TestCase violates encapsulation of InstructionFetch.
        final ExecutionToMemoryAccessRegister exeMem = mock(ExecutionToMemoryAccessRegister.class);

        final InstructionFetch instructionFetch = new InstructionFetch(instructionMemory);
        instructionFetch.setExecutionToMemoryAccessRegister(exeMem);

        final int expectedBranchResult = 8;
        when(exeMem.shouldBranch()).thenReturn(true);
        when(exeMem.getBranchResult()).thenReturn(expectedBranchResult);
        instructionFetch.run();
        assertEquals(expectedBranchResult, instructionFetch.getProgramCounter());
        assertEquals(instructions.get(0), instructionFetch.getInstruction());

        when(exeMem.shouldBranch()).thenReturn(false);
        instructionFetch.run();
        assertEquals(expectedBranchResult + 4, instructionFetch.getProgramCounter());
        assertEquals(instructions.get(expectedBranchResult / 4), instructionFetch.getInstruction());
    }

    private void setInstructions(@NotNull final List<Instruction> instructions) {
        final int initProgramCounter = 0;
        int number = 0;
        for (final Instruction instruction : instructions) {
            instructionMemory.setAddress(initProgramCounter + number * 4);
            instructionMemory.write(instruction);
            number++;
        }
    }

    @AfterEach
    void tearDown() {
        instructionMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);
    }
}
