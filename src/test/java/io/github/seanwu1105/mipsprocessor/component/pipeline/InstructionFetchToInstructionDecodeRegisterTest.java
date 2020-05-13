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
    private InstructionDecode instructionDecode;
    @NotNull
    private HazardDetectionUnit hazardDetectionUnit;
    @NotNull
    private InstructionFetchToInstructionDecodeRegister ifId;

    @BeforeEach
    void buildUp() {
        instructionFetch = mock(InstructionFetch.class);
        instructionDecode = mock(InstructionDecode.class);
        hazardDetectionUnit = mock(HazardDetectionUnit.class);

        ifId = new InstructionFetchToInstructionDecodeRegister(instructionFetch);
        ifId.setInstructionDecode(instructionDecode);
        ifId.setHazardDetectionUnit(hazardDetectionUnit);
        when(instructionDecode.shouldBranch()).thenReturn(false);
        when(hazardDetectionUnit.mustStall()).thenReturn(false);
    }

    @Test
    void testUpdate() {
        final var expectedProgramCounter = 8;
        final var expectedInstruction = new Instruction("00000000001000010000100000100000");

        when(instructionFetch.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(expectedInstruction);

        when(hazardDetectionUnit.mustStall()).thenReturn(false);

        ifId.setHazardDetectionUnit(hazardDetectionUnit);
        ifId.update();

        assertEquals(expectedProgramCounter, ifId.getProgramCounter());
        assertEquals(expectedInstruction, ifId.getInstruction());
    }

    @Test
    void testUpdateWhenStalling() {
        final var firstProgramCounter = 8;
        final var firstInstruction = new Instruction("00000000001000010000100000100000");
        final var secondProgramCounter = 12;
        final var secondInstruction = new Instruction("00000000011000010000100000100000");

        when(instructionFetch.getProgramCounter()).thenReturn(firstProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(firstInstruction);
        when(hazardDetectionUnit.mustStall()).thenReturn(false);
        ifId.update();
        when(instructionFetch.getProgramCounter()).thenReturn(secondProgramCounter);
        when(instructionFetch.getInstruction()).thenReturn(secondInstruction);
        when(hazardDetectionUnit.mustStall()).thenReturn(true);
        ifId.update();

        assertEquals(firstInstruction, ifId.getInstruction());
    }

    @Test
    void testUpdateWhenTakingBranch() {
        final var notNop = new Instruction("00000000001000010000100000100000");

        when(instructionFetch.getInstruction()).thenReturn(notNop);
        when(instructionDecode.shouldBranch()).thenReturn(true);

        ifId.update();

        assertEquals(Instruction.NOP, ifId.getInstruction());
    }
}