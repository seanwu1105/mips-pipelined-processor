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

    @Override
    public void run() {

    }
}
