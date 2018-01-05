package com.simonebakker.simone.addemup.models;

public class Level {
    private int mAmountOfNumbers;
    private int mMinRange;
    private int mMaxRange;
    private int mPointsForCorrect;
    private int mLevelTime;
    private int mNeededPoints;

    public Level(int numberLevel) {
        setVariables(numberLevel);
    }

    /**
     * Sets all the level specific variables depending on which level
     * @param numberLevel: the level, determines all the values
     */
    private void setVariables(int numberLevel) {
        switch (numberLevel) {
            case 1:
                setValues(2, 1, 10, 5, 30000, 20);
                break;
            case 2:
                setValues(2, 1, 10, 5, 30000, 30);
                break;
            case 3:
                setValues(2, 1, 15, 10, 30000, 40);
                break;
            case 4:
                setValues(2, 1, 15, 10, 30000, 60);
                break;
            case 5:
                setValues(2, 1, 20, 15, 30000, 60);
                break;
            case 6:
                setValues(2, 1, 20, 15, 30000, 90);
                break;
            case 7:
                setValues(2, 1, 25, 20, 30000, 100);
                break;
            case 8:
                setValues(2, 1, 25, 20, 30000, 120);
                break;
            case 9:
                setValues(2, 10, 30, 25, 30000, 125);
                break;
            case 10:
                setValues(2, 10, 30, 25, 30000, 150);
                break;
            case 11:
                setValues(2, 15, 35, 30, 30000, 150);
                break;
            case 12:
                setValues(2, 15, 35, 30, 30000, 180);
                break;
            case 13:
                setValues(3, 1, 10, 35, 60000, 280);
                break;
            case 14:
                setValues(3, 1, 10, 35, 60000, 315);
                break;
            case 15:
                setValues(3, 1, 15, 40, 60000, 320);
                break;
            case 16:
                setValues(3, 1, 15, 40, 60000, 360);
                break;
            case 17:
                setValues(3, 1, 20, 45, 60000, 360);
                break;
            case 18:
                setValues(3, 1, 20, 45, 60000, 405);
                break;
            case 19:
                setValues(3, 1, 25, 50, 60000, 400);
                break;
            case 20:
                setValues(3, 1, 25, 50, 60000, 450);
                break;
            case 21:
                setValues(3, 10, 30, 55, 60000, 440);
                break;
            case 22:
                setValues(3, 10, 30, 55, 60000, 495);
                break;
            case 23:
                setValues(3, 15, 35, 60, 60000, 480);
                break;
            case 24:
                setValues(3, 15, 35, 60, 60000, 540);
                break;
            default:
                // after level 24, the variables don't change anymore
                // except you need more and more points to pass the level
                int neededPoints = (numberLevel - 24) * 35 + 540;
                setValues(3, 15, 35, 60, 30000, neededPoints);
                break;
        }
    }

    /**
     * Sets all the variables
     * @param amountOfNumbers: the amount of numbers that need to be added up to make the goal
     * @param minRange: the lower boundary for the numbers
     * @param maxRange: the higher boundary for the numbers
     * @param pointsForCorrect: the amount of points the user gets for a good answer
     * @param levelTime: the time the level lasts (in seconds)
     * @param neededPoints: the points the user needs to pass the level
     */
    private void setValues(int amountOfNumbers, int minRange, int maxRange, int pointsForCorrect, int levelTime, int neededPoints) {
        mAmountOfNumbers = amountOfNumbers;
        mMinRange = minRange;
        mMaxRange = maxRange;
        mPointsForCorrect = pointsForCorrect;
        mLevelTime = levelTime;
        mNeededPoints = neededPoints;
    }

    public int getmAmountOfNumbers() {
        return mAmountOfNumbers;
    }

    public void setmAmountOfNumbers(int mAmountOfNumbers) {
        this.mAmountOfNumbers = mAmountOfNumbers;
    }

    public int getmMinRange() {
        return mMinRange;
    }

    public void setmMinRange(int mMinRange) {
        this.mMinRange = mMinRange;
    }

    public int getmMaxRange() {
        return mMaxRange;
    }

    public void setmMaxRange(int mMaxRange) {
        this.mMaxRange = mMaxRange;
    }

    public int getmPointsForCorrect() {
        return mPointsForCorrect;
    }

    public void setmPointsForCorrect(int mPointsForCorrect) {
        this.mPointsForCorrect = mPointsForCorrect;
    }

    public int getmLevelTime() {
        return mLevelTime;
    }

    public void setmLevelTime(int mLevelTime) {
        this.mLevelTime = mLevelTime;
    }

    public int getmNeededPoints() {
        return mNeededPoints;
    }

    public void setmNeededPoints(int mNeededPoints) {
        this.mNeededPoints = mNeededPoints;
    }
}
