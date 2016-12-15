package com.zestedesavoir.zestwriter.view;

import com.zestedesavoir.zestwriter.view.com.CustomStyledClassedTextArea;
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

    private boolean enable = false;
    private boolean cirumflex = false;
    private boolean trema;

    public KeyListener(){
        logger.debug("Initialize keyListener");
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e){
        if(sourceText != null && enable){
            logger.debug("Keypressed: " + e.getKeyCode() + " -- " + e.getModifiers());

            if(e.getKeyCode() == 26 && e.getModifiers() == 0){
                //Circumflex on Linux (27 for Windows)
                // + without modifiers

                if(cirumflex){
                    Platform.runLater(() -> sourceText.appendText("^^"));
                    cirumflex = false;
                }else{
                    cirumflex = true;
                    trema = false;
                }
            }else if(e.getKeyCode() == 26 && e.getModifiers() == 1){
                //Trema with shift
                // + (41 for Windows without shift) (Shift is the modifiers)

                if(trema){
                    Platform.runLater(() -> sourceText.appendText("¨¨"));
                    trema = false;
                }else{
                    trema = true;
                    cirumflex = false;
                }
            }else{
                switch(e.getKeyCode()){
                    case 18: //E
                        if(cirumflex)
                            appendSpecialCharacter("ê");
                        else if(trema)
                            appendSpecialCharacter("ë");
                        break;
                    case 30: //A
                        if(cirumflex)
                            appendSpecialCharacter("â");
                        else if(trema)
                            appendSpecialCharacter("ä");
                        break;
                    case 23: //I
                        if(cirumflex)
                            appendSpecialCharacter("î");
                        else if(trema)
                            appendSpecialCharacter("ï");
                        break;
                    case 24: //O
                        if(cirumflex)
                            appendSpecialCharacter("ô");
                        else if(trema)
                            appendSpecialCharacter("ö");
                        break;
                    case 22: //U
                        if(cirumflex)
                            appendSpecialCharacter("û");
                        else if(trema)
                            appendSpecialCharacter("ü");
                        break;
                }

                cirumflex = false;
            }
        }
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent){
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent){
    }

    public void setSourceText(CustomStyledClassedTextArea sourceText){
        this.sourceText = sourceText;
        enable = true;
    }

    public void setEnable(boolean enable){
        this.enable = enable;
    }

    private void appendSpecialCharacter(String letter){
        logger.debug("Append special character (Linux Only): " + letter);
        Platform.runLater(() -> sourceText.deleteText(sourceText.getCaretPosition() - 1, sourceText.getCaretPosition()));
        Platform.runLater(() -> sourceText.appendText(letter));
        cirumflex = false;
        trema = false;
    }
}
