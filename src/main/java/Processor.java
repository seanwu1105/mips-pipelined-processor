import component.Alu;
import component.Memory;
import component.Register;
import component.pipeline.*;
import controller.MainController;
import org.jetbrains.annotations.NotNull;
import signal.Instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Processor {

    private final List<Stage> stages = new ArrayList<>();
    private final List<PipelineRegister> pipelineRegisters = new ArrayList<>();
    private final List<ProcessorLogger> loggers = new ArrayList<>();
    private final InstructionDecode instructionDecode;
    private final MemoryAccess memoryAccess;

    private Processor(
            @NotNull InstructionFetch instructionFetch,
            @NotNull InstructionFetchToInstructionDecodeRegister ifId,
            @NotNull InstructionDecode instructionDecode,
            @NotNull InstructionDecodeToExecutionRegister idExe,
            @NotNull Execution execution,
            @NotNull ExecutionToMemoryAccessRegister exeMem,
            @NotNull MemoryAccess memoryAccess,
            @NotNull MemoryAccessToWriteBackRegister memWb,
            @NotNull WriteBack writeBack
    ) {
        this.instructionDecode = instructionDecode;
        this.memoryAccess = memoryAccess;
        stages.add(instructionFetch);
        stages.add(instructionDecode);
        stages.add(execution);
        stages.add(memoryAccess);
        stages.add(writeBack);
        pipelineRegisters.add(ifId);
        pipelineRegisters.add(idExe);
        pipelineRegisters.add(exeMem);
        pipelineRegisters.add(memWb);
    }

    public void run() {
        do {
            stages.forEach(Stage::run);
            pipelineRegisters.forEach(PipelineRegister::update);
            loggers.forEach(printer -> printer.onClockCycleFinished(this));
        } while (hasUnfinishedInstructions());
    }

    private boolean hasUnfinishedInstructions() {
        for (Stage stage : stages)
            if (stage.hasInstruction()) return true;
        return false;
    }

    public void addLogger(ProcessorLogger logger) {
        loggers.add(logger);
    }

    public Set<Integer> getWrittenRegisterAddresses() {
        return instructionDecode.getWrittenRegisterAddresses();
    }

    public int readRegister(int address) {
        return instructionDecode.readRegister(address);
    }

    public Set<Integer> getWrittenDataMemoryAddresses() {
        return memoryAccess.getWrittenDataMemoryAddresses();
    }

    public int readDataMemory(int address) {
        return memoryAccess.readDataMemory(address);
    }

    static public class Builder {

        @NotNull
        private final Memory instructionMemory = new Memory();
        @NotNull
        private Register register = new Register();
        @NotNull
        private Memory dataMemory = new Memory();

        Builder setInstructions(List<Instruction> instructions) {
            instructionMemory.setMemoryWrite(MainController.MemoryWrite.TRUE);
            int address = 0x00;
            for (Instruction instruction : instructions) {
                instructionMemory.setAddress(address);
                instructionMemory.write(instruction);
                address += 4;
            }
            instructionMemory.setMemoryWrite(MainController.MemoryWrite.FALSE);
            return this;
        }

        Builder setRegister(@NotNull Register register) {
            this.register = register;
            return this;
        }

        Builder setDataMemory(@NotNull Memory dataMemory) {
            this.dataMemory = dataMemory;
            return this;
        }

        @NotNull
        Processor build() {
            InstructionFetch instructionFetch = new InstructionFetch(instructionMemory);
            InstructionFetchToInstructionDecodeRegister ifId = new InstructionFetchToInstructionDecodeRegister(instructionFetch);
            InstructionDecode instructionDecode = new InstructionDecode(ifId, new MainController(), register);
            InstructionDecodeToExecutionRegister idExe = new InstructionDecodeToExecutionRegister(instructionDecode);
            Execution execution = new Execution(idExe, new Alu(), new Alu());
            ExecutionToMemoryAccessRegister exeMem = new ExecutionToMemoryAccessRegister(execution);
            MemoryAccess memoryAccess = new MemoryAccess(exeMem, dataMemory);
            MemoryAccessToWriteBackRegister memWb = new MemoryAccessToWriteBackRegister(memoryAccess);
            WriteBack writeBack = new WriteBack(memWb, register);
            return new Processor(
                    instructionFetch,
                    ifId,
                    instructionDecode,
                    idExe,
                    execution,
                    exeMem,
                    memoryAccess,
                    memWb,
                    writeBack
            );
        }
    }
}
