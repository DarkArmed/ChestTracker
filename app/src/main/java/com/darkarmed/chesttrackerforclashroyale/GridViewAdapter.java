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

    private void extend() {
        int current_size =mChests.size();
        for (int i = current_size; i < current_size + EXTEND_LENGTH; ++i) {
            mChests.add(new Chest(i, mSequence.charAt(i % mSequence.length())));
        }
        notifyDataSetChanged();
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
        return pos;
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
                if (chest.getStatus() != Chest.Status.OPENED) {
                    chest.setStatus(Chest.Status.OPENED);
                    loadImage(imageButton, chest);
                    if (chest.getIndex() + BUFFER_LENGTH >= mChests.size()) {
                        extend();
                    }
                }
            }
        });

        imageButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (chest.getStatus() == Chest.Status.OPENED) {
                    chest.setStatus(Chest.Status.LOCKED);
                    loadImage(imageButton, chest);
                }
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
