package com.hexgen;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

/**
 * Created by anishjoseph on 25/05/18.
 */
public class TableCellWithMenu<S,T> extends TableCell {
    private MenuGenerator menuGenerator;
    public TableCellWithMenu(MenuGenerator menuGenerator){
        super();
        this.menuGenerator = menuGenerator;
    }

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        String itemString = ((String)item);
        setText(itemString == null ? null : itemString);

        if(item == null) {
            /* if the cell has no valid data just return - we don't need a context menu */
            return;
        }
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LogRecord logRecord = (LogRecord) ((TableRow) this.getParent()).getItem();
            ContextMenu menu = menuGenerator.getMenu(logRecord);
            menu.show(this.getParent(), event.getScreenX(), event.getScreenY());
        });

    }
}