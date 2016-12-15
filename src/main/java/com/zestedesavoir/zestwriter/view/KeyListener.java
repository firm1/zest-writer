package com.zestedesavoir.zestwriter.view;

import com.zestedesavoir.zestwriter.view.com.CustomStyledClassedTextArea;
import com.ziclix.python.sql.pipe.Source;
import javafx.application.Platform;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

/**
 * Created by: WinXaito (Kevin Vuilleumier)
 */
public class KeyListener implements NativeKeyListener{
    private Logger logger = LogManager.getLogger(KeyListener.class);
    private CustomStyledClassedTextArea sourceText;
    private MdConvertController mdConvertController;
    private boolean cirumflex = false;

    public KeyListener(){
        System.out.println("Initialize keylistener");
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent){
        if(sourceText != null){
            logger.info("KeyPressed: Code: " + nativeKeyEvent.getKeyCode() + " Str: " + nativeKeyEvent.toString());

            if(nativeKeyEvent.getKeyCode() == 26){
                if(cirumflex){
                    Platform.runLater(() -> sourceText.appendText("^^ (Youpi !)"));
                    logger.info("AppendText: ^^");

                    cirumflex = false;
                }else{
                    cirumflex = true;
                }

                logger.info("KEY 30 - Cirumflex");
            }else if(nativeKeyEvent.getKeyCode() == 18){
                if(cirumflex){
                    logger.info("AppendText: ê");
                    Platform.runLater(() -> sourceText.appendText("ê (Youpi !)"));

                    cirumflex = false;
                }
            }
        }else{
            logger.info("Not sourceText defined");
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent){
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent){
    }

    public void setMdConvertController(MdConvertController mdConvertController){
        this.mdConvertController = mdConvertController;
    }

    public void setSourceText(CustomStyledClassedTextArea sourceText){
        this.sourceText = sourceText;
    }
}
