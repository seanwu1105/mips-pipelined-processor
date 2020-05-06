package component.pipeline;

import component.Register;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import signal.FunctionCode;
import signal.Instruction;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InstructionDecodeTest {

    private final int expectedProgramCounter = 0;
    private final Map<Integer, Integer> registerValues = Map.of(
            0, 0,
            1, 11,
            2, 12,
            3, 13
    );
    @NotNull
    private Register register;

    @BeforeEach
    void buildUp() {
        register = new Register();
        register.setRegisterWrite(MainController.RegisterWrite.TRUE);
        registerValues.forEach((key, value) -> {
            if (key != 0) {
                register.setWriteAddress(key);
                register.write(value);
            }
        });
    }

    @Test
    void testDecodeRType() {
        final Instruction instruction = new Instruction("000000 00000 00001 00010 00000 100000"); // add $2, $0, $1
        final InstructionFetchToInstructionDecodeRegister ifId = mock(InstructionFetchToInstructionDecodeRegister.class);
        when(ifId.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(ifId.getInstruction()).thenReturn(instruction);

        final InstructionDecode instructionDecode = new InstructionDecode(ifId, new MainController(), register);
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
        assertEquals(FunctionCode.ADD, instructionDecode.getFunctionCode());
        assertEquals(2, instructionDecode.getRd());
    }

    @Test
    void testDecodeLoadWord() {
        final Instruction instruction = new Instruction("100011 00001 00010 0000000000010100"); // lw $2, 20($1)
        final InstructionFetchToInstructionDecodeRegister ifId = mock(InstructionFetchToInstructionDecodeRegister.class);
        when(ifId.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(ifId.getInstruction()).thenReturn(instruction);

        final InstructionDecode instructionDecode = new InstructionDecode(ifId, new MainController(), register);
        instructionDecode.run();

        assertEquals(MainController.RegisterDestination.RT, instructionDecode.getRegisterDestination());
        assertEquals(MainController.AluOperation.MEMORY_REFERENCE, instructionDecode.getAluOperation());
        assertEquals(MainController.AluSource.IMMEDIATE, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.FALSE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.TRUE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.TRUE, instructionDecode.getRegisterWrite());
        assertEquals(MainController.MemoryToRegister.FROM_MEMORY, instructionDecode.getMemoryToRegister());
        assertEquals(expectedProgramCounter, instructionDecode.getProgramCounter());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData1());
        assertEquals(20, instructionDecode.getImmediate());
        assertEquals(2, instructionDecode.getRt());
    }

    @Test
    void testDecodeSaveWord() {
        final Instruction instruction = new Instruction("101011 00001 00010 0000000000010100"); // sw $2, 20($1)
        final InstructionFetchToInstructionDecodeRegister ifId = mock(InstructionFetchToInstructionDecodeRegister.class);
        when(ifId.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(ifId.getInstruction()).thenReturn(instruction);

        final InstructionDecode instructionDecode = new InstructionDecode(ifId, new MainController(), register);
        instructionDecode.run();

        assertEquals(MainController.AluOperation.MEMORY_REFERENCE, instructionDecode.getAluOperation());
        assertEquals(MainController.AluSource.IMMEDIATE, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.FALSE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.FALSE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.TRUE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.FALSE, instructionDecode.getRegisterWrite());
        assertEquals(expectedProgramCounter, instructionDecode.getProgramCounter());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData1());
        assertEquals(registerValues.get(2), instructionDecode.getRegisterData2());
        assertEquals(20, instructionDecode.getImmediate());
    }

    @Test
    void testDecodeBranchOnEqual() {
        final Instruction instruction = new Instruction("000100 00001 00010 0000000000010100"); // beq $1, $2, 20
        final InstructionFetchToInstructionDecodeRegister ifId = mock(InstructionFetchToInstructionDecodeRegister.class);
        when(ifId.getProgramCounter()).thenReturn(expectedProgramCounter);
        when(ifId.getInstruction()).thenReturn(instruction);

        final InstructionDecode instructionDecode = new InstructionDecode(ifId, new MainController(), register);
        instructionDecode.run();

        assertEquals(MainController.AluOperation.BRANCH, instructionDecode.getAluOperation());
        assertEquals(MainController.AluSource.REGISTER, instructionDecode.getAluSource());
        assertEquals(MainController.Branch.TRUE, instructionDecode.getBranch());
        assertEquals(MainController.MemoryRead.FALSE, instructionDecode.getMemoryRead());
        assertEquals(MainController.MemoryWrite.FALSE, instructionDecode.getMemoryWrite());
        assertEquals(MainController.RegisterWrite.FALSE, instructionDecode.getRegisterWrite());
        assertEquals(expectedProgramCounter, instructionDecode.getProgramCounter());
        assertEquals(registerValues.get(1), instructionDecode.getRegisterData1());
        assertEquals(registerValues.get(2), instructionDecode.getRegisterData2());
        assertEquals(20, instructionDecode.getImmediate());
    }

    @AfterEach
    void tearDown() {
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
    }
}