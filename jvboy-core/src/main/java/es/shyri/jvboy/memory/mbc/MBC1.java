package es.shyri.jvboy.memory.mbc;

public class MBC1 extends MBC {
    private byte[] rom;

    public MBC1(byte[] rom) {
        this.rom = rom;
    }


    @Override
    public byte read(int address) {
        return 0;
    }

    @Override
    public void write(int address, int value) {

    }
}
