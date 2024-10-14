package de.wladimircomputin.cryptohouse.importexport;

import java.util.List;

public interface MultipleSelectionDialogReceiver<T> {
    void onFinish(List<T> selectedItems);
    void onAbort();
}
