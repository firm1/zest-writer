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
            logger.debug("Keypressed: " + e.getRawCode() + " -- " + e.getModifiers());

            if(e.getRawCode() == 221){
                if(cirumflex){
                    appendSpecialCharacter("^^");
                    cirumflex = false;
                }else{
                    cirumflex = true;
                    trema = false;
                }
            }else if(e.getRawCode() == 192){
                if(trema){
                    appendSpecialCharacter("¨¨");
                    trema = false;
                }else{
                    trema = true;
                    cirumflex = false;
                }
            }else{
                switch(e.getRawCode()){
                    case 69: //E
                        if(cirumflex)
                            appendSpecialCharacter("ê");
                        else if(trema)
                            appendSpecialCharacter("ë");
                        break;
                    case 65: //A
                        if(cirumflex)
                            appendSpecialCharacter("â");
                        else if(trema)
                            appendSpecialCharacter("ä");
                        break;
                    case 73: //I
                        if(cirumflex)
                            appendSpecialCharacter("î");
                        else if(trema)
                            appendSpecialCharacter("ï");
                        break;
                    case 79: //O
                        if(cirumflex)
                            appendSpecialCharacter("ô");
                        else if(trema)
                            appendSpecialCharacter("ö");
                        break;
                    case 85: //U
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
        logger.debug("Append special character (Linux Only): " + letter + " (Length: " + letter.length() + ")");
        Platform.runLater(() -> sourceText.deleteText(sourceText.getCaretPosition() - letter.length(), sourceText.getCaretPosition()));
        Platform.runLater(() -> sourceText.appendText(letter));
        cirumflex = false;
        trema = false;
    }
}
