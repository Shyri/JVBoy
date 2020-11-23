package es.shyri.jvboy.memory.mbc;

public abstract class MBC {
    public abstract byte read(int address);
    public abstract void write(int address, int value);
}
