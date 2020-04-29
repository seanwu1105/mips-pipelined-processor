package component;

import component.pipeline.ExecutionToMemoryAccessRegister;
import component.pipeline.InstructionFetch;
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
        int initProgramCounter = 0, number = 0;
        List<Instruction> instructions = List.of(
                new Instruction("00010000010000100000000000000001"),
                new Instruction("00000000000000100001100000100000"),
                new Instruction("00010000000000100000000000000110"),
                new Instruction("00000000000000100001100000100000")
        );

        for (Instruction instruction : instructions) {
            instructionMemory.setAddress(initProgramCounter + number * 4);
            instructionMemory.write(instruction);
            number++;
        }

        // XXX: This TestCase violates encapsulation of InstructionFetch.
        ExecutionToMemoryAccessRegister exeMem = mock(ExecutionToMemoryAccessRegister.class);

        InstructionFetch instructionFetch = new InstructionFetch(instructionMemory);
        instructionFetch.setExecutionToMemoryAccessRegister(exeMem);

        when(exeMem.getBranch()).thenReturn(MainController.Branch.TRUE);
        when(exeMem.getBranchResult()).thenReturn(8);
        instructionFetch.run();
        assertEquals(8, instructionFetch.getProgramCounter());
        assertEquals(instructions.get(0), instructionFetch.getInstruction());

        when(exeMem.getBranch()).thenReturn(MainController.Branch.FALSE);
        instructionFetch.run();
        assertEquals(12, instructionFetch.getProgramCounter());
        assertEquals(instructions.get(2), instructionFetch.getInstruction());
    }

    @AfterEach
    void tearDown() {
        instructionMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);
    }
}
