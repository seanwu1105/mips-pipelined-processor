package io.github.seanwu1105.mipsprocessor.controller;

import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.seanwu1105.mipsprocessor.controller.MainController.AluOperation.BRANCH;
import static io.github.seanwu1105.mipsprocessor.controller.MainController.AluOperation.MEMORY_REFERENCE;
import static io.github.seanwu1105.mipsprocessor.controller.MainController.AluOperation.R_TYPE;
import static io.github.seanwu1105.mipsprocessor.controller.MainController.AluSource.IMMEDIATE;
import static io.github.seanwu1105.mipsprocessor.controller.MainController.AluSource.REGISTER;
import static io.github.seanwu1105.mipsprocessor.controller.MainController.MemoryToRegister.FROM_ALU_RESULT;
import static io.github.seanwu1105.mipsprocessor.controller.MainController.MemoryToRegister.FROM_MEMORY;
import static io.github.seanwu1105.mipsprocessor.controller.MainController.RegisterDestination.RD;
import static io.github.seanwu1105.mipsprocessor.controller.MainController.RegisterDestination.RT;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MainControllerTest {

    @NotNull
    private final Instruction addSignal = new Instruction("000000 00000000000000000000 100000");
    @NotNull
    private final Instruction loadWordSignal = new Instruction("100011 00000000000000000000 000000");
    @NotNull
    private final Instruction saveWordSignal = new Instruction("101011 00000000000000000000 000000");
    @NotNull
    private final Instruction branchOnEqualSignal = new Instruction("000100 00000000000000000000 000000");
    @NotNull
    private MainController mainController;

    @BeforeEach
    void buildUp() {
        mainController = new MainController();
    }

    @Test
    void testGetSignalsWithRType() {
        mainController.setInstruction(addSignal);
        assertEquals(R_TYPE, mainController.getAluOperation());
        assertEquals(REGISTER, mainController.getAluSource());
        assertEquals(MainController.MemoryRead.FALSE, mainController.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, mainController.getMemoryWrite());
        assertEquals(FROM_ALU_RESULT, mainController.getMemoryToRegister());
        assertEquals(RD, mainController.getRegisterDestination());
        assertEquals(MainController.RegisterWrite.TRUE, mainController.getRegisterWrite());
        assertEquals(MainController.Branch.FALSE, mainController.getBranch());

        mainController.setInstruction(saveWordSignal);
        assertEquals(MEMORY_REFERENCE, mainController.getAluOperation());
        mainController.setInstruction(branchOnEqualSignal);
        assertEquals(BRANCH, mainController.getAluOperation());
    }

    @Test
    void testGetSignalsWithLoadWord() {
        mainController.setInstruction(loadWordSignal);
        assertEquals(MEMORY_REFERENCE, mainController.getAluOperation());
        assertEquals(IMMEDIATE, mainController.getAluSource());
        assertEquals(MainController.MemoryRead.TRUE, mainController.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, mainController.getMemoryWrite());
        assertEquals(FROM_MEMORY, mainController.getMemoryToRegister());
        assertEquals(RT, mainController.getRegisterDestination());
        assertEquals(MainController.RegisterWrite.TRUE, mainController.getRegisterWrite());
        assertEquals(MainController.Branch.FALSE, mainController.getBranch());

        mainController.setInstruction(saveWordSignal);
        assertEquals(IMMEDIATE, mainController.getAluSource());
        mainController.setInstruction(branchOnEqualSignal);
        assertEquals(REGISTER, mainController.getAluSource());
    }

    @Test
    void testGetSignalsWithSaveWord() {
        mainController.setInstruction(saveWordSignal);
        assertEquals(MainController.MemoryRead.FALSE, mainController.getMemoryRead());
        assertEquals(MainController.MemoryWrite.TRUE, mainController.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.FALSE, mainController.getRegisterWrite());
        assertEquals(MainController.Branch.FALSE, mainController.getBranch());
    }

    @Test
    void testGetSignalsWithBranchOnEqual() {
        mainController.setInstruction(branchOnEqualSignal);
        assertEquals(MainController.MemoryRead.FALSE, mainController.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, mainController.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.FALSE, mainController.getRegisterWrite());
        assertEquals(MainController.Branch.TRUE, mainController.getBranch());
    }

    @Test
    void testGetSignalsWithNop() {
        mainController.setInstruction(Instruction.NOP);
        assertEquals(RT, mainController.getRegisterDestination());
        assertEquals(MEMORY_REFERENCE, mainController.getAluOperation());
        assertEquals(REGISTER, mainController.getAluSource());
        assertEquals(MainController.Branch.FALSE, mainController.getBranch());
        assertEquals(MainController.MemoryRead.FALSE, mainController.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, mainController.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.FALSE, mainController.getRegisterWrite());
        assertEquals(FROM_ALU_RESULT, mainController.getMemoryToRegister());
    }
}