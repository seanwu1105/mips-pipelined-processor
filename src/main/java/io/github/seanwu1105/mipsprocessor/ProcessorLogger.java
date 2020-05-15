package io.github.seanwu1105.mipsprocessor;

import io.github.seanwu1105.mipsprocessor.component.pipeline.ExecutionToMemoryAccessRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionDecode;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionDecodeToExecutionRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.InstructionFetchToInstructionDecodeRegister;
import io.github.seanwu1105.mipsprocessor.component.pipeline.MemoryAccess;
import io.github.seanwu1105.mipsprocessor.component.pipeline.MemoryAccessToWriteBackRegister;
import org.jetbrains.annotations.NotNull;

import java.util.TreeSet;

class ProcessorLogger {

    @NotNull
    private final StringBuilder stringBuilder = new StringBuilder();

    private int clockCycle = 1;

    void onClockCycleFinished(
            @NotNull final InstructionFetchToInstructionDecodeRegister ifId,
            @NotNull final InstructionDecode instructionDecode,
            @NotNull final InstructionDecodeToExecutionRegister idExe,
            @NotNull final ExecutionToMemoryAccessRegister exeMem,
            @NotNull final MemoryAccess memoryAccess,
            @NotNull final MemoryAccessToWriteBackRegister memWb
    ) {
        appendLine("CC" + clockCycle + ":");
        appendLine("");
        logRegister(instructionDecode);
        appendLine("");
        logDataMemory(memoryAccess);
        appendLine("");
        logIfId(ifId);
        appendLine("");
        logIdExe(idExe);
        appendLine("");
        logExeMem(exeMem);
        appendLine("");
        logMemWb(memWb);
        appendLine("=================================================================");
        clockCycle++;
    }

    private void logRegister(@NotNull final InstructionDecode instructionDecode) {
        final Iterable<Integer> sortedAddresses = new TreeSet<>(instructionDecode.getWrittenRegisterAddresses());
        appendLine("Registers:");
        sortedAddresses.forEach((address) -> appendLine("$" + address + ": " + instructionDecode.readRegister(address)));
    }

    private void logDataMemory(@NotNull final MemoryAccess memoryAccess) {
        final Iterable<Integer> sortedAddresses = new TreeSet<>(memoryAccess.getWrittenDataMemoryAddresses());
        appendLine("Data memory:");
        sortedAddresses.forEach((address) ->
                appendLine(String.format("0x%02X: %d", address, memoryAccess.readDataMemory(address)))
        );
    }

    private void logIfId(@NotNull final InstructionFetchToInstructionDecodeRegister ifId) {
        appendLine("IF/ID:");
        appendLine("PC\t" + ifId.getProgramCounter());
        appendLine("Instruction\t" + ifId.getInstruction());
    }

    private void logIdExe(@NotNull final InstructionDecodeToExecutionRegister idExe) {
        appendLine("ID/EX:");
        appendLine("ReadData1\t" + idExe.getRegisterData1());
        appendLine("ReadData2\t" + idExe.getRegisterData2());
        appendLine("sign_ext\t" + idExe.getImmediate());
        appendLine("Rs\t" + idExe.getRs());
        appendLine("Rt\t" + idExe.getRt());
        appendLine("Rd\t" + idExe.getRd());
        appendLine("Control Signals\t"
                + idExe.getRegisterDestination()
                + idExe.getAluOperation()
                + idExe.getAluSource()
                + idExe.getBranch()
                + idExe.getMemoryRead()
                + idExe.getMemoryWrite()
                + idExe.getRegisterWrite()
                + idExe.getMemoryToRegister());
    }

    private void logExeMem(@NotNull final ExecutionToMemoryAccessRegister exeMem) {
        appendLine("EX/MEM:");
        appendLine("ALUout\t" + exeMem.getAluResult());
        appendLine("WriteData\t" + exeMem.getRegisterData2());
        appendLine("Rt/Rd\t" + exeMem.getWriteRegisterAddress());
        appendLine("Control Signals\t"
                + exeMem.getBranch()
                + exeMem.getMemoryRead()
                + exeMem.getMemoryWrite()
                + exeMem.getRegisterWrite()
                + exeMem.getMemoryToRegister());
    }

    private void logMemWb(@NotNull final MemoryAccessToWriteBackRegister memWb) {
        appendLine("MEM/WB:");
        appendLine("ReadData\t" + memWb.getMemoryReadData());
        appendLine("ALUout\t" + memWb.getAluResult());
        appendLine("Rt/Rd\t" + memWb.getWriteRegisterAddress());
        appendLine("Control Signals\t"
                + memWb.getRegisterWrite()
                + memWb.getMemoryToRegister());
    }

    @NotNull
    public String getLog() {
        return stringBuilder.toString();
    }

    private void appendLine(final String str) {
        stringBuilder.append(str).append(System.lineSeparator());
    }
}