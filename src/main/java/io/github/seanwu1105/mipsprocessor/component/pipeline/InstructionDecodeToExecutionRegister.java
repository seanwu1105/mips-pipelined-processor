package io.github.seanwu1105.mipsprocessor.component.pipeline;

import io.github.seanwu1105.mipsprocessor.controller.MainController;
import io.github.seanwu1105.mipsprocessor.signal.FunctionCode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InstructionDecodeToExecutionRegister implements PipelineRegister {

    @NotNull
    private final InstructionDecode instructionDecode;

    @NotNull
    private MainController.RegisterDestination registerDestination = MainController.RegisterDestination.RT;

    @NotNull
    private MainController.AluOperation aluOperation = MainController.AluOperation.MEMORY_REFERENCE;

    @NotNull
    private MainController.AluSource aluSource = MainController.AluSource.REGISTER;

    @NotNull
    private MainController.Branch branch = MainController.Branch.FALSE;

    @NotNull
    private MainController.MemoryRead memoryRead = MainController.MemoryRead.FALSE;

    @NotNull
    private MainController.MemoryWrite memoryWrite = MainController.MemoryWrite.FALSE;

    @NotNull
    private MainController.RegisterWrite registerWrite = MainController.RegisterWrite.FALSE;

    @NotNull
    private MainController.MemoryToRegister memoryToRegister = MainController.MemoryToRegister.FROM_ALU_RESULT;

    private int registerData1;
    private int registerData2;
    private int immediate;
    private int rs;
    private int rt;
    private int rd;

    @Nullable
    private FunctionCode functionCode = FunctionCode.NOP;

    public InstructionDecodeToExecutionRegister(@NotNull final InstructionDecode instructionDecode) {
        this.instructionDecode = instructionDecode;
    }

    @Override
    public void update() {
        registerDestination = instructionDecode.getRegisterDestination();
        aluOperation = instructionDecode.getAluOperation();
        aluSource = instructionDecode.getAluSource();
        branch = instructionDecode.getBranch();
        memoryRead = instructionDecode.getMemoryRead();
        memoryWrite = instructionDecode.getMemoryWrite();
        registerWrite = instructionDecode.getRegisterWrite();
        memoryToRegister = instructionDecode.getMemoryToRegister();

        registerData1 = instructionDecode.getRegisterData1();
        registerData2 = instructionDecode.getRegisterData2();
        immediate = instructionDecode.getImmediate();
        functionCode = instructionDecode.getFunctionCode();
        rs = instructionDecode.getRs();
        rt = instructionDecode.getRt();
        rd = instructionDecode.getRd();
    }

    @NotNull
    public MainController.RegisterDestination getRegisterDestination() {
        return registerDestination;
    }

    @NotNull
    public MainController.AluOperation getAluOperation() {
        return aluOperation;
    }

    @NotNull
    public MainController.AluSource getAluSource() {
        return aluSource;
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

    @NotNull
    public MainController.RegisterWrite getRegisterWrite() {
        return registerWrite;
    }

    @NotNull
    public MainController.MemoryToRegister getMemoryToRegister() {
        return memoryToRegister;
    }


    public int getRegisterData1() {
        return registerData1;
    }

    public int getRegisterData2() {
        return registerData2;
    }

    public int getImmediate() {
        return immediate;
    }

    @Nullable
    public FunctionCode getFunctionCode() {
        return functionCode;
    }

    public int getRs() {
        return rs;
    }

    public int getRt() {
        return rt;
    }

    public int getRd() {
        return rd;
    }
}
