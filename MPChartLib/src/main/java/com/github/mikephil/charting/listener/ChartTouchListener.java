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
    protected void performHighlightSection(Highlight h, int highLightColor, int activeHighLightColor) {
        if (h != null) {
            if (mFirstHighlighted == null) {
                mChart.highlightValue(h, true);
                mFirstHighlighted = h;
            } else if (mSecondHighlighted == null && h.getX() > mFirstHighlighted.getX()) {
                mSecondHighlighted = h;
                mLastLineTapped = mSecondHighlighted;
                mSecondHighlighted.setColor(activeHighLightColor);
                mChart.highlightValues(new Highlight[] {mFirstHighlighted, mSecondHighlighted});
                fillSection();
            } else if (mSecondHighlighted == null && h.getX() == mFirstHighlighted.getX()) {
                mSecondHighlighted = h;
                mSecondHighlighted.setX(mSecondHighlighted.getX() + 1);
                mLastLineTapped = mSecondHighlighted;
                mSecondHighlighted.setColor(activeHighLightColor);
                mChart.highlightValues(new Highlight[] {mFirstHighlighted, mSecondHighlighted});
                fillSection();
            } else if (mSecondHighlighted == null && h.getX() < mFirstHighlighted.getX()) {
                mSecondHighlighted = mFirstHighlighted;
                mLastLineTapped = h;
                mFirstHighlighted = h;
                mFirstHighlighted.setColor(activeHighLightColor);
                mChart.highlightValues(new Highlight[] {mFirstHighlighted, mSecondHighlighted});
                fillSection();
            } else if (h.equalTo(mFirstHighlighted)) {
                mLastLineTapped = h;
                mFirstHighlighted.setColor(activeHighLightColor);
                mSecondHighlighted.setColor(highLightColor);
                mChart.highlightValues(new Highlight[] {mFirstHighlighted, mSecondHighlighted});
            } else if (h.equalTo(mSecondHighlighted)) {
                mLastLineTapped = h;
                mSecondHighlighted.setColor(activeHighLightColor);
                mFirstHighlighted.setColor(highLightColor);
                mChart.highlightValues(new Highlight[] {mFirstHighlighted, mSecondHighlighted});
            } else if (mLastLineTapped.equalTo(mFirstHighlighted) && h.getX() < mSecondHighlighted.getX()) {
                mFirstHighlighted = h;
                mLastLineTapped = h;
                mFirstHighlighted.setColor(activeHighLightColor);
                mChart.highlightValues(new Highlight[] {mFirstHighlighted, mSecondHighlighted});
                fillSection();
            } else if (mLastLineTapped.equalTo(mSecondHighlighted) && h.getX() > mFirstHighlighted.getX()) {
                mSecondHighlighted = h;
                mLastLineTapped = h;
                mSecondHighlighted.setColor(activeHighLightColor);
                mChart.highlightValues(new Highlight[]{mFirstHighlighted, mSecondHighlighted});
                fillSection();
            }
        }
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
