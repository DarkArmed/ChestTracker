package com.darkarmed.chesttrackerforclashroyale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xu on 5/18/16.
 */
public class ChestsAdapter extends BaseAdapter {
    private final Context mContext;
    private List<Chest> mChests = new ArrayList<>();
    private String mSequence;
    private int mLastOpened = 0;
    private final int BUFFER_LENGTH = 12;
    private final int EXTEND_LENGTH = 40;

    public ChestsAdapter(Context context, List<Chest> chests) {
        this.mContext = context;
        this.mChests = chests;
        this.mSequence = mContext.getString(R.string.chest_sequence);
    }

    public void add(Chest chest) {
        mChests.add(chest);
        notifyDataSetChanged();
    }

    public void clear() {
        mChests.clear();
        notifyDataSetChanged();
    }

    public void remove(Chest chest) {
        mChests.remove(chest);
        notifyDataSetChanged();
    }

    public void remove(int pos) {
        this.remove(mChests.get(pos));
    }

    private void extend(Boolean force) {
        int current_size = mChests.size();
        int pos = getLastOpened();
        if (pos + BUFFER_LENGTH >= current_size) {
            for (int i = current_size; i < current_size + EXTEND_LENGTH; ++i) {
                mChests.add(new Chest(i, mSequence.charAt(i % mSequence.length())));
            }
            notifyDataSetChanged();
        }
    }

    public void open(Chest chest) {
        if (chest.getStatus() != Chest.Status.OPENED) {
            chest.setStatus(Chest.Status.OPENED);
            int pos = chest.getIndex();
            skip(pos - 1);
            extend(false);
            notifyDataSetChanged();
            if (mLastOpened < pos) {
                mLastOpened = pos;
            }
        }
    }

    public void open(int pos) {
        this.open(mChests.get(pos));
    }

    public void open(Chest.Type type) {

    }

    public void skip(int pos) {
        if (pos >= 0) {
            Chest chest = mChests.get(pos);
            if (chest != null && chest.getStatus() == Chest.Status.LOCKED) {
                chest.setStatus(Chest.Status.SKIPPED);
                skip(pos - 1);
            }
        }
    }

    public void lock(Chest chest) {
        if (chest.getStatus() == Chest.Status.OPENED) {
            Chest nextChest = mChests.get(chest.getIndex() + 1);
            if (nextChest.getStatus() == Chest.Status.LOCKED) {
                chest.setStatus(Chest.Status.LOCKED);
                mLastOpened = restore(chest.getIndex() - 1);
            } else {
                chest.setStatus(Chest.Status.SKIPPED);
            }
            notifyDataSetChanged();
        }
    }

    public void lock(int pos) {
        this.lock(mChests.get(pos));
    }

    public int restore(int pos){
        if (pos >= 0) {
            Chest chest = mChests.get(pos);
            if (chest != null && chest.getStatus() == Chest.Status.SKIPPED) {
                chest.setStatus(Chest.Status.LOCKED);
                return restore(pos - 1);
            }
        }
        return pos;
    }

    public int getLastOpened() {
        return mLastOpened;
    }

    @Override
    public int getCount() {
        return mChests.size();
    }

    @Override
    public Object getItem(int pos) {
        return mChests.get(pos);
    }

    public List<Chest> getItems() {
        return mChests;
    }

    @Override
    public long getItemId(int pos) {
        return mChests.get(pos).getIndex();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Chest chest = (Chest) getItem(position);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        final ImageView imageView = (ImageView) layoutInflater.inflate(
                R.layout.view_chest, parent, false);

        loadImage(imageView, chest);

        return imageView;
    }

    private void loadImage(ImageView imageView, Chest chest) {
        imageView.setImageResource(chest.getThumb());
        switch (chest.getStatus()) {
            case LOCKED:
                imageView.setImageAlpha(191);
                break;
            case SKIPPED:
                imageView.setImageAlpha(127);
                break;
            case OPENED:
                imageView.setImageAlpha(255);
                break;
            default:
                imageView.setImageAlpha(255);
        }
    }
}
