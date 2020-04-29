package component.pipeline;

import controller.MainController;

public class Execution implements Stage {

    public Execution(InstructionDecodeToExecutionRegister idEx) {
    }

    public MainController.RegisterWrite getRegisterWrite() {
        return null;
    }

    public MainController.MemoryToRegister getMemoryToRegister() {
        return null;
    }

    public MainController.Branch getBranch() {
        return null;
    }

    public MainController.MemoryRead getMemoryRead() {
        return null;
    }

    public MainController.MemoryWrite getMemoryWrite() {
        return null;
    }

    public int getBranchResult() {
        return 0;
    }

    public int getAluResult() {
        return 0;
    }

    public int getRegisterData2() {
        return 0;
    }

    public int getWriteRegisterAddress() {
        return 0;
    }

    @Override
    public void run() {

    }
}
