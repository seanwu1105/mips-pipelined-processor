package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.component.Alu;
import io.github.seanwu1105.mipsprocessor.component.HazardDetectionUnit;
import io.github.seanwu1105.mipsprocessor.component.Register;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.FunctionCode;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstructionDecodeTest {

    @NotNull
    private final Map<Integer, Integer> registerValues = Map.of(
            0, 0,
            1, 11,
            2, 12,
            3, 13
    );
    private final int expectedProgramCounter = 4;
    @NotNull
    private InstructionFetchToInstructionDecodeRegister ifId;
    @NotNull
    private HazardDetectionUnit hazardDetectionUnit;
    @NotNull
    private InstructionDecode instructionDecode;
    @NotNull
    private Register register;

    @BeforeEach
    void buildUp() {
        ifId = mock(InstructionFetchToInstructionDecodeRegister.class);
        hazardDetectionUnit = mock(HazardDetectionUnit.class);

        register = new Register();
        register.setRegisterWrite(MainController.RegisterWrite.TRUE);
        registerValues.forEach((key, value) -> {
            if (key != 0) {
                register.setWriteAddress(key);
                register.write(value);
            }
        });

        instructionDecode = new InstructionDecode(ifId, new MainController(), register, new Alu());
        instructionDecode.setHazardDetectionUnit(hazardDetectionUnit);

        when(hazardDetectionUnit.mustStall()).thenReturn(false);
        when(ifId.getProgramCounter()).thenReturn(expectedProgramCounter);
    }

    @Test
    void testDecodeRType() {
        final var instruction = new Instruction("000000 00000 00001 00010 00000 100000"); // add $2, $0, $1
        when(ifId.getInstruction()).thenReturn(instruction);

        instructionDecode.run();

        assertEquals(MainController.RegisterDestination.RD, instructionDecode.getRegisterDestination());
        assertEquals(MainController.AluOperation.R_TYPE, instructionDecode.getAluOperation());
        assertEquals(MainController.AluSource.REGISTER, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.FALSE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.FALSE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.TRUE, instructionDecode.getRegisterWrite());
        assertEquals(MainController.MemoryToRegister.FROM_ALU_RESULT, instructionDecode.getMemoryToRegister());
        assertEquals(registerValues.get(0), instructionDecode.getRegisterData1());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData2());
        assertEquals(FunctionCode.ADD, instructionDecode.getFunctionCode());
        assertEquals(2, instructionDecode.getRd());
    }

    @Test
    void testDecodeLoadWord() {
        final var instruction = new Instruction("100011 00001 00010 0000000000010100"); // lw $2, 20($1)
        when(ifId.getInstruction()).thenReturn(instruction);

        instructionDecode.run();

        assertEquals(MainController.RegisterDestination.RT, instructionDecode.getRegisterDestination());
        assertEquals(MainController.AluOperation.MEMORY_REFERENCE, instructionDecode.getAluOperation());
        assertEquals(MainController.AluSource.IMMEDIATE, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.FALSE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.TRUE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.TRUE, instructionDecode.getRegisterWrite());
        assertEquals(MainController.MemoryToRegister.FROM_MEMORY, instructionDecode.getMemoryToRegister());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData1());
        assertEquals(20, instructionDecode.getImmediate());
        assertEquals(2, instructionDecode.getRt());
    }

    @Test
    void testDecodeSaveWord() {
        final var instruction = new Instruction("101011 00001 00010 0000000000010100"); // sw $2, 20($1)
        when(ifId.getInstruction()).thenReturn(instruction);

        instructionDecode.run();

        assertEquals(MainController.AluOperation.MEMORY_REFERENCE, instructionDecode.getAluOperation());
        assertEquals(MainController.AluSource.IMMEDIATE, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.FALSE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.FALSE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.TRUE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.FALSE, instructionDecode.getRegisterWrite());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData1());
        assertEquals(registerValues.get(2), instructionDecode.getRegisterData2());
        assertEquals(20, instructionDecode.getImmediate());
    }

    @Test
    void testDecodeBranchOnEqualFalse() {
        final var instruction = new Instruction("000100 00001 00010 0000000000010100"); // beq $1, $2, 20
        when(ifId.getInstruction()).thenReturn(instruction);

        instructionDecode.run();

        assertEquals(MainController.AluSource.REGISTER, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.TRUE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.FALSE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.FALSE, instructionDecode.getRegisterWrite());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData1());
        assertEquals(registerValues.get(2), instructionDecode.getRegisterData2());
        assertEquals(20, instructionDecode.getImmediate());

        assertEquals(MainController.AluOperation.BRANCH, instructionDecode.getAluOperation());
        assertFalse(instructionDecode.shouldBranch());
        assertEquals(expectedProgramCounter + 20 * 4, instructionDecode.getBranchAdderResult());
    }

    @Test
    void testDecodeBranchOnEqualTrue() {
        final var instruction = new Instruction("000100 00001 00001 0000000000010100"); // beq $1, $1, 20
        when(ifId.getInstruction()).thenReturn(instruction);

        instructionDecode.run();

        assertEquals(MainController.AluSource.REGISTER, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.TRUE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.FALSE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.FALSE, instructionDecode.getRegisterWrite());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData1());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData2());
        assertEquals(20, instructionDecode.getImmediate());

        assertEquals(MainController.AluOperation.BRANCH, instructionDecode.getAluOperation());
        assertTrue(instructionDecode.shouldBranch());
        assertEquals(expectedProgramCounter + 20 * 4, instructionDecode.getBranchAdderResult());
    }

    @Test
    void testStalling() {
        final var instruction = new Instruction("000000 00001 00000 00000 00000 100000"); // add $1, $0, $0
        when(ifId.getInstruction()).thenReturn(instruction);
        when(hazardDetectionUnit.mustStall()).thenReturn(true);

        instructionDecode.run();

        assertEquals(MainController.RegisterDestination.RT, instructionDecode.getRegisterDestination());
        assertEquals(MainController.AluOperation.MEMORY_REFERENCE, instructionDecode.getAluOperation());
        assertEquals(MainController.AluSource.REGISTER, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.FALSE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.FALSE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.FALSE, instructionDecode.getRegisterWrite());
        assertEquals(MainController.MemoryToRegister.FROM_ALU_RESULT, instructionDecode.getMemoryToRegister());
    }

    @AfterEach
    void tearDown() {
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
    }
}