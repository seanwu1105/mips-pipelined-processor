# MIPS Pipelined Processor

[![Java CI with Gradle](https://github.com/seanwu1105/mips-pipelined-processor/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/seanwu1105/mips-pipelined-processor/actions)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/42b29e0e6f1545bd8ce7a0f145db0584)](https://www.codacy.com/manual/seanwu1105/mips-pipelined-processor?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=seanwu1105/mips-pipelined-processor&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/42b29e0e6f1545bd8ce7a0f145db0584)](https://www.codacy.com/manual/seanwu1105/mips-pipelined-processor?utm_source=github.com&utm_medium=referral&utm_content=seanwu1105/mips-pipelined-processor&utm_campaign=Badge_Coverage)

An assignment for course _CE3001-A Computer Organization_ in National Central University, Taiwan.

## Usage

### Create Processor

```java
final var instructions = List.of(
        new Instruction("000101 01001 01000 0000000000000001"),   // bne $9, $8, 2
        new Instruction("000000 00001 00010 00011 00000 100000"), // add $3, $1, $2
        new Instruction("000000 00011 00100 00101 00000 100010"), // add $4, $6, $7
        new Instruction("000000 00011 00100 00101 00000 100010")  // sub $5, $3, $4
);
final var initRegisterValues = Map.of(
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
final var initDataMemoryValues = Map.of(
        0x00, 5,
        0x04, 9,
        0x08, 4,
        0x0C, 8,
        0x10, 7
);

// Create a MIPS pipelined processor with the given instructions, initial register values and data memory values.
final var processor = new Processor.Builder()
        .setInstructions(instructions)
        .setRegisterValues(initRegisterValues)
        .setDataMemoryValues(initDataMemoryValues)
        .build();

// Create and add a logger to log the variables in the processor.
final var logger = new ProcessorLogger();
processor.addLogger(logger);

// Start the pipeline.
processor.run();

// Get and print the log.
System.out.println(logger.getLog());
```

### Behavior

See [ProcessorTest](./src/test/java/io/github/seanwu1105/mipsprocessor/ProcessorTest.java) and [ProcessorLoggerTest](./src/test/java/io/github/seanwu1105/mipsprocessor/ProcessorLoggerTest.java) for all examples.

#### Simple Data Hazard

Use [forwarding unit](./src/main/java/io/github/seanwu1105/mipsprocessor/component/ForwardingUnit.java) to resolve. For example:

```text
00000000001000100000100000100000 // add $1, $1, $2
00000000001000110000100000100000 // sub $1, $1, $3
00000000001001000000100000100000 // sub $1, $1, $4
00000000001001010000100000100000 // add $1, $1, $5
```

#### Data Hazard With Load Word

Use [hazard detection unit](./src/main/java/io/github/seanwu1105/mipsprocessor/component/HazardDetectionUnit.java) to resolve. Stall the instruction after the load word `lw` instruction for one clock cycle to wait the `lw` instruction has finished the memory access stage. For example:

```text
10001100010000010000000000001000   // lw  $1, 8($2)
000000000010001100100 00000 100010 // sub $4, $1, $3
```

#### Control Hazard Only

To reduce the punishment of branch, the processor determine whether to branch in the instruction decode stage. Always assume the branch will __Not__ be taken. If branch, flush the latest instruction in the instruction fetch stage and stall the instruction after the branch instruction for one clock cycle. For example:

```text
00010001001010010000000000000001 // beq $9, $9, 2
00000000001000100001100000100000 // add $3, $1, $2
00000000011001000010100000100010 // add $4, $6, $7
00000000011001000010100000100010 // sub $5, $3, $4
```

#### Branch with Data Hazard

__!!not yet implement!!__

Always stall. For instance:

```mips
add $1, $2, $2
beq $9, $1, 4  # cannot get correct $1 for data hazard at ID stage --> stall 2 clock cycles 
sub $6, $0, $0
lw $5, 3($4)
beq $9, $5, 4  # cannot get correct $5 for data hazard at ID stage --> stall 2 clock cycles
sub $6, $7, $8
sub $6, $0, $0
```

## Implementation

Simulate the simple MIPS pipeline. Including structural, data and control hazard detection. The implementation details are derived from [_Computer Organization and Design: The Hardware/Software Interface, Fifth Edition_](https://play.google.com/store/books/details/David_A_Patterson_Computer_Organization_and_Design?id=EVhgAAAAQBAJ), Single cycle implementation.

### Supported Instructions

|                | `add`  | `sub`  | `and`  | `or`   | `slt`  | `addi`     | `andi`     | `lw`       | `sw`       | `beq`      | `bne`      |
|:--------------:|:------:|:------:|:------:|:------:|:------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|
| OP Code        | 000000 | 000000 | 000000 | 000000 | 000000 | 001000     | 001100     | 100011     | 101011     | 000100     | 000101     |
| Function Code  | 100000 | 100010 | 100100 | 100101 | 101010 | don't care | don't care | don't care | don't care | don't care | don't care |
| ALU Operation  | 10     | 10     | 10     | 10     | 10     | 00         | 11         | 00         | 00         | 01         | 01         |
| ALU Control    | 010    | 110    | 000    | 001    | 111    | 010        | 000        | 010        | 010        | 110        | 110        |

### Control Signals

|                 | `RegisterDestination` | `AluOperation` | `AluSource` | `Branch` | `MemoryRead` | `MemoryWrite` | `RegisterWrite` | `MemoryToRegister` |
|:---------------:|:---------------------:|:--------------:|:-----------:|:--------:|:------------:|:-------------:|:---------------:|:------------------:|
| NOP             | `0`                   | `00`           | `0`         | `0`      | `0`          | `0`           | `0`             | `0`                |
| R Type          | `1`                   | `10`           | `0`         | `0`      | `0`          | `0`           | `1`             | `0`                |
| Load Word       | `0`                   | `00`           | `1`         | `0`      | `1`          | `0`           | `1`             | `1`                |
| Save Word       | don't care (`0`)      | `00`           | `1`         | `0`      | `0`          | `1`           | `0`             | don't care (`0`)   |
| Branch On Equal | don't care (`0`)      | `01`           | `0`         | `1`      | `0`          | `0`           | `0`             | don't care (`0`)   |

## Dependencies

* JDK 11
* Intellij IDEA 2020.1.1

## Reference

* [OP Codes List](https://opencores.org/projects/plasma/opcodes)