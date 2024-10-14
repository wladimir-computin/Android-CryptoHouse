package de.wladimircomputin.cryptohouse.actions.status;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.Property;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import de.wladimircomputin.cryptohouse.R;
import de.wladimircomputin.cryptohouse.actions.ActionCallback;
import de.wladimircomputin.cryptohouse.actions.ActionItem;
import de.wladimircomputin.cryptohouse.boilerplate.ItemTouchHelperAdapter;
import de.wladimircomputin.cryptohouse.boilerplate.OnStartDragListener;
import de.wladimircomputin.cryptohouse.devicemanager.DeviceManagerDevice;

public class ActionItemRecyclerListAdapter extends RecyclerView.Adapter<ActionItemHolder> implements ItemTouchHelperAdapter {

    public final List<ActionItem> list = new ArrayList<>();
    private final OnStartDragListener mDragStartListener;
    ActionCallback callback;
    HashMap<String, DeviceManagerDevice> deviceManagerItems;
    Context context;

    public ActionItemRecyclerListAdapter(OnStartDragListener dragStartListener, ActionCallback callback, HashMap<String, DeviceManagerDevice> deviceManagerItems, Context context) {
        mDragStartListener = dragStartListener;
        this.callback = callback;
        this.deviceManagerItems = deviceManagerItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ActionItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.action_item, parent, false);
        return new ActionItemHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActionItemHolder holder, int position) {
        holder.titleText.setText(list.get(position).name);

        holder.drag.setOnLongClickListener(v -> {
            mDragStartListener.onStartDrag(holder);
            return true;
        });

        holder.cloneButton.setOnClickListener(v -> {
            onItemClone(holder.getBindingAdapterPosition());
        });

        holder.deleteButton.setOnClickListener(v -> {
            onItemRemove(holder.getBindingAdapterPosition());
        });

        holder.actionButton.setOnClickListener(v -> {
            onItemAction(holder.getBindingAdapterPosition());
        });

        holder.configButton.setOnClickListener(v -> {
            onItemConfig(holder.getBindingAdapterPosition());
        });

        holder.advancedPanel.post(() -> {
            if(holder.advancedPanel.getTag() == null) {
                holder.advancedPanel.setTag(holder.advancedPanel.getHeight());
                holder.advancedPanel.getLayoutParams().height = 0;
                holder.advancedPanel.setLayoutParams(holder.advancedPanel.getLayoutParams());
                holder.arrow.setRotation(180f);
            }
        });

        holder.arrow.setOnClickListener((view) -> {
            if(holder.arrow.getRotation() == 0) {
                animateHeightTo(holder.advancedPanel, 0);
                holder.arrow.animate()
                        .rotation(180f)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            } else {
                animateHeightTo(holder.advancedPanel, (int)holder.advancedPanel.getTag());
                holder.arrow.animate()
                        .rotation(0)
                        .setDuration(300)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .start();
            }
        });

        ActionStatusRecyclerListAdapter actionStatusRecyclerListAdapter = new ActionStatusRecyclerListAdapter(callback, deviceManagerItems, context);
        holder.actionRecycleview.setAdapter(actionStatusRecyclerListAdapter);
        holder.actionRecycleview.setLayoutManager(new LinearLayoutManager(context));
        actionStatusRecyclerListAdapter.list.addAll(Arrays.asList(list.get(holder.getBindingAdapterPosition()).action_device_items));
        actionStatusRecyclerListAdapter.notifyDataSetChanged();
    }

    public void onItemAction(int position) {
        callback.onActionClicked(list.get(position));
    }

    public void onItemConfig(int position) {
        callback.onConfigClicked(list.get(position));
    }

    @Override
    public void onItemRemove(int position) {
        if(position >= 0 && position < list.size()) {
            list.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void onItemClone(int position) {
        ActionItem item = list.get(position).clone();
        item.id = UUID.randomUUID().toString();
        list.add(position+1, item);
        notifyItemInserted(position+1);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(list, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void animateHeightTo(@NonNull View view, int height) {
        final int currentHeight = view.getHeight();
        ObjectAnimator animator = ObjectAnimator.ofInt(view, new HeightProperty(), currentHeight, height);
        animator.setDuration(300);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    static class HeightProperty extends Property<View, Integer> {

        public HeightProperty() {
            super(Integer.class, "height");
        }

        @Override public Integer get(View view) {
            return view.getHeight();
        }

        @Override public void set(View view, Integer value) {
            view.getLayoutParams().height = value;
            view.setLayoutParams(view.getLayoutParams());
        }
    }
}

