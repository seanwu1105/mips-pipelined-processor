package component.pipeline;

import controller.MainController;

public class MemoryAccess implements Stage {

    public MemoryAccess(ExecutionToMemoryAccessRegister exMem) {
    }

    @Override
    public void run() {

    }

    public MainController.RegisterWrite getRegisterWrite() {
        return null;
    }

    public MainController.MemoryToRegister getMemoryToRegister() {
        return null;
    }
}
