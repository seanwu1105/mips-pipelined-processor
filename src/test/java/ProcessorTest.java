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

    static public final Map<Integer, Integer> initRegisterValues = Map.of(
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
    static public final Map<Integer, Integer> initDataMemoryValues = Map.of(
            0x00, 5,
            0x04, 9,
            0x08, 4,
            0x0C, 8,
            0x10, 7
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
        initRegisterValues.forEach((key, value) -> {
            if (key != 0) {
                register.setWriteAddress(key);
                register.write(value);
            }
        });
        register.setRegisterWrite(MainController.RegisterWrite.FALSE);
    }

    private void initializeDataMemory() {
        dataMemory = new Memory();
        dataMemory.setMemoryWrite(MainController.MemoryWrite.TRUE);
        initDataMemoryValues.forEach((key, value) -> {
            dataMemory.setAddress(key);
            dataMemory.write(value);
        });
        dataMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);
    }

    @Test
    void testAdd() {
        Instruction addInstruction = new Instruction("000000 00001 00010 00011 00000 100000"); // add $3, $1, $2
        List<Instruction> instructions = List.of(addInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(addInstruction.getRd());
        assertEquals(
                initRegisterValues.get(addInstruction.getRs()) + initRegisterValues.get(addInstruction.getRt()),
                register.readData1()
        );
    }

    @Test
    void testSubtract() {
        Instruction subtractInstruction = new Instruction("000000 00001 00010 00011 00000 100010"); // sub $3, $1, $2
        List<Instruction> instructions = List.of(subtractInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(subtractInstruction.getRd());
        assertEquals(
                initRegisterValues.get(subtractInstruction.getRs()) - initRegisterValues.get(subtractInstruction.getRt()),
                register.readData1()
        );
    }

    @Test
    void testAnd() {
        Instruction andInstruction = new Instruction("000000 00001 00010 00011 00000 100100"); // and $3, $1, $2
        List<Instruction> instructions = List.of(andInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(andInstruction.getRd());
        assertEquals(
                initRegisterValues.get(andInstruction.getRs()) & initRegisterValues.get(andInstruction.getRt()),
                register.readData1()
        );
    }

    @Test
    void testOr() {
        Instruction orInstruction = new Instruction("000000 00001 00010 00011 00000 100101"); // or $3, $1, $2
        List<Instruction> instructions = List.of(orInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(orInstruction.getRd());
        assertEquals(
                initRegisterValues.get(orInstruction.getRs()) | initRegisterValues.get(orInstruction.getRt()),
                register.readData1()
        );
    }

    @Test
    void testSetOnLessThan() {
        Instruction setOnLessThanInstruction = new Instruction("000000 00001 00010 00011 00000 101010"); // slt $3, $1, $2
        List<Instruction> instructions = List.of(setOnLessThanInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(setOnLessThanInstruction.getRd());
        assertEquals(
                initRegisterValues.get(setOnLessThanInstruction.getRs()) < initRegisterValues.get(setOnLessThanInstruction.getRt()) ? 1 : 0,
                register.readData1()
        );
    }

    @Test
    void testLoadWord() {
        Instruction loadWordInstruction = new Instruction("100011 00010 00001 0000000000000100"); // lw $1, 4($2)
        List<Instruction> instructions = List.of(loadWordInstruction);

        buildProcessorAndRun(instructions);

        register.setReadAddress1(loadWordInstruction.getRt());
        assertEquals(
                initDataMemoryValues.get(initRegisterValues.get(loadWordInstruction.getRs()) + loadWordInstruction.getImmediate()),
                register.readData1()
        );
    }

    @Test
    void testSaveWord() {
        Instruction saveWordInstruction = new Instruction("101011 00010 00001 0000000000000100"); // sw $1, 4($2)
        List<Instruction> instructions = List.of(saveWordInstruction);

        buildProcessorAndRun(instructions);

        dataMemory.setMemoryRead(MainController.MemoryRead.TRUE);
        dataMemory.setAddress(initRegisterValues.get(saveWordInstruction.getRs()) + saveWordInstruction.getImmediate());
        assertEquals(initRegisterValues.get(saveWordInstruction.getRt()), dataMemory.read());
        dataMemory.setMemoryRead(MainController.MemoryRead.FALSE);
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

    private void buildProcessorAndRun(List<Instruction> instructions) {
        Processor processor = processorBuilder
                .setInstructions(instructions)
                .build();
        processor.run();
    }

    @Test
    void testProcessorPrinter() {
        Instruction addInstruction = new Instruction("000000 00001 00010 00011 00000 100000"); // add $3, $1, $2
        String expect = String.join(System.lineSeparator(),
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
                "ALUout\t" + (initRegisterValues.get(addInstruction.getRs()) + initRegisterValues.get(addInstruction.getRt())),
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
                "ALUout\t" + (initRegisterValues.get(addInstruction.getRs()) + initRegisterValues.get(addInstruction.getRt())),
                "Rt/Rd\t" + addInstruction.getRd(),
                "Control Signals\t" + "10",
                "=================================================================",
                "CC5:",
                "",
                "Registers:",
                "$0: " + initRegisterValues.get(0),
                "$1: " + initRegisterValues.get(1),
                "$2: " + initRegisterValues.get(2),
                "$3: " + (initRegisterValues.get(1) + initRegisterValues.get(2)),
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

        ProcessorLogger logger = new ProcessorLogger();
        List<Instruction> instructions = List.of(
                addInstruction
        );

        Processor processor = processorBuilder
                .setInstructions(instructions)
                .build();
        processor.addLogger(logger);
        processor.run();

        assertEquals(expect, logger.getLog());
    }
}