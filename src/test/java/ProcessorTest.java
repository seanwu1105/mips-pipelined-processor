import component.Memory;
import component.Register;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import signal.Instruction;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorTest {

    private final Map<Integer, Integer> registerValues = Map.of(
            0, 0,
            1, 11,
            2, 12,
            3, 13,
            4, 14,
            5, 15,
            6, 16,
            7, 17,
            8, 18,
            9, 19
    );
    private final Map<Integer, Integer> dataMemoryValues = Map.of(
            0x00, 101,
            0x04, 104,
            0x08, 108,
            0x0C, 112,
            0x10, 116
    );
    @NotNull
    private Processor.Builder processorBuilder;
    private Register register;
    private Memory dataMemory;

    @BeforeEach
    void buildUp() {
        initializeRegister();
        initializeDataMemory();
        processorBuilder = new Processor.Builder()
                .setRegister(register)
                .setDataMemory(dataMemory);
    }

    private void initializeRegister() {
        register = new Register();
        register.setRegisterWrite(MainController.RegisterWrite.TRUE);
        registerValues.forEach((key, value) -> {
            register.setWriteAddress(key);
            register.write(value);
        });
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
    }

    private void initializeDataMemory() {
        dataMemory = new Memory();
        dataMemory.setMemoryWrite(MainController.MemoryWrite.TRUE);
        dataMemoryValues.forEach((key, value) -> {
            dataMemory.setAddress(key);
            dataMemory.write(value);
        });
        dataMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);
    }

    @Test
    void testAdd() {
        List<Instruction> instructions = List.of(
                new Instruction("000000 00001 00010 00011 00000 100000") // add $3, $1, $2
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(3);
        assertEquals(registerValues.get(1) + registerValues.get(2), register.readData1());
    }

    @Test
    void testSubtract() {
        List<Instruction> instructions = List.of(
                new Instruction("000000 00001 00010 00011 00000 100010") // sub $3, $1, $2
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(3);
        assertEquals(registerValues.get(1) - registerValues.get(2), register.readData1());
    }

    @Test
    void testAnd() {
        List<Instruction> instructions = List.of(
                new Instruction("000000 00001 00010 00011 00000 100100") // and $3, $1, $2
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(3);
        assertEquals(registerValues.get(1) & registerValues.get(2), register.readData1());
    }

    @Test
    void testOr() {
        List<Instruction> instructions = List.of(
                new Instruction("000000 00001 00010 00011 00000 100101") // or $3, $1, $2
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(3);
        assertEquals(registerValues.get(1) | registerValues.get(2), register.readData1());
    }

    @Test
    void testSetOnLessThan() {
        List<Instruction> instructions = List.of(
                new Instruction("000000 00001 00010 00011 00000 101010") // slt $3, $1, $2
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(3);
        assertEquals(registerValues.get(1) < registerValues.get(2) ? 1 : 0, register.readData1());
    }

    @Test
    void testLoadWord() {
        List<Instruction> instructions = List.of(
                new Instruction("100011 00010 00001 0000000000000100") // lw $1, 4($2)
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(1);
        assertEquals(dataMemoryValues.get(registerValues.get(2) + 4), register.readData1());
    }

    private void buildProcessorAndRun(List<Instruction> instructions) {
        Processor processor = processorBuilder
                .setInstructions(instructions)
                .build();
        processor.run();
    }
}