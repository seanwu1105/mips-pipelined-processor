package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.controller.MainController;
import org.jetbrains.annotations.NotNull;

public class ExecutionToMemoryAccessRegister implements PipelineRegister {

    @NotNull
    private final Execution execution;

    @NotNull
    private MainController.RegisterWrite registerWrite = MainController.RegisterWrite.FALSE;

    @NotNull
    private MainController.MemoryToRegister memoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;

    @NotNull
    private MainController.Branch branch = MainController.Branch.FALSE;

    @NotNull
    private MainController.MemoryRead memoryRead = MainController.MemoryRead.FALSE;

    @NotNull
    private MainController.MemoryWrite memoryWrite = MainController.MemoryWrite.FALSE;

    private int aluResult;
    private int registerData2;
    private int writeRegisterAddress;

    public ExecutionToMemoryAccessRegister(@NotNull final Execution execution) {
        this.execution = execution;
    }

    @NotNull
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @NotNull
    public MainController.MemoryToRegister getMemoryToRegister() {
        return memoryToRegister;
    }

    @NotNull
    public MainController.Branch getBranch() {
        return branch;
    }

    @NotNull
    public MainController.MemoryRead getMemoryRead() {
        return memoryRead;
    }

    @NotNull
    public MainController.MemoryWrite getMemoryWrite() {
        return memoryWrite;
    }

    public int getAluResult() {
        return aluResult;
    }

    public int getRegisterData2() {
        return registerData2;
    }

    public int getWriteRegisterAddress() {
        return writeRegisterAddress;
    }

    @Override
    public void update() {
        registerWrite = execution.getRegisterWrite();
        memoryToRegister = execution.getMemoryToRegister();
        branch = execution.getBranch();
        memoryRead = execution.getMemoryRead();
        memoryWrite = execution.getMemoryWrite();
        aluResult = execution.getAluResult();
        registerData2 = execution.getRegisterData2();
        writeRegisterAddress = execution.getWriteRegisterAddress();
    }
}
