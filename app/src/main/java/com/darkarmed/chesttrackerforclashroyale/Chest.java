package com.darkarmed.chesttrackerforclashroyale;

import java.util.Date;

/**
 * Created by Xu on 5/20/16.
 */
public class Chest {

    public enum Type {
        SILVER, GOLDEN, GIANT, MAGICAL, SUPER_MAGICAL
    }
    public enum Status {
        LOCKED, SKIPPED, OPENED
    }

    private Integer mIndex = 0;

    private Type mType = Type.SILVER;
    private Status mStatus = Status.LOCKED;

    private Integer mThumb;
    private Integer mThumbLocked;

    private Date mDate = new Date();

    Chest(Integer index, Type type, Status status) {
        this.mIndex = index;
        this.mType = type;
        this.mStatus = status;
    }

    Chest(Integer index, char c) {
        this.mIndex = index;
        switch (c) {
            case 's':
                this.mType = Chest.Type.SILVER;
                break;
            case 'g':
                this.mType = Chest.Type.GOLDEN;
                break;
            case 'G':
                this.mType = Chest.Type.GIANT;
                break;
            case 'm':
                this.mType = Chest.Type.MAGICAL;
                break;
            case 'M':
                this.mType = Chest.Type.SUPER_MAGICAL;
                break;
            default:
                this.mType = Chest.Type.SILVER;
        }
        this.mStatus = Chest.Status.LOCKED;
    }

    public Integer getIndex() {
        return mIndex;
    }

    public void setIndex(Integer index) {
        mIndex = index;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public Integer getThumb() {
        loadThumb();
        if (mStatus == Status.LOCKED) {
            return mThumbLocked;
        } else {
            return mThumb;
        }
    }

    private void loadThumb() {
        switch (mType) {
            case SILVER:
                mThumb = R.drawable.silver_chest;
                mThumbLocked = R.drawable.silver_chest_locked;
                break;
            case GOLDEN:
                mThumb = R.drawable.golden_chest;
                mThumbLocked = R.drawable.golden_chest_locked;
                break;
            case GIANT:
                mThumb = R.drawable.giant_chest;
                mThumbLocked = R.drawable.giant_chest_locked;
                break;
            case MAGICAL:
                mThumb = R.drawable.magical_chest;
                mThumbLocked = R.drawable.magical_chest_locked;
                break;
            default:
                mThumb = R.drawable.silver_chest;
                mThumbLocked = R.drawable.silver_chest_locked;
        }
    }
}
