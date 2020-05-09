# MIPS Pipelined Processor

[![Java CI with Gradle](https://github.com/seanwu1105/mips-pipelined-processor/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/seanwu1105/mips-pipelined-processor/actions)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/42b29e0e6f1545bd8ce7a0f145db0584)](https://www.codacy.com/manual/seanwu1105/mips-pipelined-processor?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=seanwu1105/mips-pipelined-processor&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/42b29e0e6f1545bd8ce7a0f145db0584)](https://www.codacy.com/manual/seanwu1105/mips-pipelined-processor?utm_source=github.com&utm_medium=referral&utm_content=seanwu1105/mips-pipelined-processor&utm_campaign=Badge_Coverage)

## Dependencies

* JDK 11

## Supported Instructions

|                | `add`  | `sub`  | `and`  |  `or`  | `slt`  |    `lw`    |    `sw`    |   `beq`    |
|:--------------:|:------:|:------:|:------:|:------:|:------:|:----------:|:----------:|:----------:|
|     OP Code    | 000000 | 000000 | 000000 | 000000 | 000000 |   100011   |   101011   |   000100   |
|  Function Code | 100000 | 100010 | 100100 | 100101 | 101010 | don't care | don't care | don't care |
|   ALU Control  |  010   |  110   |  000   |  001   |  111   |    010     |    010     |    110     |

## Control Signals

|                 | `RegisterDestination` | `AluOperation` | `AluSource` | `Branch` | `MemoryRead` | `MemoryWrite` | `RegisterWrite` | `MemoryToRegister` |
|:---------------:|:---------------------:|:--------------:|:-----------:|:--------:|:------------:|:-------------:|:---------------:|:------------------:|
|       NOP       |          `0`          |      `00`      |     `0`     |    `0`   |      `0`     |      `0`      |       `0`       |         `0`        |
|      R Type     |          `1`          |      `10`      |     `0`     |    `0`   |      `0`     |      `0`      |       `1`       |         `0`        |
|    Load Word    |          `0`          |      `00`      |     `1`     |    `0`   |      `1`     |      `0`      |       `1`       |         `1`        |
|    Save Word    |    don't care (`0`)   |      `00`      |     `1`     |    `0`   |      `0`     |      `1`      |       `0`       |  don't care (`0`)  |
| Branch On Equal |    don't care (`0`)   |      `01`      |     `0`     |    `1`   |      `0`     |      `0`      |       `0`       |  don't care (`0`)  |