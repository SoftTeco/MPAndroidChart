
package com.github.mikephil.charting.data;

import android.graphics.Color;

import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

import java.util.List;

/**
 * Baseclass of all DataSets for Bar-, Line-, Scatter- and CandleStickChart.
 *
 * @author Philipp Jahoda
 * Modifications copyright (C) 2023 SoftTeco LLC
 */
public abstract class BarLineScatterCandleBubbleDataSet<T extends Entry>
        extends DataSet<T>
        implements IBarLineScatterCandleBubbleDataSet<T> {

    /**
     * default highlight color
     */
    protected int mHighLightColor = Color.rgb(255, 187, 115);

    /**
     * default active highlight color
     */
    protected int mActiveHighLightColor = Color.RED;

    public BarLineScatterCandleBubbleDataSet(List<T> yVals, String label) {
        super(yVals, label);
    }

    /**
     * Sets the color that is used for drawing the highlight indicators. Dont
     * forget to resolve the color using getResources().getColor(...) or
     * Color.rgb(...).
     *
     * @param color
     */
    public void setHighLightColor(int color) {
        mHighLightColor = color;
    }

    /**
     * Sets the color of the active vertical line to highlight
     * a section on the chart
     *
     * @param color
     */
    public void setActiveHighLightColorForSection(int color) {
        mActiveHighLightColor = color;
    }

    @Override
    public int getHighLightColor() {
        return mHighLightColor;
    }

    /**
     * Returns the color of the active vertical line to highlight
     * a section on the chart
     *
     * @return
     */
    @Override
    public int getActiveHighLightColorForSection() {
        return mActiveHighLightColor;
    }

    protected void copy(BarLineScatterCandleBubbleDataSet barLineScatterCandleBubbleDataSet) {
        super.copy(barLineScatterCandleBubbleDataSet);
        barLineScatterCandleBubbleDataSet.mHighLightColor = mHighLightColor;
    }
}
