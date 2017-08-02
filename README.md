# 5-Stage-MIPS-Pipeline-Simulator

## Introduction
Simulate the simple MIPS pipeline. Including structural, data and control hazard detection. The concept and other details are derived from [*Computer Organization and Design: The Hardware/
Software Interface, Fifth Edition*](https://goo.gl/5SQ6pc), Single cycle implementation.

## Motivation
This is the assignment of *Computer Organization* in NCU CSIE, Taiwan.

## Hazard Detection
* Structural Hazard Detection
* Data (with LOAD instruction halt 1 more clock cycle) Hazard Detection
* Control Hazard Detection (if branch, halt 1 more clock cycle)

    > **However, if BEQ or other control instruction follow by LOAD instruction, it will cause problem.**

## Assignment Requirement and I/O Regulation
(from course *Computer Organization* in NCU CSIE)
* [105-2 Computer Organization MIPS Simulator Homework](COHW105-pipeline_ver2.pdf) (Traditional Chinese)
* [Output Description](COHW105-pipeline_ver2.pdf) (Traditional Chinese)
## Installation
Compile [104502551_pipeline.cpp](104502551_pipeline.cpp).
