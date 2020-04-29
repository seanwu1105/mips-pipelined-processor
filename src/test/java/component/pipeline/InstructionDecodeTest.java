package component.pipeline;

import component.Register;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import signal.Instruction;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstructionDecodeTest {

    Map<Integer, Integer> registerValues = Map.of(
            0, 10,
            1, 11,
            2, 12
    );
    @NotNull
    private Register register;

    @BeforeEach
    void buildUp() {
        register = new Register();
        register.setRegisterWrite(MainController.RegisterWrite.TRUE);
        registerValues.forEach((key, value) -> {
            register.setWriteAddress(key);
            register.write(value);
        });
    }

    @Test
    void testDecodeAdd() {
        int expectedProgramCounter = 0;
        Instruction instruction = new Instruction("000000 00000 00001 00010 00000 100000"); // add $0 $1 $2
        InstructionFetchToInstructionDecodeRegister ifId = mock(InstructionFetchToInstructionDecodeRegister.class);
        when(ifId.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(ifId.getInstruction()).thenReturn(instruction);

        InstructionDecode instructionDecode = new InstructionDecode(ifId, new MainController(), register);
        instructionDecode.run();

        assertEquals(MainController.RegisterDestination.RD, instructionDecode.getRegisterDestination());
        assertEquals(MainController.AluOperation.R_TYPE, instructionDecode.getAluOperation());
        assertEquals(MainController.AluSource.REGISTER, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.FALSE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.FALSE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.TRUE, instructionDecode.getRegisterWrite());
        assertEquals(MainController.MemoryToRegister.FROM_ALU_RESULT, instructionDecode.getMemoryToRegister());
        assertEquals(expectedProgramCounter, instructionDecode.getProgramCounter());
        assertEquals(registerValues.get(0), instructionDecode.getRegisterData1());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData2());
        assertEquals(2, instructionDecode.getRd());
    }

    @Test
    void testDecodeSubtract() {
    }

    @Test
    void testDecodeAnd() {
    }

    @Test
    void testDecodeOr() {
    }

    @Test
    void testDecodeSetOnLessThan() {
    }

    @Test
    void testDecodeLoadWord() {
    }

    @Test
    void testDecodeSaveWord() {
    }

    @Test
    void testDecodeBranchOnEqual() {
    }

    @AfterEach
    void tearDown() {
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
    }
}