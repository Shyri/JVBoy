package es.shyri.jvboy.javafx;

import java.io.File;
import java.io.IOException;

import es.shyri.jvboy.GameBoyDebugger;
import es.shyri.jvboy.javafx.cpu.CPUDebugController;
import es.shyri.jvboy.javafx.cpu.CodeListController;
import es.shyri.jvboy.javafx.io.IODebugController;
import es.shyri.jvboy.javafx.memory.MemoryDebugController;
import es.shyri.jvboy.javafx.render.LCDRendererFX;
import es.shyri.jvboy.javafx.render.VRAMViewer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {
    private static final int HEIGHT = 144;
    private static final int WIDTH = 160;

    private WritableImage writableImage;
    private WritableImage vramImage;

    @FXML
    CheckBox ramDebugCheckBox;
    @FXML
    CheckBox cpuDebugCheckBox;
    @FXML
    CheckBox ioDebugCheckBox;
    @FXML
    CheckBox vramViewerCheckBox;

    @FXML
    HBox screenContainer;

    @FXML
    HBox vramViewerContainer;

    private GameBoyDebugger gameBoy;

    @FXML
    CodeListController disassembledCodeController;

    @FXML
    CPUDebugController cpuStatusController;

    @FXML
    MemoryDebugController ramListController;

    @FXML
    IODebugController ioStatusController;

    @FXML
    TextField addresBreak;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("resource/main.fxml"));
        fxmlLoader.setController(this);
        Parent root = fxmlLoader.load();
        root.setStyle("-fx-background-color: #dddddd;");

        writableImage = new WritableImage(WIDTH, HEIGHT);
        ImageView imageView = new ImageView(writableImage);
        screenContainer.getChildren()
                       .add(0, imageView);

        primaryStage.setTitle("JVBoy");
        primaryStage.setScene(new Scene(root, 700, 275));
        primaryStage.show();

        final LCDRendererFX lcdRendererFX = new LCDRendererFX(writableImage.getPixelWriter());

        VRAMViewer vramViewer = new VRAMViewer(vramViewerContainer);
        ramListController.setRamViewer(vramViewer);

        ramDebugCheckBox.selectedProperty()
                        .addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable,
                                                Boolean oldValue,
                                                Boolean newValue) {
                                gameBoy.setMemoryDebugEnabled(newValue);
                            }

                        });
        cpuDebugCheckBox.selectedProperty()
                        .addListener(new ChangeListener<Boolean>() {
                            @Override
                            public void changed(ObservableValue<? extends Boolean> observable,
                                                Boolean oldValue,
                                                Boolean newValue) {
                                gameBoy.setCPUDebugEnabled(newValue);
                            }

                        });
        ioDebugCheckBox.selectedProperty()
                       .addListener(new ChangeListener<Boolean>() {
                           @Override
                           public void changed(ObservableValue<? extends Boolean> observable,
                                               Boolean oldValue,
                                               Boolean newValue) {

                               gameBoy.setIODebugEnabled(newValue);
                           }
                       });
        vramViewerCheckBox.selectedProperty()
                          .addListener(new ChangeListener<Boolean>() {
                              @Override
                              public void changed(ObservableValue<? extends Boolean> observable,
                                                  Boolean oldValue,
                                                  Boolean newValue) {
                                  ramListController.setVramViewerEnabled(newValue);
                                  if (newValue) {
                                      gameBoy.setMemoryDebugEnabled(true);
                                  }
                              }
                          });

        gameBoy =
                new GameBoyDebugger(cpuStatusController, disassembledCodeController, ramListController, ioStatusController);
        try {
            //            gameBoy.loadBios(new File("bios.gb"));
            //            gameBoy.loadRom(new File("test_roms/01-special.gb"));                        // PASSING
            //            gameBoy.loadRom(new File("test_roms/02-interrupts.gb"));                     // PASSING
            //            gameBoy.loadRom(new File("test_roms/03-op sp,hl.gb"));                       // PASSING
            //            gameBoy.loadRom(new File("test_roms/04-op r,imm.gb"));                       // PASSING
            //            gameBoy.loadRom(new File("test_roms/05-op rp.gb"));                          // PASSING
            //            gameBoy.loadRom(new File("test_roms/06-ld r,r.gb"));                         // PASSING
            //            gameBoy.loadRom(new File("test_roms/07-jr,jp,call,ret,rst.gb"));             // PASSING
            //            gameBoy.loadRom(new File("test_roms/08-misc instrs.gb"));                    // PASSING
            //            gameBoy.loadRom(new File("test_roms/09-op r,r.gb"));                         // PASSING
            //            gameBoy.loadRom(new File("test_roms/10-bit ops.gb"));                        // PASSING
            //            gameBoy.loadRom(new File("test_roms/11-op a,(hl).gb"));                      // PASSING
            //            gameBoy.loadRom(new File("test_roms/instr_timing.gb"));

            gameBoy.loadRom(new File("test_roms/halt_bug.gb"));
            //
            //            gameBoy.loadRom(new File("test_roms/cpu_registers_initial_dmg.gbc"));
            //            gameBoy.loadRom(new File("test_roms/Tetris.gb"));
            //            gameBoy.loadRom(new File("test_roms/Mario.gb"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                gameBoy.init(lcdRendererFX);
            }
        }).start();
    }

    private void onVRAMUpdate() {
        //        vramImageView
    }

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    public void onGoClicked() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (addresBreak.getText() != null && addresBreak.getText()
                                                                .length() > 0) {
                    gameBoy.runToAddress(Integer.parseInt(addresBreak.getText(), 16));
                } else {
                    gameBoy.start();
                }
            }
        }).start();
    }

    @FXML
    public void onGo1Clicked() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                gameBoy.runInstructions(1);
            }
        }).start();
    }

    @FXML
    public void onGo10Clicked() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                gameBoy.runInstructions(10);
            }
        }).start();
    }

    @FXML
    public void onGo100Clicked() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                gameBoy.runInstructions(100);
            }
        }).start();
    }

    @FXML
    public void onGo1000Clicked() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                gameBoy.runInstructions(1000);
            }
        }).start();
    }

    @FXML
    public void onGo10000Clicked() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                gameBoy.runInstructions(10000);
            }
        }).start();
    }

    @FXML
    public void onGo100000Clicked() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                gameBoy.runInstructions(100000);
            }
        }).start();
    }

    private int getColorForShade(int shade) {
        if (shade == 0) {
            return 0xffffffff;
        } else if (shade == 1) {
            return 0xffBBBBBB;
        } else if (shade == 2) {
            return 0xff666666;
        } else {
            return 0xFF000000;
        }

        //        f (shade == 0) {
        //            return 0xff879603;
        //        } else if (shade == 1) {
        //            return 0xff4d6b03;
        //        } else if (shade == 2) {
        //            return 0xff2b5503;
        //        } else {
        //            return 0xff144403;
        //        }
    }
}
