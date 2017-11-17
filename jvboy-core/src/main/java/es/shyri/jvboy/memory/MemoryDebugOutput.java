package es.shyri.jvboy.memory;

/**
 * Created by shyri on 11/10/2017.
 */
public interface MemoryDebugOutput {
    void onWriteToRAM(int position, String[] ram, byte[] ramValues);

    void onUpdateWholeRAM(String[] ram, byte[] ramValues);
}
