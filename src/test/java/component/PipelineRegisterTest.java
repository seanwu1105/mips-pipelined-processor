package component;

import component.pipeline.InstructionFetch;
import component.pipeline.InstructionFetchToInstructionDecodeRegister;
import org.junit.jupiter.api.Test;
import signal.Instruction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PipelineRegisterTest {

    @Test
    void testGetInstructionFetchToInstructionDecodeRegisterProperties() {
        int expectedProgramCounter = 8;
        Instruction expectedInstruction = new Instruction("00000000000000000000000000000000");

        InstructionFetch instructionFetch = mock(InstructionFetch.class);
        when(instructionFetch.getNewProgramCounter()).thenReturn(expectedProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(expectedInstruction);

        InstructionFetchToInstructionDecodeRegister ifId = new InstructionFetchToInstructionDecodeRegister(instructionFetch);
        ifId.update();
        assertEquals(expectedProgramCounter, ifId.getNewProgramCounter());
        assertEquals(expectedInstruction, ifId.getInstruction());
    }

    @Test
    void testGetInstructionDecodeToExecutionRegisterProperties() {
    }

    @Test
    void testGetExecutionToMemoryAccessRegisterProperties() {
    }

    @Test
    void testGetMemoryAccessToWriteBackRegisterProperties() {
    }
}