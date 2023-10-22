package com.github.mikephil.charting.listener;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

/**
 * Created by philipp on 12/06/15.
 * Modifications copyright (C) 2023 SoftTeco LLC
 */
public abstract class ChartTouchListener<T extends Chart<?>> extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

    public enum ChartGesture {
        NONE, DRAG, X_ZOOM, Y_ZOOM, PINCH_ZOOM, ROTATE, SINGLE_TAP, DOUBLE_TAP, LONG_PRESS, FLING
    }

    /**
     * the last touch gesture that has been performed
     **/
    protected ChartGesture mLastGesture = ChartGesture.NONE;

    // states
    protected static final int NONE = 0;
    protected static final int DRAG = 1;
    protected static final int X_ZOOM = 2;
    protected static final int Y_ZOOM = 3;
    protected static final int PINCH_ZOOM = 4;
    protected static final int POST_ZOOM = 5;
    protected static final int ROTATE = 6;

    /**
     * integer field that holds the current touch-state
     */
    protected int mTouchMode = NONE;

    /**
     * the first highlighted object (via touch)
     */
    protected Highlight mFirstHighlighted;

    /**
     * the second highlighted object (via touch)
     */
    protected Highlight mSecondHighlighted;

    /**
     * the last highlighted object (via touch)
     */
    protected Highlight mLastLineTapped;

    /**
     * Is the down time of motion even set
     * Default false
     */
    protected boolean isDownTimeSet = false;

    /**
     * the gesturedetector used for detecting taps and longpresses, ...
     */
    protected GestureDetector mGestureDetector;

    /**
     * the chart the listener represents
     */
    protected T mChart;

    public ChartTouchListener(T chart) {
        this.mChart = chart;

        mGestureDetector = new GestureDetector(chart.getContext(), this);
    }

    /**
     * Calls the OnChartGestureListener to do the start callback
     *
     * @param me
     */
    public void startAction(MotionEvent me) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null)
            l.onChartGestureStart(me, mLastGesture);
    }

    /**
     * Calls the OnChartGestureListener to do the end callback
     *
     * @param me
     */
    public void endAction(MotionEvent me) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null)
            l.onChartGestureEnd(me, mLastGesture);
    }

    /**
     * Sets the last value that was first highlighted via touch.
     *
     * @param high
     */
    public void setFirstHighlighted(Highlight high) {
        mFirstHighlighted = high;
    }

    /**
     * Sets the last value that was second highlighted via touch.
     *
     * @param high
     */
    public void setSecondHighlighted(Highlight high) {
        mSecondHighlighted = high;
    }

    /**
     * returns the touch mode the listener is currently in
     *
     * @return
     */
    public int getTouchMode() {
        return mTouchMode;
    }

    /**
     * Returns the last gesture that has been performed on the chart.
     *
     * @return
     */
    public ChartGesture getLastGesture() {
        return mLastGesture;
    }


    /**
     * Perform a highlight operation.
     *
     * @param e
     */
    protected void performHighlight(Highlight h, MotionEvent e) {

        if (h == null || h.equalTo(mFirstHighlighted)) {
            mChart.highlightValue(null, true);
            mFirstHighlighted = null;
        } else {
            mChart.highlightValue(h, true);
            mFirstHighlighted = h;
        }
    }

    /**
     * Perform a highlight section operation.
     *
     * @param h
     * @param highLightColor
     * @param activeHighLightColor
     */
    protected void performHighlightSection(Highlight h, int highLightColor, int activeHighLightColor, boolean isSingleTap) {
        if (h == null) {
            return;
        }
        if (mFirstHighlighted == null) {
            mChart.highlightValue(h, true);
            mFirstHighlighted = h;
            return;
        }
        if (mSecondHighlighted == null) {
            if (h.getX() > mFirstHighlighted.getX()) {
                setSecondHighlight(h, activeHighLightColor);
            } else if (h.getX() == mFirstHighlighted.getX()) {
                mSecondHighlighted = h;
                mSecondHighlighted.setX(mSecondHighlighted.getX() + 1);
                mLastLineTapped = mSecondHighlighted;
                mSecondHighlighted.setColor(activeHighLightColor);
            } else {
                mSecondHighlighted = mFirstHighlighted;
                setFirstHighlight(h, activeHighLightColor);
            }
            mChart.highlightValues(new Highlight[]{mFirstHighlighted, mSecondHighlighted});
            fillSection();
            return;
        }
        if (h.isTappedOnTheLineWithInaccuracy(mFirstHighlighted)) {
            if (!isSingleTap && h.getDownTime() != mSecondHighlighted.getDownTime()) {
                mLastLineTapped = h;
                mFirstHighlighted.setColor(activeHighLightColor);
                mSecondHighlighted.setColor(highLightColor);
            } else if (isSingleTap) {
                mLastLineTapped = h;
                mFirstHighlighted.setColor(activeHighLightColor);
                mSecondHighlighted.setColor(highLightColor);
            }
        } else if (h.isTappedOnTheLineWithInaccuracy(mSecondHighlighted) ) {
            if (!isSingleTap && h.getDownTime() != mFirstHighlighted.getDownTime()) {
                mLastLineTapped = h;
                mSecondHighlighted.setColor(activeHighLightColor);
                mFirstHighlighted.setColor(highLightColor);
            } else if (isSingleTap) {
                mLastLineTapped = h;
                mSecondHighlighted.setColor(activeHighLightColor);
                mFirstHighlighted.setColor(highLightColor);
            }
        } else if (mLastLineTapped.isTappedOnTheLineWithInaccuracy(mFirstHighlighted) && h.getX() < mSecondHighlighted.getX() ) {
            setFirstHighlight(h, activeHighLightColor);
        } else if (mLastLineTapped.isTappedOnTheLineWithInaccuracy(mSecondHighlighted) && h.getX() > mFirstHighlighted.getX() ) {
            setSecondHighlight(h, activeHighLightColor);
        }
        mChart.highlightValues(new Highlight[]{mFirstHighlighted, mSecondHighlighted});
        fillSection();
    }

    /**
     * Sets thr first Highlight line object
     *
     * @param h
     * @param color
     */
    private void setFirstHighlight(Highlight h, int color) {
        mFirstHighlighted = h;
        mLastLineTapped = h;
        mFirstHighlighted.setColor(color);
    }

    /**
     * Sets thr second Highlight line object
     *
     * @param h
     * @param color
     */
    private void setSecondHighlight(Highlight h, int color) {
        mSecondHighlighted = h;
        mLastLineTapped = h;
        mSecondHighlighted.setColor(color);
    }

    protected void fillSection() {
        if (mFirstHighlighted != null && mSecondHighlighted != null) {
            List<ILineDataSet> dataSets = (List<ILineDataSet>) mChart.getData().getDataSets();
            int filledStartIndex = (int) (mFirstHighlighted.getX() - mChart.getXAxis().mAxisMinimum);
            int filledEndIndex = (int) (mSecondHighlighted.getX() - mChart.getXAxis().mAxisMinimum);
            for (ILineDataSet set: dataSets) {
                set.setDrawFilledSection(filledStartIndex, (int) (filledEndIndex));
            }
        }
    }

    /**
     * returns the distance between two points
     *
     * @param eventX
     * @param startX
     * @param eventY
     * @param startY
     * @return
     */
    protected static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
