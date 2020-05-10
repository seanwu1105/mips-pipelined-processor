package io.github.seanwu1105.mipsprocessor.component.pipeline;

public interface Stage {

    void run();

    boolean hasInstruction();
}
