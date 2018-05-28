package com.hexgen;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

/**
 * Created by anishjoseph on 25/05/18.
 */
public class TableCellWithMenu<S,T> extends TableCell {
    private MenuGenerator menuGenerator;
    private static ContextMenu oldMenu;
    public TableCellWithMenu(MenuGenerator menuGenerator){
        super();
        this.menuGenerator = menuGenerator;
    }

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        setText(item == null ? null : item.toString());

        if(item == null) {
            /* if the cell has no valid data just return - we don't need a context menu */
            return;
        }
        this.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            LogRecord logRecord = (LogRecord) ((TableRow) this.getParent()).getItem();
            if(oldMenu != null){
                oldMenu.hide();
            }
            ContextMenu menu = menuGenerator.getMenu(logRecord);
            oldMenu = menu;
            if(menu != null) menu.show(this.getParent(), event.getScreenX(), event.getScreenY());
        });

    }
}
