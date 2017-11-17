package es.shyri.jvboy.cpu;

/**
 * Created by shyri on 03/11/2017.
 */
public interface DisassemblyOutput {
    void updateDecompiled(int PC, String[] decompiledCode);
}
