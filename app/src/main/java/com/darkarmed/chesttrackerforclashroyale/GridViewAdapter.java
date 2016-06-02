package com.darkarmed.chesttrackerforclashroyale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xu on 5/18/16.
 */
public class GridViewAdapter extends BaseAdapter {
    private final Context mContext;
    private List<Chest> mChests = new ArrayList<>();
    private String mSequence;
    private int mLastOpened = 0;
    private final int BUFFER_LENGTH = 12;
    private final int EXTEND_LENGTH = 40;

    public GridViewAdapter(Context context, List<Chest> chests) {
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

        final ImageButton imageButton = (ImageButton) layoutInflater.inflate(
                R.layout.chest_button, parent, false);

        loadImage(imageButton, chest);

        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        imageButton.setScaleX(0.9f);
                        imageButton.setScaleY(0.9f);
                        break;
                    case MotionEvent.ACTION_UP:
                        imageButton.setScaleX(1f);
                        imageButton.setScaleY(1f);
                        break;
                    default:
                }
                return false;
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(chest);
            }
        });

        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lock(chest);
                return false;
            }
        });

        return imageButton;
    }

    private void loadImage(ImageButton imageButton, Chest chest) {
        imageButton.setImageResource(chest.getThumb());
        switch (chest.getStatus()) {
            case LOCKED:
                imageButton.setImageAlpha(191);
                break;
            case SKIPPED:
                imageButton.setImageAlpha(127);
                break;
            case OPENED:
                imageButton.setImageAlpha(255);
                break;
            default:
                imageButton.setImageAlpha(255);
        }
    }
}
