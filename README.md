# MIPS Pipelined Processor

[![Java CI with Gradle](https://github.com/seanwu1105/mips-pipelined-processor/workflows/Java%20CI%20with%20Gradle/badge.svg)](https://github.com/seanwu1105/mips-pipelined-processor/actions)
[![codecov](https://codecov.io/gh/seanwu1105/mips-pipelined-processor/branch/master/graph/badge.svg)](https://codecov.io/gh/seanwu1105/mips-pipelined-processor)
[![CodeFactor](https://www.codefactor.io/repository/github/seanwu1105/mips-pipelined-processor/badge)](https://www.codefactor.io/repository/github/seanwu1105/mips-pipelined-processor)

## Dependencies

* JDK 11

## Main Controller

|                 | `RegisterDestination` | `AluOperation` | `AluSource` | `Branch` | `MemoryRead` | `MemoryWrite` | `RegisterWrite` | `MemoryToRegister` |
|:---------------:|:---------------------:|:--------------:|:-----------:|:--------:|:------------:|:-------------:|:---------------:|:------------------:|
|       NOP       |          `0`          |      `00`      |     `0`     |    `0`   |      `0`     |      `0`      |       `0`       |         `0`        |
|      R Type     |          `1`          |      `10`      |     `0`     |    `0`   |      `0`     |      `0`      |       `1`       |         `0`        |
|    Load Word    |          `0`          |      `00`      |     `1`     |    `0`   |      `1`     |      `0`      |       `1`       |         `1`        |
|    Save Word    |       don't care      |      `00`      |     `1`     |    `0`   |      `0`     |      `1`      |       `0`       |     don't care     |
| Branch On Equal |       don't care      |      `01`      |     `0`     |    `1`   |      `0`     |      `0`      |       `0`       |     don't care     |