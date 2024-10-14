package de.wladimircomputin.cryptohouse.actions;

import de.wladimircomputin.cryptohouse.actions.config.ActionDeviceItem;
import de.wladimircomputin.cryptohouse.actions.status.ActionStatusHolder;

public interface ActionCallback {
    void onActionClicked(ActionItem item);
    void onConfigClicked(ActionItem item);
    void onActionStatusClicked(ActionDeviceItem item, ActionStatusHolder holder);
}
