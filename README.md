# JVBoy
Yet Another (WIP) Android Gameboy Emulator

Tests:

- [X] 01-special.gb
- [X] 02-interrupts.gb
- [X] 03-op sp,hl.gb
- [X] 04-op r,imm.gb
- [X] 05-op rp.gb
- [X] 06-ld r,r.gb 
- [X] 07-jr,jp,call,ret,rst.gb
- [X] 08-misc instrs.gb 
- [X] 09-op r,r.gb
- [X] 10-bit ops.gb
- [X] 11-op a,(hl).gb
- [ ] instr_timing.gb

To Do:
- [ ] MBC
- [ ] Background Scroll
- [ ] Sprites
- [ ] Key Input
- [ ] FPS Limit

Hardware Bug emulation: 
- [ ] halt_bug.gb
- [ ] oam_bug
  - [ ] lcd_sync.gb
  - [ ] causes.gb
  - [ ] non_causes.gb
  - [ ] scanline_timing.gb
  - [ ] timing_bug.gb	
  - [ ] timing_no_bug.gb
  - [ ] timing_effect.gb
  - [ ] instr_effect.gb

![Screenshot](images/screenshot.png)
