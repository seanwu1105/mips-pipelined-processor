package io.github.seanwu1105.mipsprocessor;

import io.github.seanwu1105.mipsprocessor.component.Memory;
import io.github.seanwu1105.mipsprocessor.component.Register;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorTest {

    @NotNull
    private final Map<Integer, Integer> initRegisterValues = Map.of(
            0, 0,
            1, 9,
            2, 8,
            3, 7,
            4, 1,
            5, 2,
            6, 3,
            7, 4,
            8, 5,
            9, 6
    );
    @NotNull
    private final Map<Integer, Integer> initDataMemoryValues = Map.of(
            0x00, 5,
            0x04, 9,
            0x08, 4,
            0x0C, 8,
            0x10, 7
    );
    @NotNull
    private Processor.Builder processorBuilder;
    @NotNull
    private Register register;
    @NotNull
    private Memory dataMemory;

    @BeforeEach
    void buildUp() {
        register = new Register();
        initializeRegister();
        dataMemory = new Memory();
        initializeDataMemory();
        processorBuilder = new Processor.Builder()
                .setRegister(register)
                .setDataMemory(dataMemory);
    }

    private void initializeRegister() {
        register.setRegisterWrite(MainController.RegisterWrite.TRUE);
        initRegisterValues.forEach((key, value) -> {
            if (key != 0) {
                register.setWriteAddress(key);
                register.write(value);
            }
        });
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
    }

    private void initializeDataMemory() {
        dataMemory.setMemoryWrite(MainController.MemoryWrite.TRUE);
        initDataMemoryValues.forEach((key, value) -> {
            dataMemory.setAddress(key);
            dataMemory.write(value);
        });
        dataMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);
    }

    @Test
    void testAdd() {
        final var addInstruction = new Instruction("000000 00001 00010 00011 00000 100000"); // add $3, $1, $2
        final var instructions = List.of(addInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(addInstruction.getRd());
        assertEquals(
                initRegisterValues.get(addInstruction.getRs()) + initRegisterValues.get(addInstruction.getRt()),
                register.readData1()
        );
    }

    @Test
    void testSubtract() {
        final var subtractInstruction = new Instruction("000000 00001 00010 00011 00000 100010"); // sub $3, $1, $2
        final var instructions = List.of(subtractInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(subtractInstruction.getRd());
        assertEquals(
                initRegisterValues.get(subtractInstruction.getRs()) - initRegisterValues.get(subtractInstruction.getRt()),
                register.readData1()
        );
    }

    @Test
    void testAnd() {
        final var andInstruction = new Instruction("000000 00001 00010 00011 00000 100100"); // and $3, $1, $2
        final var instructions = List.of(andInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(andInstruction.getRd());
        assertEquals(
                initRegisterValues.get(andInstruction.getRs()) & initRegisterValues.get(andInstruction.getRt()),
                register.readData1()
        );
    }

    @Test
    void testOr() {
        final var orInstruction = new Instruction("000000 00001 00010 00011 00000 100101"); // or $3, $1, $2
        final var instructions = List.of(orInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(orInstruction.getRd());
        assertEquals(
                initRegisterValues.get(orInstruction.getRs()) | initRegisterValues.get(orInstruction.getRt()),
                register.readData1()
        );
    }

    @Test
    void testSetOnLessThan() {
        final var setOnLessThanInstruction = new Instruction("000000 00001 00010 00011 00000 101010"); // slt $3, $1, $2
        final var instructions = List.of(setOnLessThanInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(setOnLessThanInstruction.getRd());
        assertEquals(
                initRegisterValues.get(setOnLessThanInstruction.getRs()) < initRegisterValues.get(setOnLessThanInstruction.getRt()) ? 1 : 0,
                register.readData1()
        );
    }

    @Test
    void testAddImmediate() {
        final var addiInstruction = new Instruction("001000 00001 00011 0000000000000111"); // addi $3, $1, 7
        final var instructions = List.of(addiInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(addiInstruction.getRt());
        assertEquals(
                initRegisterValues.get(addiInstruction.getRs()) + addiInstruction.getImmediate(),
                register.readData1()
        );
    }

    @Test
    void testAndImmediate() {
        final var addiInstruction = new Instruction("001100 00001 00011 0000000000000111"); // andi $3, $1, 7
        final var instructions = List.of(addiInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(addiInstruction.getRt());
        assertEquals(
                initRegisterValues.get(addiInstruction.getRs()) & addiInstruction.getImmediate(),
                register.readData1()
        );
    }

    @Test
    void testLoadWord() {
        final var loadWordInstruction = new Instruction("100011 00010 00001 0000000000000100"); // lw $1, 4($2)
        final var instructions = List.of(loadWordInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(loadWordInstruction.getRt());
        assertEquals(
                initDataMemoryValues.get(initRegisterValues.get(loadWordInstruction.getRs()) + loadWordInstruction.getImmediate()),
                register.readData1()
        );
    }

    @Test
    void testSaveWord() {
        final var saveWordInstruction = new Instruction("101011 00010 00001 0000000000000100"); // sw $1, 4($2)
        final var instructions = List.of(saveWordInstruction);

        buildProcessorAndRun(instructions);

        dataMemory.setMemoryRead(MainController.MemoryRead.TRUE);
        dataMemory.setAddress(initRegisterValues.get(saveWordInstruction.getRs()) + saveWordInstruction.getImmediate());
        assertEquals(initRegisterValues.get(saveWordInstruction.getRt()), dataMemory.read());
        dataMemory.setMemoryRead(MainController.MemoryRead.FALSE);
    }

    @Test
    void testDataHazardAtExecutionStage() {
        final var instructions = List.of(
                new Instruction("000000 00010 00010 00001 00000 100000"), // add $1, $2, $2
                new Instruction("000000 00001 00001 00011 00000 100000")  // add $3, $1, $1
        );

        final var expectedRegister1 = initRegisterValues.get(2) + initRegisterValues.get(2);
        final var expect = expectedRegister1 + expectedRegister1;

        buildProcessorAndRun(instructions);

        register.setReadAddress1(3);
        assertEquals(expect, register.readData1());
    }

    @Test
    void testDataHazardAtMemoryAccessStage() {
        final var instructions = List.of(
                new Instruction("000000 00010 00010 00001 00000 100000"), // add $1, $2, $2
                new Instruction("000000 00000 00000 01001 00000 100010"), // sub $9, $0, $0
                new Instruction("000000 00001 00001 00011 00000 100000")  // add $3, $1, $1
        );

        final var expectedRegister1 = initRegisterValues.get(2) + initRegisterValues.get(2);
        final var expect = expectedRegister1 + expectedRegister1;

        buildProcessorAndRun(instructions);

        register.setReadAddress1(3);
        assertEquals(expect, register.readData1());
    }

    @Test
    void testDataHazardAtWriteBackStage() {
        final var instructions = List.of(
                new Instruction("000000 00010 00010 00001 00000 100000"), // add $1, $2, $2
                new Instruction("000000 00000 00000 01001 00000 100010"), // sub $9, $0, $0
                new Instruction("000000 00000 00000 01001 00000 100010"), // sub $9, $0, $0
                new Instruction("000000 00001 00001 00011 00000 100000")  // add $3, $1, $1
        );

        final var expectedRegister1 = initRegisterValues.get(2) + initRegisterValues.get(2);
        final var expect = expectedRegister1 + expectedRegister1;

        buildProcessorAndRun(instructions);

        register.setReadAddress1(3);
        assertEquals(expect, register.readData1());
    }

    @Test
    void testDataHazardAtAllPossibleStages() {
        final var instructions = List.of(
                new Instruction("000000 00001 00010 00001 00000 100000"), // add $1, $1, $2
                new Instruction("000000 00001 00011 00001 00000 100000"), // sub $1, $1, $3
                new Instruction("000000 00001 00100 00001 00000 100000"), // sub $1, $1, $4
                new Instruction("000000 00001 00101 00001 00000 100000")  // add $1, $1, $5
        );

        var expect = 0;
        for (var i = 1; i <= 5; i++) {
            expect += initRegisterValues.get(i);
        }

        buildProcessorAndRun(instructions);

        register.setReadAddress1(1);
        assertEquals(expect, register.readData1());
    }

    @Test
    void testDataHazardWithLoadWordInstruction() {
        final var instructions = List.of(
                new Instruction("100011 00010 00001 0000000000001000"), // lw $1, 8($2)
                new Instruction("000000 00001 00011 00100 00000 100010")  // sub $4, $1, $3
        );

        final var expectedRegister1 = initDataMemoryValues.get(initRegisterValues.get(2) + 8);
        final var expect = expectedRegister1 - initRegisterValues.get(3);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(4);
        assertEquals(expect, register.readData1());
    }

    @Test
    void testBranchOnEqualFalse() {
        final var instructions = List.of(
                new Instruction("000100 01001 01000 0000000000000001"), // beq $9, $8, 1
                new Instruction("000000 00001 00010 00011 00000 100000"), // add $3, $1, $2
                new Instruction("000000 00011 00100 00101 00000 100010") // sub $5, $3, $4
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(5);
        assertEquals(initRegisterValues.get(1) + initRegisterValues.get(2) - initRegisterValues.get(4), register.readData1());
    }

    @Test
    void testBranchOnEqualTrue() {
        final var instructions = List.of(
                new Instruction("000100 01001 01001 0000000000000001"), // beq $9, $9, 2
                new Instruction("000000 00001 00010 00011 00000 100000"), // add $3, $1, $2
                new Instruction("000000 00011 00100 00101 00000 100010"), // add $4, $6, $7
                new Instruction("000000 00011 00100 00101 00000 100010") // sub $5, $3, $4
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(5);
        assertEquals(initRegisterValues.get(3) - initRegisterValues.get(4), register.readData1());
    }

    @Test
    void testBranchOnNotEqualFalse() {
        final var instructions = List.of(
                new Instruction("000101 01001 01001 0000000000000001"), // bne $9, $9, 1
                new Instruction("000000 00001 00010 00011 00000 100000"), // add $3, $1, $2
                new Instruction("000000 00011 00100 00101 00000 100010") // sub $5, $3, $4
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(5);
        assertEquals(initRegisterValues.get(1) + initRegisterValues.get(2) - initRegisterValues.get(4), register.readData1());
    }

    @Test
    void testBranchOnNotEqualTrue() {
        final var instructions = List.of(
                new Instruction("000101 01001 01000 0000000000000001"), // bne $9, $8, 2
                new Instruction("000000 00001 00010 00011 00000 100000"), // add $3, $1, $2
                new Instruction("000000 00011 00100 00101 00000 100010"), // add $4, $6, $7
                new Instruction("000000 00011 00100 00101 00000 100010") // sub $5, $3, $4
        );

        buildProcessorAndRun(instructions);

        register.setReadAddress1(5);
        assertEquals(initRegisterValues.get(3) - initRegisterValues.get(4), register.readData1());
    }

    private void buildProcessorAndRun(@NotNull final Iterable<Instruction> instructions) {
        final var processor = processorBuilder
                .setInstructions(instructions)
                .build();
        processor.run();
    }
}