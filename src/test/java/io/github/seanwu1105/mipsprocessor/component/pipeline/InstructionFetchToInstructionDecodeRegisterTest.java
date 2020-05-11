package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.component.HazardDetectionUnit;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstructionFetchToInstructionDecodeRegisterTest {

    @NotNull
    private InstructionFetch instructionFetch;
    @NotNull
    private HazardDetectionUnit hazardDetectionUnit;
    @NotNull
    private InstructionFetchToInstructionDecodeRegister ifId;

    @BeforeEach
    void buildUp() {
        instructionFetch = mock(InstructionFetch.class);
        hazardDetectionUnit = mock(HazardDetectionUnit.class);

        ifId = new InstructionFetchToInstructionDecodeRegister(instructionFetch);
        ifId.setHazardDetectionUnit(hazardDetectionUnit);
        when(hazardDetectionUnit.needStalling()).thenReturn(false);
    }

    @Test
    void testGetInstructionFetchToInstructionDecodeRegisterProperties() {
        final var expectedProgramCounter = 8;
        final var expectedInstruction = new Instruction("00000000001000010000100000100000");

        when(instructionFetch.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(expectedInstruction);

        when(hazardDetectionUnit.needStalling()).thenReturn(false);

        ifId.setHazardDetectionUnit(hazardDetectionUnit);
        ifId.update();

        assertEquals(expectedProgramCounter, ifId.getProgramCounter());
        assertEquals(expectedInstruction, ifId.getInstruction());
    }

    @Test
    void testStallInstructionFetchToInstructionDecodeRegisterUpdate() {
        final var firstProgramCounter = 8;
        final var firstInstruction = new Instruction("00000000001000010000100000100000");
        final var secondProgramCounter = 12;
        final var secondInstruction = new Instruction("00000000011000010000100000100000");

        ifId.setHazardDetectionUnit(hazardDetectionUnit);

        when(instructionFetch.getProgramCounter()).thenReturn(firstProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(firstInstruction);
        when(hazardDetectionUnit.needStalling()).thenReturn(false);
        ifId.update();
        when(instructionFetch.getProgramCounter()).thenReturn(secondProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(secondInstruction);
        when(hazardDetectionUnit.needStalling()).thenReturn(true);
        ifId.update();

        assertEquals(firstInstruction, ifId.getInstruction());
    }
}