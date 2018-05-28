package com.hexgen;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;

/**
 * Created by anishjoseph on 25/05/18.
 */
public class MsgTableCell<S,T> extends TableCell {
    public MsgTableCell(){
        super();
    }

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        setText(item == null ? null : item.toString());
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            StringBuffer sb = new StringBuffer();
            LogRecord logRecord = (LogRecord) ((TableRow) this.getParent()).getItem();

            if (logRecord != null) {
                TextArea textArea = new TextArea();
                textArea.setEditable(false);
                sb.append(logRecord.getMsg() + "\n");
                if(logRecord.getAddlInfo() != null) {
                    sb.append(logRecord.getAddlInfo());
                }
                textArea.setText(sb.toString());
                textArea.setPrefWidth(1000);
                textArea.setPrefHeight(600);
                Popup popup = new Popup();
                popup.centerOnScreen();
                popup.setAutoHide(true);
                popup.getContent().add(textArea);
                popup.show(this.getScene().getWindow());
            }

        });

    }
}