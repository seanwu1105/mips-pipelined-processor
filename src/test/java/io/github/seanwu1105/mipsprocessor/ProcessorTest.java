package io.github.seanwu1105.mipsprocessor;

import io.github.seanwu1105.mipsprocessor.component.Memory;
import io.github.seanwu1105.mipsprocessor.component.Register;
import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.Instruction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessorTest {

    @NotNull
    private static final Map<Integer, Integer> initRegisterValues = Map.of(
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
    private static final Map<Integer, Integer> initDataMemoryValues = Map.of(
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

    // TODO: Implement branch hazard resolver.
//    @Test
//    void testBranchOnEqualFalse() {
//        List<Instruction> instructions = List.of(
//                new Instruction("000100 00001 00010 0000000000000001"), // beq $1, $2, 1
//                new Instruction("000000 00001 00010 00011 00000 100000"), // add $3, $1, $2
//                new Instruction("000000 00011 00100 00101 00000 100010") // sub $5, $3, $4
//        );
//
//        buildProcessorAndRun(instructions);
//
//        register.setReadAddress1(5);
//        assertEquals(registerValues.get(1) + registerValues.get(2) - registerValues.get(4), register.readData1());
//    }
//
//    @Test
//    void testBranchOnEqualTrue() {
//        List<Instruction> instructions = List.of(
//                new Instruction("000100 00001 00001 0000000000000001"), // beq $1, $1, 1
//                new Instruction("000000 00001 00010 00011 00000 100000"), // add $3, $1, $2
//                new Instruction("000000 00011 00100 00101 00000 100010") // sub $5, $3, $4
//        );
//
//        buildProcessorAndRun(instructions);
//
//        register.setReadAddress1(5);
//        assertEquals(registerValues.get(3) - registerValues.get(4), register.readData1());
//    }

    @Test
    void testProcessorLogWithSingleRTypeInstruction() {
        final var addInstruction = new Instruction("000000 00001 00010 00011 00000 100101"); // or $3, $1, $2
        final var expect = String.join(System.lineSeparator(),
                "CC1:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 4,
                "Instruction\t" + addInstruction,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC2:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 8,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + initRegisterValues.get(addInstruction.getRs()),
                "ReadData2\t" + initRegisterValues.get(addInstruction.getRt()),
                "sign_ext\t" + addInstruction.getImmediate(),
                "Rs\t" + addInstruction.getRs(),
                "Rt\t" + addInstruction.getRt(),
                "Rd\t" + addInstruction.getRd(),
                "Control Signals\t" + "110000010",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC3:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 12,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + (initRegisterValues.get(addInstruction.getRs()) | initRegisterValues.get(addInstruction.getRt())),
                "WriteData\t" + initRegisterValues.get(addInstruction.getRt()),
                "Rt/Rd\t" + addInstruction.getRd(),
                "Control Signals\t" + "00010",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC4:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 16,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + (initRegisterValues.get(addInstruction.getRs()) | initRegisterValues.get(addInstruction.getRt())),
                "Rt/Rd\t" + addInstruction.getRd(),
                "Control Signals\t" + "10",
                "=================================================================",
                "CC5:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + (initRegisterValues.get(1) | initRegisterValues.get(2)),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 20,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",

                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                ""
        );

        final var logger = new ProcessorLogger();
        final var instructions = List.of(
                addInstruction
        );

        buildProcessorAndRun(instructions, logger);

        assertEquals(expect, logger.getLog());
    }

    @Test
    void testProcessorLogWithSingleLoadWordInstruction() {
        final var loadWordInstruction = new Instruction("100011 00001 00110 0000000000000011"); // lw $6, 3($1)
        final var expect = String.join(System.lineSeparator(),
                "CC1:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 4,
                "Instruction\t" + loadWordInstruction,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC2:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 8,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + initRegisterValues.get(loadWordInstruction.getRs()),
                "ReadData2\t" + initRegisterValues.get(loadWordInstruction.getRt()),
                "sign_ext\t" + loadWordInstruction.getImmediate(),
                "Rs\t" + loadWordInstruction.getRs(),
                "Rt\t" + loadWordInstruction.getRt(),
                "Rd\t" + loadWordInstruction.getRd(),
                "Control Signals\t" + "000101011",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC3:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 12,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + (initRegisterValues.get(loadWordInstruction.getRs()) + loadWordInstruction.getImmediate()),
                "WriteData\t" + initRegisterValues.get(loadWordInstruction.getRt()),
                "Rt/Rd\t" + loadWordInstruction.getRt(),
                "Control Signals\t" + "01011",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC4:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 16,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + initDataMemoryValues.get((initRegisterValues.get(loadWordInstruction.getRs()) + loadWordInstruction.getImmediate())),
                "ALUout\t" + (initRegisterValues.get(loadWordInstruction.getRs()) + loadWordInstruction.getImmediate()),
                "Rt/Rd\t" + loadWordInstruction.getRt(),
                "Control Signals\t" + "11",
                "=================================================================",
                "CC5:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initDataMemoryValues.get((initRegisterValues.get(loadWordInstruction.getRs()) + loadWordInstruction.getImmediate())),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 20,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",

                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                ""
        );

        final var logger = new ProcessorLogger();
        final var instructions = List.of(
                loadWordInstruction
        );

        buildProcessorAndRun(instructions, logger);

        assertEquals(expect, logger.getLog());
    }

    @Test
    void testProcessorLogWithSingleSaveWordInstruction() {
        final var saveWordInstruction = new Instruction("101011 00101 00100 0000000000000010"); // sw $4, 2($5)
        final var expect = String.join(System.lineSeparator(),
                "CC1:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 4,
                "Instruction\t" + saveWordInstruction,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC2:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 8,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + initRegisterValues.get(saveWordInstruction.getRs()),
                "ReadData2\t" + initRegisterValues.get(saveWordInstruction.getRt()),
                "sign_ext\t" + saveWordInstruction.getImmediate(),
                "Rs\t" + saveWordInstruction.getRs(),
                "Rt\t" + saveWordInstruction.getRt(),
                "Rd\t" + saveWordInstruction.getRd(),
                "Control Signals\t" + "000100100",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC3:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 12,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + (initRegisterValues.get(saveWordInstruction.getRs()) + saveWordInstruction.getImmediate()),
                "WriteData\t" + initRegisterValues.get(saveWordInstruction.getRt()),
                "Rt/Rd\t" + saveWordInstruction.getRt(),
                "Control Signals\t" + "00100",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC4:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initRegisterValues.get(initRegisterValues.get(saveWordInstruction.getRs() + saveWordInstruction.getImmediate())),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 16,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + (initRegisterValues.get(saveWordInstruction.getRs()) + saveWordInstruction.getImmediate()),
                "Rt/Rd\t" + saveWordInstruction.getRt(),
                "Control Signals\t" + "00",
                "=================================================================",
                ""
        );

        final var logger = new ProcessorLogger();
        final var instructions = List.of(
                saveWordInstruction
        );

        buildProcessorAndRun(instructions, logger);

        assertEquals(expect, logger.getLog());
    }

    @Test
    void testProcessorLogWithMemoryAccessAndRTypeInstructionsWithoutHazards() {
        final var instructions = List.of(
                new Instruction("100011 01000 01001 0000000000000011"),   // lw $9, 3($8)   ----- $9 = 4
                new Instruction("000000 00110 00000 00111 00000 100010"), // sub $7, $6, $0 ----- $7 = 3 - 0
                new Instruction("000000 00011 00010 00100 00000 100100"), // and $4, $3, $2 ----- $4 = 7 & 8
                new Instruction("101011 01000 00001 0000000000000111"),   // sw $1, 7($8)   ----- m12 = 9
                new Instruction("000000 00101 00101 00101 00000 100000")  // add $5, $5, $5 ----- $5 = 2 + 2
        );
        final var expect = String.join(System.lineSeparator(),
                "CC1:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 4,
                "Instruction\t" + instructions.get(0),
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC2:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 8,
                "Instruction\t" + instructions.get(1),
                "",
                "ID/EX:",
                "ReadData1\t" + initRegisterValues.get(instructions.get(0).getRs()),
                "ReadData2\t" + initRegisterValues.get(instructions.get(0).getRt()),
                "sign_ext\t" + instructions.get(0).getImmediate(),
                "Rs\t" + instructions.get(0).getRs(),
                "Rt\t" + instructions.get(0).getRt(),
                "Rd\t" + instructions.get(0).getRd(),
                "Control Signals\t" + "000101011",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC3:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 12,
                "Instruction\t" + instructions.get(2),
                "",
                "ID/EX:",
                "ReadData1\t" + initRegisterValues.get(instructions.get(1).getRs()),
                "ReadData2\t" + initRegisterValues.get(instructions.get(1).getRt()),
                "sign_ext\t" + instructions.get(1).getImmediate(),
                "Rs\t" + instructions.get(1).getRs(),
                "Rt\t" + instructions.get(1).getRt(),
                "Rd\t" + instructions.get(1).getRd(),
                "Control Signals\t" + "110000010",
                "",
                "EX/MEM:",
                "ALUout\t" + (initRegisterValues.get(instructions.get(0).getRs()) + instructions.get(0).getImmediate()),
                "WriteData\t" + initRegisterValues.get(instructions.get(0).getRt()),
                "Rt/Rd\t" + instructions.get(0).getRt(),
                "Control Signals\t" + "01011",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                "CC4:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initRegisterValues.get(9),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 16,
                "Instruction\t" + instructions.get(3),
                "",
                "ID/EX:",
                "ReadData1\t" + initRegisterValues.get(instructions.get(2).getRs()),
                "ReadData2\t" + initRegisterValues.get(instructions.get(2).getRt()),
                "sign_ext\t" + instructions.get(2).getImmediate(),
                "Rs\t" + instructions.get(2).getRs(),
                "Rt\t" + instructions.get(2).getRt(),
                "Rd\t" + instructions.get(2).getRd(),
                "Control Signals\t" + "110000010",
                "",
                "EX/MEM:",
                "ALUout\t" + (initRegisterValues.get(instructions.get(1).getRs()) - initRegisterValues.get(instructions.get(1).getRt())),
                "WriteData\t" + initRegisterValues.get(instructions.get(1).getRt()),
                "Rt/Rd\t" + instructions.get(1).getRd(),
                "Control Signals\t" + "00010",
                "",
                "MEM/WB:",
                "ReadData\t" + initDataMemoryValues.get((initRegisterValues.get(instructions.get(0).getRs()) + instructions.get(0).getImmediate())),
                "ALUout\t" + (initRegisterValues.get(instructions.get(0).getRs()) + instructions.get(0).getImmediate()),
                "Rt/Rd\t" + instructions.get(0).getRt(),
                "Control Signals\t" + "11",
                "=================================================================",
                "CC5:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + initRegisterValues.get(7),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initDataMemoryValues.get(initRegisterValues.get(8) + instructions.get(0).getImmediate()),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 20,
                "Instruction\t" + instructions.get(4),
                "",
                "ID/EX:",
                "ReadData1\t" + initRegisterValues.get(instructions.get(3).getRs()),
                "ReadData2\t" + initRegisterValues.get(instructions.get(3).getRt()),
                "sign_ext\t" + instructions.get(3).getImmediate(),
                "Rs\t" + instructions.get(3).getRs(),
                "Rt\t" + instructions.get(3).getRt(),
                "Rd\t" + instructions.get(3).getRd(),
                "Control Signals\t" + "000100100",
                "",
                "EX/MEM:",
                "ALUout\t" + (initRegisterValues.get(instructions.get(2).getRs()) & initRegisterValues.get(instructions.get(2).getRt())),
                "WriteData\t" + initRegisterValues.get(instructions.get(2).getRt()),
                "Rt/Rd\t" + instructions.get(2).getRd(),
                "Control Signals\t" + "00010",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + (initRegisterValues.get(instructions.get(1).getRs()) - initRegisterValues.get(instructions.get(1).getRt())),
                "Rt/Rd\t" + instructions.get(1).getRd(),
                "Control Signals\t" + "10",
                "=================================================================",
                "CC6:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + initRegisterValues.get(4),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + (initRegisterValues.get(6) - initRegisterValues.get(0)),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initDataMemoryValues.get(initRegisterValues.get(8) + instructions.get(0).getImmediate()),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initDataMemoryValues.get(0x0C),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 24,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + initRegisterValues.get(instructions.get(4).getRs()),
                "ReadData2\t" + initRegisterValues.get(instructions.get(4).getRt()),
                "sign_ext\t" + instructions.get(4).getImmediate(),
                "Rs\t" + instructions.get(4).getRs(),
                "Rt\t" + instructions.get(4).getRt(),
                "Rd\t" + instructions.get(4).getRd(),
                "Control Signals\t" + "110000010",
                "",
                "EX/MEM:",
                "ALUout\t" + (initRegisterValues.get(instructions.get(3).getRs()) + instructions.get(3).getImmediate()),
                "WriteData\t" + initRegisterValues.get(instructions.get(3).getRt()),
                "Rt/Rd\t" + instructions.get(3).getRt(),
                "Control Signals\t" + "00100",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + (initRegisterValues.get(instructions.get(2).getRs()) & initRegisterValues.get(instructions.get(2).getRt())),
                "Rt/Rd\t" + instructions.get(2).getRd(),
                "Control Signals\t" + "10",
                "=================================================================",
                "CC7:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + (initRegisterValues.get(3) & initRegisterValues.get(2)),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + (initRegisterValues.get(6) - initRegisterValues.get(0)),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initDataMemoryValues.get(initRegisterValues.get(8) + instructions.get(0).getImmediate()),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initRegisterValues.get(1),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 28,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + (initRegisterValues.get(instructions.get(4).getRs()) + initRegisterValues.get(instructions.get(4).getRt())),
                "WriteData\t" + initRegisterValues.get(instructions.get(4).getRt()),
                "Rt/Rd\t" + instructions.get(4).getRd(),
                "Control Signals\t" + "00010",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + (initRegisterValues.get(instructions.get(3).getRs()) + instructions.get(3).getImmediate()),
                "Rt/Rd\t" + instructions.get(3).getRt(),
                "Control Signals\t" + "00",
                "=================================================================",
                "CC8:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + (initRegisterValues.get(3) & initRegisterValues.get(2)),
                "$5: " + initRegisterValues.get(5),
                "$6: " + initRegisterValues.get(6),
                "$7: " + (initRegisterValues.get(6) - initRegisterValues.get(0)),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initDataMemoryValues.get(initRegisterValues.get(8) + instructions.get(0).getImmediate()),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initRegisterValues.get(1),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 32,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + (initRegisterValues.get(instructions.get(4).getRs()) + initRegisterValues.get(instructions.get(4).getRt())),
                "Rt/Rd\t" + instructions.get(4).getRd(),
                "Control Signals\t" + "10",
                "=================================================================",
                "CC9:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + initRegisterValues.get(3),
                "$4: " + (initRegisterValues.get(3) & initRegisterValues.get(2)),
                "$5: " + (initRegisterValues.get(5) + initRegisterValues.get(5)),
                "$6: " + initRegisterValues.get(6),
                "$7: " + (initRegisterValues.get(6) - initRegisterValues.get(0)),
                "$8: " + initRegisterValues.get(8),
                "$9: " + initDataMemoryValues.get(initRegisterValues.get(8) + instructions.get(0).getImmediate()),
                "",
                "Data memory:",
                "0x00: " + initDataMemoryValues.get(0x00),
                "0x04: " + initDataMemoryValues.get(0x04),
                "0x08: " + initDataMemoryValues.get(0x08),
                "0x0C: " + initRegisterValues.get(1),
                "0x10: " + initDataMemoryValues.get(0x10),
                "",
                "IF/ID:",
                "PC\t" + 36,
                "Instruction\t" + Instruction.NOP,
                "",
                "ID/EX:",
                "ReadData1\t" + 0,
                "ReadData2\t" + 0,
                "sign_ext\t" + 0,
                "Rs\t" + 0,
                "Rt\t" + 0,
                "Rd\t" + 0,
                "Control Signals\t" + "000000000",
                "",
                "EX/MEM:",
                "ALUout\t" + 0,
                "WriteData\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00000",
                "",
                "MEM/WB:",
                "ReadData\t" + 0,
                "ALUout\t" + 0,
                "Rt/Rd\t" + 0,
                "Control Signals\t" + "00",
                "=================================================================",
                ""
        );

        final var logger = new ProcessorLogger();

        buildProcessorAndRun(instructions, logger);

        assertEquals(expect, logger.getLog());
    }

    private void buildProcessorAndRun(@NotNull final Iterable<Instruction> instructions) {
        buildProcessorAndRun(instructions, null);
    }

    private void buildProcessorAndRun(@NotNull final Iterable<Instruction> instructions, @Nullable final ProcessorLogger logger) {
        final var processor = processorBuilder
                .setInstructions(instructions)
                .build();
        if (logger != null) processor.addLogger(logger);
        processor.run();
    }
}