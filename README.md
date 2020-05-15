# MIPS Pipelined Processor

[![Java CI with Gradle](https://github.com/seanwu1105/mips-pipelined-processor/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/seanwu1105/mips-pipelined-processor/actions)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/42b29e0e6f1545bd8ce7a0f145db0584)](https://www.codacy.com/manual/seanwu1105/mips-pipelined-processor?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=seanwu1105/mips-pipelined-processor&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/42b29e0e6f1545bd8ce7a0f145db0584)](https://www.codacy.com/manual/seanwu1105/mips-pipelined-processor?utm_source=github.com&utm_medium=referral&utm_content=seanwu1105/mips-pipelined-processor&utm_campaign=Badge_Coverage)

## Dependencies

* JDK 11
* Intellij IDEA 2020.1.1

## Supported Instructions

|                | `add`  | `sub`  | `and`  | `or`   | `slt`  | `addi`     | `andi`     | `lw`       | `sw`       | `beq`      | `bne`      |
|:--------------:|:------:|:------:|:------:|:------:|:------:|:----------:|:----------:|:----------:|:----------:|:----------:|:----------:|
| OP Code        | 000000 | 000000 | 000000 | 000000 | 000000 | 001000     | 001100     | 100011     | 101011     | 000100     | 000101     |
| Function Code  | 100000 | 100010 | 100100 | 100101 | 101010 | don't care | don't care | don't care | don't care | don't care | don't care |
| ALU Operation  | 10     | 10     | 10     | 10     | 10     | 00         | 11         | 00         | 00         | 01         | 01         |
| ALU Control    | 010    | 110    | 000    | 001    | 111    | 010        | 000        | 010        | 010        | 110        | 110        |

## Control Signals

|                 | `RegisterDestination` | `AluOperation` | `AluSource` | `Branch` | `MemoryRead` | `MemoryWrite` | `RegisterWrite` | `MemoryToRegister` |
|:---------------:|:---------------------:|:--------------:|:-----------:|:--------:|:------------:|:-------------:|:---------------:|:------------------:|
| NOP             | `0`                   | `00`           | `0`         | `0`      | `0`          | `0`           | `0`             | `0`                |
| R Type          | `1`                   | `10`           | `0`         | `0`      | `0`          | `0`           | `1`             | `0`                |
| Load Word       | `0`                   | `00`           | `1`         | `0`      | `1`          | `0`           | `1`             | `1`                |
| Save Word       | don't care (`0`)      | `00`           | `1`         | `0`      | `0`          | `1`           | `0`             | don't care (`0`)   |
| Branch On Equal | don't care (`0`)      | `01`           | `0`         | `1`      | `0`          | `0`           | `0`             | don't care (`0`)   |

## Control Hazard

Control hazard with data hazard: always stall. For instance,

```mips
add $1, $2, $2
beq $9, $1, 4  # cannot get correct $1 for data hazard at ID stage --> stall 2 clock cycles 
sub $6, $0, $0
lw $5, 3($4)
beq $9, $5, 4  # cannot get correct $5 for data hazard at ID stage --> stall 2 clock cycles
sub $6, $7, $8
sub $6, $0, $0
```

## Reference

* [OP Codes List](https://opencores.org/projects/plasma/opcodes)